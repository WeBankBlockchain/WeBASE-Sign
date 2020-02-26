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
package com.webank.webase.sign.api.controller;

import java.util.List;
import java.util.Optional;

import com.webank.webase.sign.enums.EncryptTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.webank.webase.sign.api.service.UserService;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.po.UserInfoPo;
import com.webank.webase.sign.pojo.vo.BaseRspVo;
import com.webank.webase.sign.pojo.vo.RspUserInfoVo;
import com.webank.webase.sign.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * Controller.
 */
@Api(value = "user", tags = "user interface")
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * new user from ecdsa(standard)
     */
    @ApiOperation(value = "new user from ecdsa", notes = "new user from ecdsa")
    @GetMapping("/newUser")
    public BaseRspVo newUser()
        throws BaseException {
        //new user
        RspUserInfoVo userInfo = userService.newUser(EncryptTypes.STANDARD.getValue());
        return CommonUtils.buildSuccessRspVo(userInfo);
    }

    /**
     * new user from guomi
     */
    @ApiOperation(value = "new user from guomi", notes = "new user from guomi")
    @GetMapping("/newUserGuomi")
    public BaseRspVo newUserGuomi()
            throws BaseException {
        //new user
        RspUserInfoVo userInfo = userService.newUser(EncryptTypes.GUOMI.getValue());
        return CommonUtils.buildSuccessRspVo(userInfo);
    }

    /**
     * get user.
     */
    @ApiOperation(value = "get user info", notes = "get user by userId")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId", value = "user id", required = true, dataType = "int"),
    })
    @GetMapping("/{userId}/userInfo")
    public BaseRspVo getUserInfo(@PathVariable("userId") Integer userId) throws BaseException {
        //find user
        UserInfoPo userInfo = userService.findByUserId(userId);
        RspUserInfoVo rspUserInfoVo = new RspUserInfoVo();
        Optional.ofNullable(userInfo).ifPresent(u -> BeanUtils.copyProperties(u, rspUserInfoVo));
        return CommonUtils.buildSuccessRspVo(rspUserInfoVo);
    }
    
    /**
     * get user list of ecdsa(standard) encrypt type
     */
    @ApiOperation(value = "get standard user list", notes = "get standard user list")
    @GetMapping("/list")
    public BaseRspVo getStandardUserList() throws BaseException {
        //find user list
        List<RspUserInfoVo> rspUserInfos = userService.findUserList(EncryptTypes.STANDARD.getValue());
        return CommonUtils.buildSuccessRspVo(rspUserInfos);
    }

    /**
     * get user list of guomi encrypt type
     */
    @ApiOperation(value = "get guomi user list", notes = "get guomi user list")
    @GetMapping("/listGuomi")
    public BaseRspVo getUserList() throws BaseException {
        //find user list
        List<RspUserInfoVo> rspUserInfos = userService.findUserList(EncryptTypes.GUOMI.getValue());
        return CommonUtils.buildSuccessRspVo(rspUserInfos);
    }
}
