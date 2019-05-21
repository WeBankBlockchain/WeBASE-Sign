/*
 * Copyright 2012-2019 the original author or authors.
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
package com.webank.webase.sign.keystore;

import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.Keys;
import org.fisco.bcos.web3j.utils.Numeric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.webank.webase.sign.base.ConstantCode;
import com.webank.webase.sign.base.exception.BaseException;
import com.webank.webase.sign.util.AesUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * KeyStoreService.
 *
 */
@Slf4j
@Service
public class KeyStoreService {
    @Autowired
    private AesUtils aesUtils;
    
    static final int PUBLIC_KEY_LENGTH_IN_HEX = 128;

    /**
     * getKey.
     * 
     * @return
     * @throws BaseException 
     */
    public KeyStoreInfo getKey() throws BaseException {
        try {
            ECKeyPair keyPair = Keys.createEcKeyPair();
            String publicKey = Numeric.toHexStringWithPrefixZeroPadded(keyPair.getPublicKey(),
                    PUBLIC_KEY_LENGTH_IN_HEX);
            String privateKey = Numeric.toHexStringNoPrefix(keyPair.getPrivateKey());
            String address = "0x" + Keys.getAddress(publicKey);

            KeyStoreInfo keyStoreInfo = new KeyStoreInfo();
            keyStoreInfo.setPublicKey(publicKey);
            keyStoreInfo.setPrivateKey(aesUtils.aesEncrypt(privateKey));
            keyStoreInfo.setAddress(address);

            log.info("getKey finish. keyStoreInfo[{}]", JSON.toJSONString(keyStoreInfo));
            return keyStoreInfo;
        } catch (Exception e) {
            log.error("createEcKeyPair fail.");
            throw new BaseException(ConstantCode.SYSTEM_ERROR);
        }
    }
}
