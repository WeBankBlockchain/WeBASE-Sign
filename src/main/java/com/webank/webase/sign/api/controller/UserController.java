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

import static com.webank.webase.sign.enums.CodeMessageEnums.PARAM_ADDRESS_IS_BLANK;

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
    @ApiOperation(value = "new user from ecdsa/guomi, default ecdsa",
            notes = "新建公私钥用户(ecdsa或国密)，默认ecdas")
    @GetMapping("/newUser")
    public BaseRspVo newUser(@RequestParam(required = false, defaultValue = "0") Integer encryptType)
        throws BaseException {
        //new user
        RspUserInfoVo userInfo = userService.newUser(encryptType);
        userInfo.setPrivateKey("");
        return CommonUtils.buildSuccessRspVo(userInfo);
    }

    @ApiOperation(value = "delete user by address",
            notes = "通过地址删除私钥")
    @DeleteMapping("")
    public BaseRspVo deleteUser(@RequestBody ReqUserInfoVo req) throws BaseException {
        String address = req.getAddress();
        if (StringUtils.isBlank(address)) {
            throw new BaseException(PARAM_ADDRESS_IS_BLANK);
        }
        userService.deleteByAddress(address);
        return CommonUtils.buildSuccessRspVo(null);
    }

    /**
     * get user.
     * @deprecated
     */
//    @ApiOperation(value = "get user info", notes = "get user by userId")
//    @ApiImplicitParams({
//        @ApiImplicitParam(name = "userId", value = "user id", required = true, dataType = "int"),
//    })
//    @GetMapping("/{userId}/userInfo")
//    public BaseRspVo getUserInfo(@PathVariable("userId") Integer userId) throws BaseException {
//        //find user
//        UserInfoPo userInfo = userService.findByUserId(userId);
//        RspUserInfoVo rspUserInfoVo = new RspUserInfoVo();
//        Optional.ofNullable(userInfo).ifPresent(u -> BeanUtils.copyProperties(u, rspUserInfoVo));
//        return CommonUtils.buildSuccessRspVo(rspUserInfoVo);
//    }
    
    /**
     * get user list of ecdsa/guomi by encrypt type
     * @deprecated
     */
//    @ApiOperation(value = "get standard user list by encrypt type",
//            notes = "获取国密或ECDSA用户列表")
//    @GetMapping("/list")
//    public BaseRspVo getUserList(@RequestParam(required = false, defaultValue = "0") Integer encryptType)
//            throws BaseException {
//        //find user list
//        List<RspUserInfoVo> rspUserInfos = userService.findUserList(encryptType);
//        return CommonUtils.buildSuccessRspVo(rspUserInfos);
//    }

}
