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

import com.alibaba.fastjson.JSON;
import com.webank.webase.sign.api.service.UserService;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.po.UserInfoPo;
import com.webank.webase.sign.pojo.vo.BaseRspVo;
import com.webank.webase.sign.pojo.vo.ReqNewUserVo;
import com.webank.webase.sign.pojo.vo.RspUserInfoVo;
import com.webank.webase.sign.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller.
 */
@Api(value = "user", tags = "user interface")
@Slf4j
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * new user.
     *
     * @param req parameter
     * @param result checkResult
     */
    @ApiOperation(value = "add user", notes = "add user")
    @ApiImplicitParam(name = "req", value = "user info", required = true, dataType = "ReqNewUserVo")
    @PostMapping("/newUser")
    public BaseRspVo newUser(@Valid @RequestBody ReqNewUserVo req, BindingResult result)
        throws BaseException {
        CommonUtils.checkParamBindResult(result);
        //new user
        RspUserInfoVo userInfo = userService.addUser(req);
        return CommonUtils.buildSuccessRspVo(userInfo);
    }

    /**
     * get user.
     */
    @ApiOperation(value = "get user info", notes = "get user info by name")
    @ApiImplicitParam(name = "userName", value = "userName", required = true, dataType = "String", paramType = "path")
    @GetMapping("/userInfo/{userName:[a-zA-Z0-9_]{3,25}}")
    public BaseRspVo getUserInfo(@PathVariable("userName") String userName) {
        //new user
        UserInfoPo userInfo = userService.getUserInfo(userName);
        RspUserInfoVo rspUserInfoVo = new RspUserInfoVo();
        Optional.ofNullable(userInfo).ifPresent(u ->  BeanUtils.copyProperties(u, rspUserInfoVo));
        return CommonUtils.buildSuccessRspVo(rspUserInfoVo);
    }
}
