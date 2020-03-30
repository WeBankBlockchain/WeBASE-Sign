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

import com.webank.webase.sign.api.service.UserService;
import com.webank.webase.sign.enums.EncryptTypes;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.bo.UserParam;
import com.webank.webase.sign.pojo.po.UserInfoPo;
import com.webank.webase.sign.pojo.vo.BaseRspVo;
import com.webank.webase.sign.pojo.vo.ReqUserInfoVo;
import com.webank.webase.sign.pojo.vo.RspUserInfoVo;
import com.webank.webase.sign.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.Optional;

import static com.webank.webase.sign.enums.CodeMessageEnums.PARAM_APP_ID_IS_BLANK;
import static com.webank.webase.sign.enums.CodeMessageEnums.PARAM_SIGN_USER_ID_IS_BLANK;
import static com.webank.webase.sign.enums.CodeMessageEnums.PARAM_APP_ID_IS_INVALID;
import static com.webank.webase.sign.enums.CodeMessageEnums.PARAM_ENCRYPT_TYPE_IS_INVALID;
import static com.webank.webase.sign.enums.CodeMessageEnums.PARAM_SIGN_USER_ID_IS_INVALID;

/**
 * Controller.
 */
@Slf4j
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
    public BaseRspVo newUser(@RequestParam String signUserId,
                             @RequestParam String appId,
                             @RequestParam(required = false, defaultValue = "0") Integer encryptType)
        throws BaseException {
        // validate signUserId
        if (StringUtils.isBlank(signUserId)) {
            throw new BaseException(PARAM_SIGN_USER_ID_IS_BLANK);
        }
        if (!CommonUtils.isLetterDigit(signUserId)) {
            throw new BaseException(PARAM_SIGN_USER_ID_IS_INVALID);
        }
        if (StringUtils.isBlank(appId)) {
            throw new BaseException(PARAM_APP_ID_IS_BLANK);
        }
        if (!CommonUtils.isLetterDigit(appId)) {
            throw new BaseException(PARAM_APP_ID_IS_INVALID);
        }
        if (encryptType != EncryptTypes.STANDARD.getValue()
                && encryptType != EncryptTypes.GUOMI.getValue()) {
            throw new BaseException(PARAM_ENCRYPT_TYPE_IS_INVALID);
        }
        // new user
        RspUserInfoVo userInfo = userService.newUser(signUserId, appId, encryptType);
        userInfo.setPrivateKey("");
        return CommonUtils.buildSuccessRspVo(userInfo);
    }

    /**
     * get user.
     */
    @ApiOperation(value = "check user info exist", notes = "check user info exist")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signUserId", value = "business id of user in system",
                    required = true, dataType = "String"),
    })
    @GetMapping("/{signUserId}/userInfo")
    public BaseRspVo getUserInfo(@PathVariable("signUserId") String signUserId) throws BaseException {
        //find user
        UserInfoPo userInfo = userService.findBySignUserId(signUserId);
        RspUserInfoVo rspUserInfoVo = new RspUserInfoVo();
        Optional.ofNullable(userInfo).ifPresent(u -> BeanUtils.copyProperties(u, rspUserInfoVo));
        rspUserInfoVo.setPrivateKey("");
        return CommonUtils.buildSuccessRspVo(rspUserInfoVo);
    }

    /**
     * get user list by app id
     */
    @ApiOperation(value = "get user list by app id", notes = "根据appId获取user列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "app id that users belong to",
                    required = true, dataType = "String"),
    })
    @GetMapping("/list/{appId}/{pageNumber}/{pageSize}")
    public BaseRspVo getUserListByAppId(@PathVariable("appId") String appId,
                                        @PathVariable("pageNumber") Integer pageNumber,
                                        @PathVariable("pageSize") Integer pageSize) {
        UserParam param = new UserParam();
        param.setAppId(appId);
        Integer start = Optional.ofNullable(pageNumber).map(page -> (page - 1) * pageSize)
                .orElse(null);
        param.setStart(start);
        param.setPageSize(pageSize);
        //find user
        List<RspUserInfoVo> userList = userService.findUserListByAppId(param);
        if (!userList.isEmpty()) {
            userList.forEach(user -> user.setPrivateKey(""));
        }
        return CommonUtils.buildSuccessPageRspVo(userList, userList.size());
    }

    @ApiOperation(value = "delete user by address",
            notes = "通过地址删除私钥")
    @DeleteMapping("")
    public BaseRspVo deleteUser(@RequestBody ReqUserInfoVo req) throws BaseException {
        String signUserId = req.getSignUserId();
        if (StringUtils.isBlank(signUserId)) {
            throw new BaseException(PARAM_SIGN_USER_ID_IS_BLANK);
        }
        userService.deleteBySignUserId(signUserId);
        return CommonUtils.buildSuccessRspVo(null);
    }


    @ApiOperation(value = "delete all user cache",
            notes = "删除所有用户缓存信息")
    @DeleteMapping("/all")
    public BaseRspVo deleteAllUserCache() throws BaseException {

        userService.deleteAllUserCache();
        return CommonUtils.buildSuccessRspVo(null);
    }




}
