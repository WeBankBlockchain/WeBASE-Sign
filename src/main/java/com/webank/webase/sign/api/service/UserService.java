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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.webank.webase.sign.pojo.bo.UserParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    public RspUserInfoVo newUser(String signUserId, String appId, int encryptType) throws BaseException {
        log.info("start addUser signUserId:{},appId:{},encryptType:{}",
                signUserId, appId, encryptType);
        // check user uuid exist
        UserInfoPo checkSignUserIdExists = userDao.findUserBySignUserId(signUserId);
        if (Objects.nonNull(checkSignUserIdExists)) {
            throw new BaseException(CodeMessageEnums.USER_EXISTS);
        }

        // get keyStoreInfo
        KeyStoreInfo keyStoreInfo = keyStoreService.newKeyByType(encryptType);

        //save user.
        UserInfoPo userInfoPo = new UserInfoPo();
        BeanUtils.copyProperties(keyStoreInfo, userInfoPo);
        userInfoPo.setEncryptType(encryptType);
        userInfoPo.setSignUserId(signUserId);
        userInfoPo.setAppId(appId);
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

        return rspUserInfoVo;
    }

    /**
     * query user by userId.
     */
    @Cacheable(cacheNames = "user")
    public UserInfoPo findBySignUserId(String signUserId) throws BaseException {
        log.info("start findBySignUserId. signUserId:{}", signUserId);
        UserInfoPo user = userDao.findUserBySignUserId(signUserId);
        if (Objects.isNull(user)) {
            log.warn("fail findBySignUserId, user not exists. userId:{}", signUserId);
            throw new BaseException(CodeMessageEnums.USER_NOT_EXISTS);
        }
        Optional.ofNullable(user)
            .ifPresent(u -> u.setPrivateKey(aesUtils.aesDecrypt(u.getPrivateKey())));
        log.info("end findBySignUserId. userId:{}", signUserId);
        return user;
    }

    public UserInfoPo findByAddress(String address) throws BaseException {
        log.info("start findUserByAddress. address:{}", address);
        UserInfoPo user = userDao.findUserByAddress(address);
        if (Objects.isNull(user)) {
            log.warn("fail findUserByAddress, user not exists. address:{}", address);
            throw new BaseException(CodeMessageEnums.USER_NOT_EXISTS);
        }
        Optional.ofNullable(user)
                .ifPresent(u -> u.setPrivateKey(aesUtils.aesDecrypt(u.getPrivateKey())));
        log.info("end findUserByAddress. address:{}", address);
        return user;
    }

    /**
     * query user list.
     * @param param  encryptType 1: guomi, 0: standard
     */
    public List<RspUserInfoVo> findUserList(UserParam param) {
        log.info("start findUserList.");
        List<UserInfoPo> users = userDao.findUserList(param);
        List<RspUserInfoVo> rspUserInfoVos = new ArrayList<>();
        for (UserInfoPo user : users) {
            RspUserInfoVo rspUserInfoVo = new RspUserInfoVo();
            BeanUtils.copyProperties(user, rspUserInfoVo);
            rspUserInfoVo.setPrivateKey(aesUtils.aesDecrypt(user.getPrivateKey()));
            rspUserInfoVos.add(rspUserInfoVo);
        }
        return rspUserInfoVos;
    }

    public List<RspUserInfoVo> findUserListByAppId(UserParam param) {
        log.info("start findUserListByAppId.");
        List<UserInfoPo> users = userDao.findUserListByAppId(param);
        List<RspUserInfoVo> rspUserInfoVos = new ArrayList<>();
        for (UserInfoPo user : users) {
            RspUserInfoVo rspUserInfoVo = new RspUserInfoVo();
            BeanUtils.copyProperties(user, rspUserInfoVo);
            rspUserInfoVo.setPrivateKey(aesUtils.aesDecrypt(user.getPrivateKey()));
            rspUserInfoVos.add(rspUserInfoVo);
        }
        return rspUserInfoVos;
    }

    public List<UserInfoPo> findUserListByTime(LocalDateTime begin ,LocalDateTime end) {
        log.info("start findUserListByTime.");
        List<UserInfoPo> users = userDao.findUserListByTime(begin,end);

        return users;
    }


    /**
     * delete user by signUserId
     */
    @CacheEvict(cacheNames = "user")
    public void deleteBySignUserId(String signUserId) throws BaseException{
        log.info("start deleteByUuid signUserId:{}", signUserId);
        UserInfoPo user = userDao.findUserBySignUserId(signUserId);
        if (Objects.isNull(user)) {
            log.warn("fail deleteByUuid, user not exists. signUserId:{}", signUserId);
            throw new BaseException(CodeMessageEnums.USER_NOT_EXISTS);
        }
        userDao.deleteUserBySignUserId(signUserId);
        log.info("end deleteByUuid.");
    }

    public UserInfoPo findLatestUpdatedUser() {
        UserInfoPo user = userDao.findLatestUpdateUser();
        return user;
    }
}
