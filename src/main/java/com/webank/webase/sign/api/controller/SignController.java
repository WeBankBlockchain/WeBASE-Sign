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
package com.webank.webase.sign.api.controller;

import com.webank.webase.sign.api.service.SignService;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.vo.BaseRspVo;
import com.webank.webase.sign.pojo.vo.ReqEncodeInfoVo;
import com.webank.webase.sign.pojo.vo.RspSignVo;
import com.webank.webase.sign.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller.
 */
@Api(value = "sign", tags = "sign interface")
@Slf4j
@RestController
@RequestMapping("sign")
public class SignController {

    @Autowired
    SignService signService;


    /**
     * add sign.
     *
     * @param req parameter
     * @param result checkResult
     */
    @ApiOperation(value = "add sign", notes = "add sign")
    @ApiImplicitParam(name = "req", value = "encode info", required = true, dataType = "ReqEncodeInfoVo")
    @PostMapping("/sign")
    public BaseRspVo sign(@Valid @RequestBody ReqEncodeInfoVo req, BindingResult result)
        throws BaseException {
        CommonUtils.checkParamBindResult(result);
        String signResult = signService.sign(req);
        // return
        RspSignVo rspSignVo = new RspSignVo();
        rspSignVo.setSignDataStr(signResult);
        return CommonUtils.buildSuccessRspVo(rspSignVo);
    }
}
