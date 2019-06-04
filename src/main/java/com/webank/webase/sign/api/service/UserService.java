/**
 * Copyright 2014-2019  the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.webank.webase.sign.api.service;

import com.webank.webase.sign.api.dao.UserDao;
import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.bo.KeyStoreInfo;
import com.webank.webase.sign.pojo.po.UserInfoPo;
import com.webank.webase.sign.pojo.vo.ReqNewUserVo;
import com.webank.webase.sign.pojo.vo.RspUserInfoVo;
import com.webank.webase.sign.util.AesUtils;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private AesUtils aesUtils;
    @Autowired
    private KeyStoreService keyStoreService;

    /**
     * add user.
     *
     * @param req parameter
     */
    public RspUserInfoVo newUser(ReqNewUserVo req) throws BaseException {
        log.info("start addUser");
        // check user name not exist.
        userNameNotExistOrThrow(req.getUserName());
        // get keyStoreInfo
        KeyStoreInfo keyStoreInfo = keyStoreService.newKey();

        //save user.
        UserInfoPo userInfoPo = new UserInfoPo();
        BeanUtils.copyProperties(keyStoreInfo, userInfoPo);
        userInfoPo.setUserName(aesUtils.aesEncrypt(req.getUserName()));

        RspUserInfoVo rspUserInfoVo = saveUser(userInfoPo);
        log.info("end addUser");
        return rspUserInfoVo;
    }

    /**
     * import user.
     */
    public RspUserInfoVo importUser(String privateKey, String userName) throws BaseException {
        log.info("start importUser");
        if (StringUtils.isBlank(privateKey)) {
            log.warn("fail importUser. privateKey is blank");
            throw new BaseException(CodeMessageEnums.PRIVATEKEY_IS_NULL);
        }
        // check user name not exist.
        userNameNotExistOrThrow(userName);

        // get keyStoreInfo
        KeyStoreInfo keyStoreInfo = keyStoreService.getKeyStoreFromPrivateKey(privateKey);

        //save user.
        UserInfoPo userInfoPo = new UserInfoPo();
        BeanUtils.copyProperties(keyStoreInfo, userInfoPo);
        userInfoPo.setUserName(aesUtils.aesEncrypt(userName));

        RspUserInfoVo rspUserInfoVo = saveUser(userInfoPo);
        log.info("end importUser");
        return rspUserInfoVo;
    }


    /**
     * save user.
     */
    private RspUserInfoVo saveUser(UserInfoPo userInfoPo) {
        // save user
        userDao.insertUserInfo(userInfoPo);

        // return
        RspUserInfoVo rspUserInfoVo = new RspUserInfoVo();
        BeanUtils.copyProperties(userInfoPo, rspUserInfoVo);

        rspUserInfoVo.setUserName(aesUtils.aesDecrypt(userInfoPo.getUserName()));
        rspUserInfoVo.setPrivateKey(aesUtils.aesDecrypt(userInfoPo.getPrivateKey()));

        return rspUserInfoVo;
    }


    /**
     * get user info.
     *
     * @param userName userName
     */
    public UserInfoPo getUserInfo(String userName) {
        userName = aesUtils.aesEncrypt(userName);
        UserInfoPo user = userDao.findUser(userName);
        if (Objects.isNull(user)) {
            log.info("not found  user info by username:{}", userName);
            return user;
        }
        user.setUserName(userName);
        user.setPrivateKey(aesUtils.aesDecrypt(user.getPrivateKey()));
        return user;
    }


    /**
     * check userName not exist.
     */
    public void userNameNotExistOrThrow(String userName) throws BaseException {
        userName = aesUtils.aesEncrypt(userName);
        if (StringUtils.isBlank(userName)) {
            log.warn("fail userNameNotExistOrThrow. userName is blank");
            throw new BaseException(CodeMessageEnums.USERNAME_IS_NULL);
        }
        // check user name
        UserInfoPo userRow = userDao.findUser(userName);
        if (userRow != null) {
            log.warn("fail userNameNotExistOrThrow. user name:{} is already exists", userName);
            throw new BaseException(CodeMessageEnums.USER_NAME_IS_EXISTS);
        }
    }

    /**
     * check userName  exist.
     */
    public UserInfoPo userNameExistOrThrow(String userName) throws BaseException {
        userName = aesUtils.aesEncrypt(userName);
        if (StringUtils.isBlank(userName)) {
            log.warn("fail userNameExistOrThrow. userName is blank");
            throw new BaseException(CodeMessageEnums.USERNAME_IS_NULL);
        }
        // check user name
        UserInfoPo userRow = userDao.findUser(userName);
        if (Objects.isNull(userRow)) {
            log.warn("fail userNameExistOrThrow. user name:{} is not exists", userName);
            throw new BaseException(CodeMessageEnums.USER_IS_NOT_EXISTS);
        }
        return userRow;
    }
}
