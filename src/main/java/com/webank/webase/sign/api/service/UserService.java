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
import java.util.Optional;
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
        String userName = req.getUserName();
        int groupId = req.getGroupId();
        // check user name not exist.
        UserInfoPo userRow = findByNameAndGroupId(groupId, userName);
        if (userRow != null) {
            log.warn("fail newUser,already exists. group:{} user:{}", groupId, userName);
            throw new BaseException(CodeMessageEnums.USER_NAME_IS_EXISTS);
        }
        // get keyStoreInfo
        KeyStoreInfo keyStoreInfo = keyStoreService.newKey();

        //save user.
        UserInfoPo userInfoPo = new UserInfoPo();
        BeanUtils.copyProperties(keyStoreInfo, userInfoPo);
        BeanUtils.copyProperties(req, userInfoPo);

        RspUserInfoVo rspUserInfoVo = saveUser(userInfoPo);
        log.info("end addUser");
        return rspUserInfoVo;
    }

    /**
     * import user.
     */
   /* public RspUserInfoVo importUser(String privateKey, String userName) throws BaseException {
        log.info("start importUser");
        if (StringUtils.isBlank(privateKey)) {
            log.warn("fail importUser. privateKey is blank");
            throw new BaseException(CodeMessageEnums.PRIVATEKEY_IS_NULL);
        }
        // check user name not exist.
        UserInfoPo userRow = findByNameAndGroupId(groupId, userName);
        if (userRow != null) {
            log.warn("fail importUser,already exists. group:{} user:{}", groupId, userName);
            throw new BaseException(CodeMessageEnums.USER_NAME_IS_EXISTS);
        }

        // get keyStoreInfo
        KeyStoreInfo keyStoreInfo = keyStoreService.getKeyStoreFromPrivateKey(privateKey);

        //save user.
        UserInfoPo userInfoPo = new UserInfoPo();
        BeanUtils.copyProperties(keyStoreInfo, userInfoPo);
        userInfoPo.setUserName(aesUtils.aesEncrypt(userName));

        RspUserInfoVo rspUserInfoVo = saveUser(userInfoPo);
        log.info("end importUser");
        return rspUserInfoVo;
    }*/


    /**
     * save user.
     */
    private RspUserInfoVo saveUser(UserInfoPo userInfoPo) {
        // save user
        userDao.insertUserInfo(userInfoPo);

        // return
        RspUserInfoVo rspUserInfoVo = new RspUserInfoVo();
        BeanUtils.copyProperties(userInfoPo, rspUserInfoVo);

        rspUserInfoVo.setUserName(userInfoPo.getUserName());
        rspUserInfoVo.setPrivateKey(aesUtils.aesDecrypt(userInfoPo.getPrivateKey()));

        return rspUserInfoVo;
    }

    /**
     * get user info.
     *
     * @param userName userName
     */
   /* public UserInfoPo getUserInfo(String userName) {
        userName = aesUtils.aesEncrypt(userName);
        UserInfoPo user = userDao.findUser(userName);
        if (Objects.isNull(user)) {
            log.info("not found  user info by username:{}", userName);
            return user;
        }
        user.setUserName(userName);
        user.setPrivateKey(aesUtils.aesDecrypt(user.getPrivateKey()));
        return user;
    }*/


    /**
     * query user by groupId and name.
     */
    private UserInfoPo findByNameAndGroupId(int groupId, String userName) throws BaseException {
        if (groupId <= 0) {
            log.error("fail findByNameAndGroupId, groupId:{}", groupId);
            throw new BaseException(CodeMessageEnums.INVALID_GROUP_ID);
        }
        if (StringUtils.isBlank(userName)) {
            log.error("fail findByNameAndGroupId, userName is null");
            throw new BaseException(CodeMessageEnums.USERNAME_IS_NULL);
        }

        UserInfoPo user = userDao.findUser(null, userName, groupId);
        Optional.ofNullable(user)
            .ifPresent(u -> u.setPrivateKey(aesUtils.aesDecrypt(u.getPrivateKey())));

        return user;
    }

    /**
     * query user by groupId and address.
     */
    public UserInfoPo findByAddressAndGroupId(int groupId, String address) throws BaseException {
        if (groupId <= 0) {
            log.error("fail findByAddressAndGroupId, groupId:{}", groupId);
            throw new BaseException(CodeMessageEnums.INVALID_GROUP_ID);
        }
        if (StringUtils.isBlank(address)) {
            log.error("fail findByAddressAndGroupId, address is null");
            throw new BaseException(CodeMessageEnums.ADDRESS_IS_NULL);
        }

        UserInfoPo user = userDao.findUser(address, null, groupId);
        Optional.ofNullable(user)
            .ifPresent(u -> u.setPrivateKey(aesUtils.aesDecrypt(u.getPrivateKey())));
        return user;
    }
}
