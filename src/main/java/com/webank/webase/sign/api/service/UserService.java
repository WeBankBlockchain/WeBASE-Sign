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

import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webank.webase.sign.api.dao.UserDao;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.bo.KeyStoreInfo;
import com.webank.webase.sign.pojo.po.UserInfoPo;
import com.webank.webase.sign.pojo.vo.RspUserInfoVo;
import com.webank.webase.sign.util.AesUtils;
import lombok.extern.slf4j.Slf4j;

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
     */
    public RspUserInfoVo newUser() throws BaseException {
        log.info("start addUser");

        // get keyStoreInfo
        KeyStoreInfo keyStoreInfo = keyStoreService.newKey();

        //save user.
        UserInfoPo userInfoPo = new UserInfoPo();
        BeanUtils.copyProperties(keyStoreInfo, userInfoPo);

        RspUserInfoVo rspUserInfoVo = saveUser(userInfoPo);
        log.info("end addUser");
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
        rspUserInfoVo.setPrivateKey(aesUtils.aesDecrypt(userInfoPo.getPrivateKey()));

        return rspUserInfoVo;
    }


    /**
     * query user by groupId and address.
     */
    public UserInfoPo findByUserId(Integer userId) throws BaseException {
        log.info("start findByUserId. userId:{}", userId);
        UserInfoPo user = userDao.findUser(userId);
        Optional.ofNullable(user)
            .ifPresent(u -> u.setPrivateKey(aesUtils.aesDecrypt(u.getPrivateKey())));
        log.info("end findByUserId. userId:{}", userId);
        return user;
    }
}
