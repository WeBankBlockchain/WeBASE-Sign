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
package com.webank.webase.sign.util;

import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.crypto.Sign.SignatureData;
import org.fisco.bcos.web3j.utils.Numeric;
import org.springframework.validation.BindingResult;
import com.alibaba.fastjson.JSON;
import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.exception.ParamException;
import com.webank.webase.sign.pojo.vo.BaseRspVo;
import lombok.extern.slf4j.Slf4j;

/**
 * CommonUtils.
 */
@Slf4j
public class CommonUtils {

    /**
     * stringToSignatureData.
     *
     * @param signatureData signatureData
     */
    public static SignatureData stringToSignatureData(String signatureData) {
        byte[] byteArr = Numeric.hexStringToByteArray(signatureData);
        byte[] signR = new byte[32];
        System.arraycopy(byteArr, 1, signR, 0, signR.length);
        byte[] signS = new byte[32];
        System.arraycopy(byteArr, 1 + signR.length, signS, 0, signS.length);
        return new SignatureData(byteArr[0], signR, signS);
    }

    /**
     * signatureDataToString.
     *
     * @param signatureData signatureData
     */
    public static String signatureDataToString(SignatureData signatureData) {
        byte[] byteArr = new byte[1 + signatureData.getR().length + signatureData.getS().length];
        byteArr[0] = signatureData.getV();
        System.arraycopy(signatureData.getR(), 0, byteArr, 1, signatureData.getR().length);
        System.arraycopy(signatureData.getS(), 0, byteArr, signatureData.getR().length + 1,
            signatureData.getS().length);
        return Numeric.toHexString(byteArr, 0, byteArr.length, false);
    }


    /**
     * check param valid result.
     */
    public static void checkParamBindResult(BindingResult result) {
        if (result.hasErrors()) {
            log.error("param exception. error:{}", JSON.toJSONString(result.getAllErrors()));
            String errFieldStr = result.getAllErrors().stream()
                .map(obj -> JSON.parseObject(JSON.toJSONString(obj)))
                .map(err -> err.getString("field"))
                .collect(Collectors.joining(","));
            StringUtils.removeEnd(errFieldStr, ",");
            String message = "These fields do not match:" + errFieldStr;

            ParamException paramException = new ParamException(CodeMessageEnums.PARAM_EXCEPTION);
            paramException.setMessage(message);
            throw paramException;
        }
    }

    /**
     *
     */
    public static BaseRspVo buildSuccessRspVo(Object data) {
        BaseRspVo baseRspVo = new BaseRspVo(CodeMessageEnums.SUCCEED);
        baseRspVo.setData(data);
        return baseRspVo;
    }
}
