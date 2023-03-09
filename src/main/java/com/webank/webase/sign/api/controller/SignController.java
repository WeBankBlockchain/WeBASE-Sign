/*
 * Copyright 2014-2021 the original author or authors.
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
import com.webank.webase.sign.api.service.UserService;
import com.webank.webase.sign.constant.ConstantProperties;
import com.webank.webase.sign.exception.BaseException;
import com.webank.webase.sign.pojo.vo.BaseRspVo;
import com.webank.webase.sign.pojo.vo.ReqEncodeInfoVo;
import com.webank.webase.sign.pojo.vo.ReqSignHashVo;
import com.webank.webase.sign.pojo.vo.ReqSignMessageHashVo;
import com.webank.webase.sign.pojo.vo.RspSignVo;
import com.webank.webase.sign.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.fisco.bcos.sdk.v3.model.CryptoType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.webank.webase.sign.enums.CodeMessageEnums.PARAM_ENCRYPT_TYPE_IS_INVALID;
import static com.webank.webase.sign.enums.CodeMessageEnums.PARAM_HASH_LENGTH_INVALID;
import static com.webank.webase.sign.enums.CodeMessageEnums.PARAM_SIGN_USER_ID_IS_INVALID;


/**
 * Controller.
 */
@Api(value = "sign", tags = "sign interface")
@RestController
@RequestMapping("sign")
public class SignController {

    @Autowired
    SignService signService;
    @Autowired
    UserService userService;
    private static final int HASH_LENGTH_PREFIX = 66;
    private static final int HASH_LENGTH_NO_PREFIX = 64;

    /**
     * add sign by ecdsa or guomi encryption
     *
     * @param req parameter
     * @param result checkResult
     */
    @ApiOperation(value = "add sign by ecdsa(default) or guomi",
            notes = "获取ECDSA或国密SM2签名数据，默认ECDSA")
    @ApiImplicitParam(name = "req", value = "encode info", required = true,
            dataType = "ReqEncodeInfoVo")
    @PostMapping("")
    public BaseRspVo signStandard(@Valid @RequestBody ReqEncodeInfoVo req, BindingResult result)
        throws BaseException {
        CommonUtils.checkParamBindResult(result);
        String signUserId = req.getSignUserId();
        if (!CommonUtils.checkLengthWithin_64(signUserId)) {
            throw new BaseException(PARAM_SIGN_USER_ID_IS_INVALID);
        }
        String signResult = signService.sign(req);
        // return
        RspSignVo rspSignVo = new RspSignVo();
        rspSignVo.setSignDataStr(signResult);
        return CommonUtils.buildSuccessRspVo(rspSignVo);
    }


    /**
     * add sign by ecdsa or guomi encryption
     *
     * @param req parameter
     * @param result checkResult
     */
    @ApiOperation(value = "add sign by ecdsa(default) or guomi by hash",
        notes = "获取ECDSA或国密SM2签名数据")
    @ApiImplicitParam(name = "req", value = "hash to sign", required = true,
        dataType = "ReqSignHashVo")
    @PostMapping("/hash")
    public BaseRspVo signHash(@Valid @RequestBody ReqSignHashVo req, BindingResult result)
        throws BaseException {
        CommonUtils.checkParamBindResult(result);
        String signUserId = req.getSignUserId();
        if (!CommonUtils.checkLengthWithin_64(signUserId)) {
            throw new BaseException(PARAM_SIGN_USER_ID_IS_INVALID);
        }
//        Integer encrtypType = req.getEncryptType();
//        if (CryptoType.ECDSA_TYPE != encrtypType && CryptoType.SM_TYPE != encrtypType) {
//            throw new BaseException(PARAM_ENCRYPT_TYPE_IS_INVALID);
//        }
        String messageHash = req.getMessageHash();
        if (messageHash.length() != HASH_LENGTH_PREFIX && messageHash.length() != HASH_LENGTH_NO_PREFIX ) {
            throw new BaseException(PARAM_HASH_LENGTH_INVALID);
        }
        String signResult = signService.signMessageHash(req);
        // return
        RspSignVo rspSignVo = new RspSignVo();
        rspSignVo.setSignDataStr(signResult);
        return CommonUtils.buildSuccessRspVo(rspSignVo);
    }
}
