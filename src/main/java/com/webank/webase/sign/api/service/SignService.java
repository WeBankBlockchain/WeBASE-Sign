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


import com.webank.webase.sign.constant.ConstantProperties;
import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.po.UserInfoPo;
import com.webank.webase.sign.pojo.vo.ReqEncodeInfoVo;
import com.webank.webase.sign.util.CommonUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.utils.ByteUtils;
import org.fisco.bcos.sdk.utils.Hex;
import org.fisco.bcos.sdk.utils.exceptions.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * SignService.
 */
@Slf4j
@Service
public class SignService {

    @Autowired
    private UserService userService;
    @Autowired
    ConstantProperties properties;
    @Autowired
    private KeyStoreService keyStoreService;
    @Autowired
    @Qualifier(value = "sm")
    private CryptoSuite smCryptoSuite;
    @Autowired
    @Qualifier(value = "ecdsa")
    private CryptoSuite ecdsaCryptoSuite;

    /**
     * add sign.
     * @param req parameter
     */
    public String sign(ReqEncodeInfoVo req) throws BaseException {
        String signUserId = req.getSignUserId();
        log.info("start sign. signUserId:{}", signUserId);
        Instant startTimeDB = Instant.now();
        // check exist
        UserInfoPo userRow = userService.findBySignUserId(signUserId);
       log.debug("end query db time: {}", Duration.between(startTimeDB, Instant.now()).toMillis());
        // check user name not exist.
        if (Objects.isNull(userRow)) {
            log.warn("fail sign, user not exists. signUserId:{}", signUserId);
            throw new BaseException(CodeMessageEnums.USER_NOT_EXISTS);
        }
        int encryptType = userRow.getEncryptType();
        // signature
        CryptoKeyPair cryptoKeyPair = keyStoreService.getKeyPairByType(userRow.getPrivateKey(), encryptType);
        // make sure hex
        byte[] encodedData;
        try {
            encodedData = ByteUtils.hexStringToBytes(req.getEncodedDataStr());
        } catch (DecoderException e) {
            log.error("hexStringToBytes error: ", e);
            throw new BaseException(CodeMessageEnums.PARAM_ENCODED_DATA_INVALID);

        }
        Instant startTime = Instant.now();
        log.info("start sign. startTime:{}", startTime.toEpochMilli());
        // sign message by type
        SignatureResult signatureResult = signMessageByType(
                encodedData, cryptoKeyPair, encryptType);
        log.info("end sign duration:{}", Duration.between(startTime, Instant.now()).toMillis());
        String signDataStr = CommonUtils.signatureResultToStringByType(signatureResult, encryptType);
        log.info("end sign. signUserId:{}", signUserId);
        return signDataStr;
    }

    public SignatureResult signMessageByType(byte[] message, CryptoKeyPair cryptoKeyPair,
        int encryptType) {
        if (encryptType == CryptoType.SM_TYPE) {
            byte[] messageHash = smCryptoSuite.hash(message);
            log.debug("userRow.messageHash：{},hex:{}", messageHash, Hex.toHexString(messageHash));
            return smCryptoSuite.sign(Hex.toHexString(messageHash), cryptoKeyPair);
        } else {
            byte[] messageHash = ecdsaCryptoSuite.hash(message);
            log.debug("userRow.messageHash：{},hex:{}", messageHash, Hex.toHexString(messageHash));
            return ecdsaCryptoSuite.sign(Hex.toHexString(messageHash), cryptoKeyPair);
        }
    }
}
