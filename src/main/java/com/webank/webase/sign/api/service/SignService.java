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

import com.webank.webase.sign.constant.ConstantProperties;
import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.po.UserInfoPo;
import com.webank.webase.sign.pojo.vo.ReqEncodeInfoVo;
import com.webank.webase.sign.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Sign;
import org.fisco.bcos.web3j.crypto.Sign.SignatureData;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
import org.fisco.bcos.web3j.protocol.core.Request;
import org.fisco.bcos.web3j.utils.ByteUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

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


    /**
     * add sign.
     *
     * @param req parameter
     */
    public String sign(ReqEncodeInfoVo req) throws BaseException {
        Integer userId = req.getUserId();
        log.debug("start sign. userId:{}", userId);
        // check user name not exist.

        Instant startTime = Instant.now();

        UserInfoPo userRow = userService.findByUserId(userId);

        log.debug("end query db time: {}", Duration.between(startTime, Instant.now()).toMillis());

        if (Objects.isNull(userRow)) {
            log.warn("fail sign, user not exists. userId:{}", userId);
            throw new BaseException(CodeMessageEnums.USER_IS_NOT_EXISTS);
        }

        // signature
        Instant startTime1 = Instant.now();
        Credentials credentials = GenCredential.create(userRow.getPrivateKey());
        log.info(" create key cost time: {}", Duration.between(startTime1, Instant.now()).toMillis());

        byte[] encodedData = ByteUtil.hexStringToBytes(req.getEncodedDataStr());
        Instant startTime2 = Instant.now();

        SignatureData signatureData = Sign.getSignInterface().signMessage(
                encodedData, credentials.getEcKeyPair());
        log.info("end sign duration:{}", Duration.between(startTime2, Instant.now()).toMillis());
        String signDataStr = CommonUtils.signatureDataToString(signatureData);

        return signDataStr;
    }


}
