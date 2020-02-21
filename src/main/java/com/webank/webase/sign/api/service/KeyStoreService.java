/*
 * Copyright 2014-2019 the original author or authors.
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
import com.webank.webase.sign.pojo.bo.KeyStoreInfo;
import com.webank.webase.sign.util.GmUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.utils.Numeric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.util.AesUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * KeyStoreService.
 */
@Slf4j
@Service
public class KeyStoreService {
    @Autowired
    private AesUtils aesUtils;

    static final int PUBLIC_KEY_LENGTH_IN_HEX = 128;
    static final int PRIVATE_KEY_LENGTH_IN_HEX = 16;

    /**
     * get KeyStoreInfo by privateKey.
     */
    public KeyStoreInfo getKeyStoreFromPrivateKey(String privateKey) throws BaseException {
        if (StringUtils.isBlank(privateKey)) {
            log.error("fail getKeyStoreFromPrivateKey. private key is null");
            throw new BaseException(CodeMessageEnums.PRIVATEKEY_IS_NULL);
        }

        if(!isValidPrivateKey(privateKey)){
            log.error("fail getKeyStoreFromPrivateKey. private key format error");
            throw new BaseException(CodeMessageEnums.PRIVATEKEY_FORMAT_ERROR);
        }

//        ECKeyPair keyPair = GenCredential.create(Numeric.toBigInt(privateKey));
        // support guomi
        ECKeyPair keyPair = GmUtils.createKeyPair(privateKey);
        return keyPair2KeyStoreInfo(keyPair);
    }


    /**
     * getKey.
     */
    public KeyStoreInfo newKey() throws BaseException {
        try {
            // support guomi TODO upgrade in web3sdk 2.1.3+
            ECKeyPair keyPair = GmUtils.createKeyPair();
            return keyPair2KeyStoreInfo(keyPair);
        } catch (Exception e) {
            log.error("createEcKeyPair fail.", e);
            throw new BaseException(CodeMessageEnums.SYSTEM_ERROR);
        }
    }


    /**
     * keyPair to keyStoreInfo.
     */
    private KeyStoreInfo keyPair2KeyStoreInfo(ECKeyPair keyPair) {
        String publicKey = Numeric
                .toHexStringWithPrefixZeroPadded(keyPair.getPublicKey(), PUBLIC_KEY_LENGTH_IN_HEX);
        String privateKey = Numeric.toHexStringNoPrefix(keyPair.getPrivateKey());
        String address = "0x" + Keys.getAddress(keyPair.getPublicKey());
        log.debug("publicKey:{} privateKey:{} address:{}", publicKey, privateKey, address);
        KeyStoreInfo keyStoreInfo = new KeyStoreInfo();
        keyStoreInfo.setPublicKey(publicKey);
        keyStoreInfo.setPrivateKey(aesUtils.aesEncrypt(privateKey));
        keyStoreInfo.setAddress(address);
        return keyStoreInfo;
    }

    private static boolean isValidPrivateKey(String privateKey) {
        String cleanPrivateKey = Numeric.cleanHexPrefix(privateKey);
        return cleanPrivateKey.length() == PRIVATE_KEY_LENGTH_IN_HEX;
    }

    @Cacheable(cacheNames = "getPrivatekey")
    public  Credentials getCredentioan(String privateKey) {
        return   GenCredential.create(privateKey);
    }

}
