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

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import com.webank.webase.sign.util.KeyPairUtils;
import com.webank.webase.sign.util.SignUtils;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Sign.SignatureData;
import org.fisco.bcos.web3j.utils.ByteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webank.webase.sign.constant.ConstantProperties;
import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.po.UserInfoPo;
import com.webank.webase.sign.pojo.vo.ReqEncodeInfoVo;
import com.webank.webase.sign.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;

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
    SignUtils signUtils;
    @Autowired
    private KeyPairUtils keyPairUtils;


    /**
     * add sign.
     * @param encryptType 1: guomi, 0: standard(ecdsa)
     * @param req parameter
     */
    public String sign(ReqEncodeInfoVo req, int encryptType) throws BaseException {
        Integer userId = req.getUserId();
        log.info("start sign. userId:{},encryptType:{}", userId, encryptType);
        // check user name not exist.
        UserInfoPo userRow = userService.findByUserId(req.getUserId());
        if (Objects.isNull(userRow)) {
            log.warn("fail sign, user not exists. userId:{}", userId);
            throw new BaseException(CodeMessageEnums.USER_IS_NOT_EXISTS);
        }

        // signature
        Credentials credentials = keyPairUtils.create(userRow.getPrivateKey(), encryptType);
        byte[] encodedData = ByteUtil.hexStringToBytes(req.getEncodedDataStr());
        Instant startTime = Instant.now();
        log.info("start sign. startTime:{}", startTime.toEpochMilli());
        // sign message by type
        SignatureData signatureData = signUtils.signMessageByType(
                encodedData, credentials.getEcKeyPair(), encryptType);
        log.info("end sign duration:{}", Duration.between(startTime, Instant.now()).toMillis());
        String signDataStr = CommonUtils.signatureDataToStringByType(signatureData, encryptType);
        log.info("start sign. userId:{}", userId);
        return signDataStr;
    }
}
