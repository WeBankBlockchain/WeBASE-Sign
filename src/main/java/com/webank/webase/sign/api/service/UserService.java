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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webank.webase.sign.api.dao.UserDao;
import com.webank.webase.sign.enums.CodeMessageEnums;
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
     * add user by encrypt type
     */
    public RspUserInfoVo newUser(int encryptType) throws BaseException {
        log.info("start addUser encryptType:{}", encryptType);

        // get keyStoreInfo
        KeyStoreInfo keyStoreInfo = keyStoreService.newKeyByType(encryptType);

        //save user.
        UserInfoPo userInfoPo = new UserInfoPo();
        BeanUtils.copyProperties(keyStoreInfo, userInfoPo);
        userInfoPo.setEncryptType(encryptType);
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
//        rspUserInfoVo.setPrivateKey(aesUtils.aesDecrypt(userInfoPo.getPrivateKey()));

        return rspUserInfoVo;
    }

    /**
     * query user by userId.
     */
    public UserInfoPo findByUserId(Integer userId) throws BaseException {
        log.info("start findByUserId. userId:{}", userId);
        UserInfoPo user = userDao.findUser(userId);
        if (Objects.isNull(user)) {
            log.warn("fail findByUserId, user not exists. userId:{}", userId);
            throw new BaseException(CodeMessageEnums.USER_IS_NOT_EXISTS);
        }
        Optional.ofNullable(user)
            .ifPresent(u -> u.setPrivateKey(aesUtils.aesDecrypt(u.getPrivateKey())));
        log.info("end findByUserId. userId:{}", userId);
        return user;
    }

    public UserInfoPo findByAddress(String address) throws BaseException {
        log.info("start findByUserId. address:{}", address);
        UserInfoPo user = userDao.findUserByAddress(address);
        if (Objects.isNull(user)) {
            log.warn("fail findByUserId, user not exists. address:{}", address);
            throw new BaseException(CodeMessageEnums.USER_IS_NOT_EXISTS);
        }
        Optional.ofNullable(user)
                .ifPresent(u -> u.setPrivateKey(aesUtils.aesDecrypt(u.getPrivateKey())));
        log.info("end findByUserId. address:{}", address);
        return user;
    }

    /**
     * query user list.
     * @param encryptType 1: guomi, 0: standard
     */
    public List<RspUserInfoVo> findUserList(int encryptType) {
        log.info("start findUserList.");
        List<UserInfoPo> users = userDao.findUserList(encryptType);
        List<RspUserInfoVo> rspUserInfoVos = new ArrayList<>();
        for (UserInfoPo user : users) {
            RspUserInfoVo rspUserInfoVo = new RspUserInfoVo();
            BeanUtils.copyProperties(user, rspUserInfoVo);
//            rspUserInfoVo.setPrivateKey(aesUtils.aesDecrypt(user.getPrivateKey()));
            rspUserInfoVos.add(rspUserInfoVo);
        }
        return rspUserInfoVos;
    }
}
