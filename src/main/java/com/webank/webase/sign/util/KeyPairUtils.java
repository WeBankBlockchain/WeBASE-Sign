/**
 * Copyright 2014-2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.webase.sign.util;

import com.webank.webase.sign.enums.EncryptTypes;
import lombok.extern.log4j.Log4j2;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.crypto.gm.sm2.crypto.asymmetric.SM2KeyGenerator;
import org.fisco.bcos.web3j.crypto.gm.sm2.crypto.asymmetric.SM2PrivateKey;
import org.fisco.bcos.web3j.crypto.gm.sm2.crypto.asymmetric.SM2PublicKey;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.KeyPair;

/**
 * create key pair by encrypt type
 * @author marsli
 */
@Log4j2
@Component
public class KeyPairUtils {

    /**
     * get Credentials from privateKey by encrypt type
     * @param privateKey
     * @param encryptType 1: guomi, 0: standard
     * @return Credentials
     */
    @Cacheable(cacheNames = "getCredentials")
    public Credentials create(String privateKey, int encryptType) {
        try {
            ECKeyPair keyPair = createKeyPairByType(privateKey, encryptType);
            if (keyPair == null) {
                return null;
            }
            Credentials credentials = Credentials.create(keyPair);
            return credentials;
        } catch (Exception e) {
            log.error("init credential from private key failed, error msg:" + e.getMessage());
            return null;
        }
    }

    /**
     * get Credentials from key pair by encrypt type
     * @param keyPair
     * @param encryptType 1: guomi, 0: standard
     * @return Credentials
     */
    public Credentials create(ECKeyPair keyPair, int encryptType) {
        try {
            ECKeyPair newKeyPair = createKeyPairByType(keyPair.getPrivateKey().toString(16), encryptType);
            if (newKeyPair == null) {
                return null;
            }
            Credentials credentials = Credentials.create(newKeyPair);
            return credentials;
        } catch (Exception e) {
            log.error("KeyPairUtils init credential from private key failed, error msg:" + e.getMessage());
            return null;
        }
    }

    /**
     * create KeyPair by encrypt type from private key
     * @param privateKey string
     * @param encryptType 1: guomi, 0: standard
     * @return ECKeyPair
     */
    public ECKeyPair createKeyPairByType(String privateKey, int encryptType) {
        if (encryptType == EncryptTypes.GUOMI.getValue()) {
            return createGuomiKeyPair(privateKey);
        } else {
            return createECDSAKeyPair(privateKey);
        }
    }

    /**
     * init KeyPair by encrypt type
     * @param encryptType 1: guomi, 0: standard
     * @return ECKeyPair
     */
    public ECKeyPair createKeyPairByType(int encryptType) {
        // use guomi
        if (encryptType == EncryptTypes.GUOMI.getValue()) {
            return GenCredential.createGuomiKeyPair();
        } else {
            return createECDSAKeyPair();
        }
    }

    /**
     * init ecdsa key pair
     * @return ECKeyPair ecdsa
     */
    private ECKeyPair createECDSAKeyPair() {
        try {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            return keyPair;
        } catch (Exception e) {
            log.error("KeyPairUtils create keypair of ECDSA failed, error msg:" + e.getMessage());
            return null;
        }
    }

    /**
     * create guomi keypair from privateKey
     * @param privateKey string
     * @return ECKeyPair guomi
     */
    private ECKeyPair createGuomiKeyPair(String privateKey) {
        SM2KeyGenerator generator = new SM2KeyGenerator();
        final KeyPair keyPairData = generator.generateKeyPair(privateKey);
        if (keyPairData != null) {
            return genEcPairFromKeyPair(keyPairData);
        }
        return null;
    }

    /**
     * get ec key pair from guomi key pair data
     * @param keyPairData common key pair
     * @return ECKeyPair
     */
    private ECKeyPair genEcPairFromKeyPair(KeyPair keyPairData) {
        try {
            SM2PrivateKey vk = (SM2PrivateKey) keyPairData.getPrivate();
            SM2PublicKey pk = (SM2PublicKey) keyPairData.getPublic();
            final byte[] publicKey = pk.getEncoded();
            final byte[] privateKey = vk.getEncoded();

            BigInteger biPublic = new BigInteger(Hex.toHexString(publicKey), 16);
            BigInteger biPrivate = new BigInteger(Hex.toHexString(privateKey), 16);

            ECKeyPair keyPair = new ECKeyPair(biPrivate, biPublic);
            return keyPair;
        } catch (Exception e) {
            log.error("KeyPairUtils create ec_keypair of guomi failed, error msg:" + e.getMessage());
            return null;
        }
    }

    /**
     * create ecdsa keypair from privateKey
     * @param privateKey string
     * @return ECKeyPair ecdsa
     */
    private ECKeyPair createECDSAKeyPair(String privateKey) {
        try {
            BigInteger bigPrivateKey = new BigInteger(privateKey, 16);
            ECKeyPair keyPair = ECKeyPair.create(bigPrivateKey);
            return keyPair;
        } catch (Exception e) {
            log.error("KeyPairUtils create keypair of ECDSA failed, error msg:" + e.getMessage());
            return null;
        }
    }

}
