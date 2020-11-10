/*
 * Copyright 2014-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.webase.sign.api.service;

import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.bo.KeyStoreInfo;
import com.webank.webase.sign.util.AesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.model.CryptoType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * KeyStoreService.
 */
@Slf4j
@Service
public class KeyStoreService {
    @Autowired
    private AesUtils aesUtils;
    @Autowired
    @Qualifier(value = "sm")
    private CryptoSuite smCryptoSuite;
    @Autowired
    @Qualifier(value = "ecdsa")
    private CryptoSuite ecdsaCryptoSuite;

    /**
     * get KeyStoreInfo by privateKey.
     * @param privateKeyRaw hex format
     * @param encryptType 1: guomi, 0: standard
     */
    @Cacheable(cacheNames = "getCredentials")
    public KeyStoreInfo getKeyStoreFromPrivateKey(String privateKeyRaw, int encryptType) throws BaseException {
        if (StringUtils.isBlank(privateKeyRaw)) {
            log.error("fail getKeyStoreFromPrivateKey. private key is null");
            throw new BaseException(CodeMessageEnums.PRIVATEKEY_IS_NULL);
        }

        // support guomi. v1.3.0+: create by type
        CryptoKeyPair cryptoKeyPair = getKeyPairByType(privateKeyRaw, encryptType);
        return keyPair2KeyStoreInfo(cryptoKeyPair);
    }

    /**
     * get Key by encrypt type
     * @param encryptType 1: guomi, 0: standard
     */
    public KeyStoreInfo newKeyByType(int encryptType) throws BaseException {
        try {
            // support guomi
            CryptoKeyPair keyPair = getKeyPairRandom(encryptType);
            return keyPair2KeyStoreInfo(keyPair);
        } catch (Exception e) {
            log.error("createEcKeyPair fail.", e);
            throw new BaseException(CodeMessageEnums.SYSTEM_ERROR);
        }
    }


    /**
     * keyPair to keyStoreInfo.
     */
    private KeyStoreInfo keyPair2KeyStoreInfo(CryptoKeyPair cryptoKeyPair) {
        String publicKey = cryptoKeyPair.getHexPublicKey();
        String privateKey = cryptoKeyPair.getHexPrivateKey();
        String address = cryptoKeyPair.getAddress();
        log.debug("publicKey:{} privateKey:{} address:{}", publicKey, privateKey, address);
        KeyStoreInfo keyStoreInfo = new KeyStoreInfo();
        keyStoreInfo.setPublicKey(publicKey);
        keyStoreInfo.setPrivateKey(aesUtils.aesEncrypt(privateKey));
        keyStoreInfo.setAddress(address);
        return keyStoreInfo;
    }

    public CryptoKeyPair getKeyPairByType(String privateKeyRaw, int encryptType) {
        if (encryptType == CryptoType.SM_TYPE) {
            return smCryptoSuite.createKeyPair(privateKeyRaw);
        } else {
            return ecdsaCryptoSuite.createKeyPair(privateKeyRaw);
        }
    }

    public CryptoKeyPair getKeyPairRandom(int encryptType) {
        if (encryptType == CryptoType.SM_TYPE) {
            return smCryptoSuite.createKeyPair();
        } else {
            return ecdsaCryptoSuite.createKeyPair();
        }
    }

}
