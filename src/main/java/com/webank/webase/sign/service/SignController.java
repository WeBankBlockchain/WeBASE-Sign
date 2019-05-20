/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.webase.sign.service;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.webank.webase.sign.base.BaseController;
import com.webank.webase.sign.base.BaseResponse;
import com.webank.webase.sign.base.exception.BaseException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller.
 * 
 */
@Api(value = "/sign", tags = "sign interface")
@Slf4j
@RestController
public class SignController extends BaseController {
    @Autowired
    SignService signService;

    /**
     * add user.
     * 
     * @param req parameter
     * @param result checkResult
     * @return
     */
    @ApiOperation(value = "add user", notes = "add user")
    @ApiImplicitParam(name = "req", value = "user info", required = true, dataType = "ReqAddUser")
    @PostMapping("/addUser")
    public BaseResponse addUser(@Valid @RequestBody ReqAddUser req,
            BindingResult result) throws BaseException {
        log.info("addUser start. req:{}", JSON.toJSONString(req));
        checkParamResult(result);
        return signService.addUser(req);
    }
    
    /**
     * get user.
     * 
     * @param req parameter
     * @param result checkResult
     * @return
     */
    @ApiOperation(value = "get user info", notes = "get user info by name")
    @ApiImplicitParam(name = "userName", value = "userName", required = true, dataType = "String", paramType = "path")
    @GetMapping("/userInfo/{userName}")
    public BaseResponse getUserInfo(@PathVariable("userName") String userName) throws BaseException {
        log.info("getUserInfo start. userName:{}", userName);
        return signService.getUserInfo(userName);
    }
    
    /**
     * add sign.
     * 
     * @param req parameter
     * @param result checkResult
     * @return
     * @throws BaseException 
     */
    @ApiOperation(value = "add sign", notes = "add sign")
    @ApiImplicitParam(name = "req", value = "encode info", required = true, dataType = "ReqEncodeInfo")
    @PostMapping("/addSign")
    public BaseResponse addSign(@Valid @RequestBody ReqEncodeInfo req,
            BindingResult result) throws BaseException {
        log.info("addSign start. req:{}", JSON.toJSONString(req));
        checkParamResult(result);
        return signService.addSign(req);
    }
}
