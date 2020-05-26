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
import com.webank.webase.sign.pojo.bo.KeyStoreInfo;
import com.webank.webase.sign.util.AddressUtils;
import com.webank.webase.sign.util.KeyPairUtils;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.ECKeyPair;

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
    @Autowired
    private KeyPairUtils keyPairUtils;
    @Autowired
    private AddressUtils addressUtils;

    static final int PUBLIC_KEY_LENGTH_IN_HEX = 128;

    /**
     * get KeyStoreInfo by privateKey.
     * @param encryptType 1: guomi, 0: standard
     */
    public KeyStoreInfo getKeyStoreFromPrivateKey(String privateKeyRaw, int encryptType) throws BaseException {
        if (StringUtils.isBlank(privateKeyRaw)) {
            log.error("fail getKeyStoreFromPrivateKey. private key is null");
            throw new BaseException(CodeMessageEnums.PRIVATEKEY_IS_NULL);
        }

        // support guomi. v1.3.0+: create by type
        ECKeyPair keyPair = keyPairUtils.createKeyPairByType(privateKeyRaw, encryptType);
        return keyPair2KeyStoreInfo(keyPair, encryptType);
    }


    /**
     * get Key by encrypt type
     * @param encryptType 1: guomi, 0: standard
     */
    public KeyStoreInfo newKeyByType(int encryptType) throws BaseException {
        try {
            // support guomi
            ECKeyPair keyPair = keyPairUtils.createKeyPairByType(encryptType);
            return keyPair2KeyStoreInfo(keyPair, encryptType);
        } catch (Exception e) {
            log.error("createEcKeyPair fail.", e);
            throw new BaseException(CodeMessageEnums.SYSTEM_ERROR);
        }
    }


    /**
     * keyPair to keyStoreInfo.
     * 1.3.0 use AddressUtil to get address instead of using Keys.java
     * @param encryptType 1: guomi, 0: standard
     */
    private KeyStoreInfo keyPair2KeyStoreInfo(ECKeyPair keyPair, int encryptType) {
        String publicKey = Numeric
                .toHexStringWithPrefixZeroPadded(keyPair.getPublicKey(), PUBLIC_KEY_LENGTH_IN_HEX);
        String privateKey = Numeric.toHexStringNoPrefix(keyPair.getPrivateKey());
        String address = "0x" + addressUtils.getAddressByType(keyPair.getPublicKey(), encryptType);
        log.debug("publicKey:{} privateKey:{} address:{}", publicKey, privateKey, address);
        KeyStoreInfo keyStoreInfo = new KeyStoreInfo();
        keyStoreInfo.setPublicKey(publicKey);
        keyStoreInfo.setPrivateKey(aesUtils.aesEncrypt(privateKey));
        keyStoreInfo.setAddress(address);
        return keyStoreInfo;
    }


    @Cacheable(cacheNames = "getPrivatekey")
    public  Credentials getCredentioan(String privateKey) {
        return   GenCredential.create(privateKey);
    }

}
