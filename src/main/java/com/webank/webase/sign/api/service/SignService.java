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
package com.webank.webase.sign.api.service;

import java.util.Objects;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Sign;
import org.fisco.bcos.web3j.crypto.Sign.SignatureData;
import org.fisco.bcos.web3j.crypto.gm.GenCredential;
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


    /**
     * add sign.
     *
     * @param req parameter
     */
    public String sign(ReqEncodeInfoVo req) throws BaseException {
        Integer userId = req.getUserId();
        log.info("start sign. userId:{}", userId);
        // check user name not exist.
        UserInfoPo userRow = userService.findByUserId(req.getUserId());
        if (Objects.isNull(userRow)) {
            log.warn("fail sign, user not exists. userId:{}", userId);
            throw new BaseException(CodeMessageEnums.USER_IS_NOT_EXISTS);
        }

        // signature
        Credentials credentials = GenCredential.create(userRow.getPrivateKey());
        byte[] encodedData = ByteUtil.hexStringToBytes(req.getEncodedDataStr());
        SignatureData signatureData = Sign.getSignInterface().signMessage(
                encodedData, credentials.getEcKeyPair());
        String signDataStr = CommonUtils.signatureDataToString(signatureData);
        log.info("start sign. userId:{}", userId);
        return signDataStr;
    }
}
