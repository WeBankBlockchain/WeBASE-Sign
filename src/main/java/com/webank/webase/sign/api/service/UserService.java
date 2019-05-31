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

import com.alibaba.fastjson.JSON;
import com.webank.webase.sign.api.dao.UserDao;
import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.bo.KeyStoreInfo;
import com.webank.webase.sign.pojo.po.UserInfoPo;
import com.webank.webase.sign.pojo.vo.ReqNewUserVo;
import com.webank.webase.sign.pojo.vo.RspUserInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private KeyStoreService keyStoreService;

    /**
     * add user.
     *
     * @param req parameter
     */
    public RspUserInfoVo addUser(ReqNewUserVo req) throws BaseException {
        log.info("start addUser. reqNewUserVo:{}", JSON.toJSONString(req));

        // check user name not exist.
        userNameNotExistOrThrow(req.getUserName());
        // get keyStoreInfo
        KeyStoreInfo keyStoreInfo = keyStoreService.getKey();

        // save user
        UserInfoPo userInfoPo = new UserInfoPo();
        BeanUtils.copyProperties(keyStoreInfo, userInfoPo);
        BeanUtils.copyProperties(req, userInfoPo);
        userDao.insertUserInfo(userInfoPo);

        // return
        RspUserInfoVo rspUserInfoVo = new RspUserInfoVo();
        BeanUtils.copyProperties(userInfoPo, rspUserInfoVo);

        log.info("end addUser. baseRsp:{}", JSON.toJSONString(rspUserInfoVo));
        return rspUserInfoVo;
    }

    /**
     * get user info.
     *
     * @param userName userName
     */
    public UserInfoPo getUserInfo(String userName) {
        return userDao.findUser(userName);
    }


    /**
     * check userName not exist.
     */
    public void userNameNotExistOrThrow(String userName) throws BaseException {
        // check user name
        UserInfoPo userRow = userDao.findUser(userName);
        if (userRow != null) {
            log.warn("fail addUser. user name:{} is already exists", userName);
            throw new BaseException(CodeMessageEnums.USER_NAME_IS_EXISTS);
        }
    }
}
