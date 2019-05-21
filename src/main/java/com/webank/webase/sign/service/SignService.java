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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webank.webase.sign.base.BaseResponse;
import com.webank.webase.sign.base.ConstantCode;
import com.webank.webase.sign.base.ConstantProperties;
import com.webank.webase.sign.base.exception.BaseException;
import com.webank.webase.sign.keystore.KeyStoreInfo;
import com.webank.webase.sign.keystore.KeyStoreService;
import com.webank.webase.sign.util.AesUtils;
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
    private KeyStoreService keyStoreService;
    @Autowired
    private SignMapper signMapper;
	@Autowired
	ConstantProperties properties;
	@Autowired
    private AesUtils aesUtils;
	
	/**
	 * add user.
	 * 
	 * @param req parameter
	 * @return
	 */
	public BaseResponse addUser(ReqAddUser req) throws BaseException {
	    BaseResponse baseRsp = new BaseResponse(ConstantCode.RET_SUCCEED);
	    
	    // check user name
	    String userName = req.getUserName();
	    UserInfoDto userRow =  signMapper.selectUser(userName);
	    if (userRow != null) {
            log.warn("fail addUser. user name:{} is already exists", userName);
            throw new BaseException(ConstantCode.USER_NAME_IS_EXISTS);
        }
	    
	    // get keyStoreInfo
	    KeyStoreInfo keyStoreInfo = keyStoreService.getKey();
	    
	    // add user
	    UserInfoDto userInfoDto = new UserInfoDto();
	    userInfoDto.setUserName(userName);
	    userInfoDto.setAddress(keyStoreInfo.getAddress());
	    userInfoDto.setPublicKey(keyStoreInfo.getPublicKey());
	    userInfoDto.setPrivateKey(keyStoreInfo.getPrivateKey());
	    userInfoDto.setDescription(req.getDescription());
	    signMapper.insertUserInfo(userInfoDto);
	    
	    // return
	    RspUserInfo rspUserInfo = new RspUserInfo();
	    BeanUtils.copyProperties(userInfoDto, rspUserInfo);
	    baseRsp.setData(rspUserInfo);
	    log.info("addUser end baseRsp:{}", baseRsp);
	    return baseRsp;
	}
	
	/**
	 * get user info.
	 * 
	 * @param userName userName
	 * @return
	 */
	public BaseResponse getUserInfo(String userName) throws BaseException {
	    BaseResponse baseRsp = new BaseResponse(ConstantCode.RET_SUCCEED);
	    
	    // select user
	    UserInfoDto userRow =  signMapper.selectUser(userName);
        if (userRow == null) {
            log.warn("fail getUserInfo. user name:{} does not exist", userName);
            throw new BaseException(ConstantCode.USER_IS_NOT_EXISTS);
        }
	    
	    // return
        RspUserInfo rspUserInfo = new RspUserInfo();
        BeanUtils.copyProperties(userRow, rspUserInfo);
        baseRsp.setData(rspUserInfo);
	    log.info("getUserInfo end baseRsp:{}", baseRsp);
	    return baseRsp;
	}
	
	
	
    /**
     * add sign.
     * 
     * @param req parameter
     * @return
     * @throws BaseException 
     */
    public BaseResponse addSign(ReqEncodeInfo req) throws BaseException {
        BaseResponse baseRsp = new BaseResponse(ConstantCode.RET_SUCCEED);
        log.info("addSign length:{}", req.getEncodedDataStr().length());
        // select user
        String userName = req.getUserName();
        UserInfoDto userRow =  signMapper.selectUser(userName);
        if (userRow == null) {
            log.warn("fail addSign. user name:{} does not exist", userName);
            throw new BaseException(ConstantCode.USER_IS_NOT_EXISTS);
        }
        
        // add signature
        String privateKey = aesUtils.aesDecrypt(userRow.getPrivateKey());
        Credentials credentials = Credentials.create(privateKey);
        byte[] encodedData = req.getEncodedDataStr().getBytes();
        SignatureData signatureData = Sign.getSignInterface().signMessage(
                encodedData, credentials.getEcKeyPair());
        String signDataStr = CommonUtils.signatureDataToString(signatureData);
        
        // return
        RspSignInfo rspSignInfo = new RspSignInfo();
        rspSignInfo.setSignDataStr(signDataStr);
        rspSignInfo.setDesc(req.getDesc());
        baseRsp.setData(rspSignInfo);;
        log.info("addSign end baseRsp:{}", baseRsp);
        return baseRsp;
    }
}
