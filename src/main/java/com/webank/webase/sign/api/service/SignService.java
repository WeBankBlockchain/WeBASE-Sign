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
import com.webank.webase.sign.util.AesUtils;
import com.webank.webase.sign.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Sign;
import org.fisco.bcos.web3j.crypto.Sign.SignatureData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SignService.
 * 
 */
@Slf4j
@Service
public class SignService {
    @Autowired
    private UserService userService;
	@Autowired
	ConstantProperties properties;
	@Autowired
    private AesUtils aesUtils;
	

	
	
    /**
     * add sign.
     * 
     * @param req parameter
     * @return
     * @throws BaseException 
     */
    public String sign(ReqEncodeInfoVo req) throws BaseException {
        // select user
        String userName = req.getUserName();
        UserInfoPo userRow =  userService.getUserInfo(userName);

        if (userRow == null) {
            log.warn("fail addSign. user name:{} does not exist", userName);
            throw new BaseException(CodeMessageEnums.USER_IS_NOT_EXISTS);
        }
        
        // signature
        String privateKey = aesUtils.aesDecrypt(userRow.getPrivateKey());
        Credentials credentials = Credentials.create(privateKey);
        byte[] encodedData = req.getEncodedDataStr().getBytes();
        SignatureData signatureData = Sign.getSignInterface().signMessage(
                encodedData, credentials.getEcKeyPair());
        String signDataStr = CommonUtils.signatureDataToString(signatureData);
        return signDataStr;
    }
}
