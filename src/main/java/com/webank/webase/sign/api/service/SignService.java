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

import com.webank.webase.sign.constant.ConstantProperties;
import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.po.UserInfoPo;
import com.webank.webase.sign.pojo.vo.ReqEncodeInfoVo;
import com.webank.webase.sign.util.CommonUtils;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Sign;
import org.fisco.bcos.web3j.crypto.Sign.SignatureData;
import org.springframework.beans.factory.annotation.Autowired;
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


    /**
     * add sign.
     *
     * @param req parameter
     */
    public String sign(ReqEncodeInfoVo req) throws BaseException {
        String address = req.getAddress();
        int groupId = req.getGroupId();
        // check user name not exist.
        UserInfoPo userRow = userService.findByAddressAndGroupId(groupId, address);
        if (Objects.isNull(userRow)) {
            log.warn("fail sign, user not exists. group:{} userAddress:{}", groupId, address);
            throw new BaseException(CodeMessageEnums.USER_IS_NOT_EXISTS);
        }

        // signature
        Credentials credentials = Credentials.create(userRow.getPrivateKey());
        byte[] encodedData = req.getEncodedDataStr().getBytes();
        SignatureData signatureData = Sign.getSignInterface().signMessage(
            encodedData, credentials.getEcKeyPair());
        String signDataStr = CommonUtils.signatureDataToString(signatureData);
        return signDataStr;
    }
}
