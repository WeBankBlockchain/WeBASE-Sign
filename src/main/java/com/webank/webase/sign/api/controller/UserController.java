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

import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.enums.EncryptTypes;
import com.webank.webase.sign.pojo.vo.ReqUserInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

import static com.webank.webase.sign.enums.CodeMessageEnums.*;

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
     * new user from ecdsa or guomi
     */
    @ApiOperation(value = "new user from ecdsa/guomi, default ecdsa",
            notes = "新建公私钥用户(ecdsa或国密)，默认ecdas")
    @GetMapping("/newUser")
    public BaseRspVo newUser(@RequestParam String uuidUser,
                             @RequestParam(required = false, defaultValue = "0") Integer encryptType)
        throws BaseException {
        // validate uuidUser
        if (StringUtils.isBlank(uuidUser)) {
            throw new BaseException(PARAM_UUID_USER_IS_BLANK);
        }
        if (!CommonUtils.isLetterDigit(uuidUser)) {
            throw new BaseException(PARAM_UUID_USER_IS_INVALID);
        }
        // new user
        RspUserInfoVo userInfo = userService.newUser(uuidUser, encryptType);
        userInfo.setPrivateKey("");
        return CommonUtils.buildSuccessRspVo(userInfo);
    }

    /**
     * get user.
     */
    @ApiOperation(value = "check user info exist", notes = "check user info exist")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uuidUser", value = "uuid of user in business system",
                    required = true, dataType = "String"),
    })
    @GetMapping("/{uuidUser}/userInfo")
    public BaseRspVo getUserInfo(@PathVariable("uuidUser") String uuidUser) throws BaseException {
        //find user
        UserInfoPo userInfo = userService.findByUuidUser(uuidUser);
        RspUserInfoVo rspUserInfoVo = new RspUserInfoVo();
        Optional.ofNullable(userInfo).ifPresent(u -> BeanUtils.copyProperties(u, rspUserInfoVo));
        rspUserInfoVo.setPrivateKey("");
        return CommonUtils.buildSuccessRspVo(rspUserInfoVo);
    }

    @ApiOperation(value = "delete user by address",
            notes = "通过地址删除私钥")
    @DeleteMapping("")
    public BaseRspVo deleteUser(@RequestBody ReqUserInfoVo req) throws BaseException {
        String uuidUser = req.getUuidUser();
        if (StringUtils.isBlank(uuidUser)) {
            throw new BaseException(PARAM_UUID_USER_IS_BLANK);
        }
        userService.deleteByUuid(uuidUser);
        return CommonUtils.buildSuccessRspVo(null);
    }

}
