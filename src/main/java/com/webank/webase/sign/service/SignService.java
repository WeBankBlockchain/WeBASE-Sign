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
package com.webank.webase.sign.service;

import org.fisco.bcos.web3j.crypto.Credentials;
import org.fisco.bcos.web3j.crypto.Sign;
import org.fisco.bcos.web3j.crypto.Sign.SignatureData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webank.webase.sign.base.BaseResponse;
import com.webank.webase.sign.base.ConstantCode;
import com.webank.webase.sign.base.ConstantProperties;
import com.webank.webase.sign.util.CommonUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * SignService.
 * 
 */
@Slf4j
@Service
public class SignService {
	@Autowired
	ConstantProperties properties;
	
    /**
     * add sign.
     * 
     * @param req parameter
     * @return
     */
    public BaseResponse add(EncodeInfo req) {
        BaseResponse baseRsp = new BaseResponse(ConstantCode.RET_SUCCEED);
        
        // add signature,privateKey is your own privateKey
        String privateKey = properties.getPrivateKey();
        Credentials credentials = Credentials.create(privateKey);
        byte[] encodedData = req.getEncodedDataStr().getBytes();
        SignatureData signatureData = Sign.getSignInterface().signMessage(
                encodedData, credentials.getEcKeyPair());
        String signDataStr = CommonUtils.signatureDataToString(signatureData);
        
        // return
        SignInfo signInfo = new SignInfo();
        signInfo.setSignDataStr(signDataStr);
        signInfo.setDesc(req.getDesc());
        baseRsp.setData(signInfo);;
        log.info("add end baseRsp:{}", baseRsp);
        return baseRsp;
    }
}
