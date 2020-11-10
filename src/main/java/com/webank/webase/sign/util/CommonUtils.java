/*
 * Copyright 2014-2020 the original author or authors.
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

import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.exception.ParamException;
import com.webank.webase.sign.pojo.vo.BasePageRspVo;
import com.webank.webase.sign.pojo.vo.BaseRspVo;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.sdk.crypto.signature.ECDSASignatureResult;
import org.fisco.bcos.sdk.crypto.signature.SM2SignatureResult;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.fisco.bcos.sdk.model.CryptoType;
import org.fisco.bcos.sdk.rlp.RlpString;
import org.fisco.bcos.sdk.rlp.RlpType;
import org.fisco.bcos.sdk.utils.Numeric;
import org.springframework.validation.BindingResult;

/**
 * CommonUtils.
 */
@Slf4j
public class CommonUtils {

    public static final int publicKeyLength_64 = 64;
    private static final byte SM_DEFAULT_V_VALUE = 0;

    /* add in v1.4.2 */
    public static String signatureResultToStringByType(SignatureResult signatureResult, int encryptType) {
        byte[] byteArr;
        if (encryptType == CryptoType.SM_TYPE) {
            byteArr = sigResult2ByteArrGuomi((SM2SignatureResult) signatureResult);
        } else {
            byteArr = sigResult2ByteArrECDSA((ECDSASignatureResult) signatureResult);
        }
        return Numeric.toHexString(byteArr, 0, byteArr.length, false);
    }

    private static byte[] sigResult2ByteArrGuomi(SM2SignatureResult signatureResult) {
        List<RlpType> sigRlpList = signatureResult.encode();
        byte[] pubValue = ((RlpString) sigRlpList.get(0)).getBytes();

        byte[] targetByteArr;
        targetByteArr = new byte[1 + signatureResult.getR().length + signatureResult.getS().length + publicKeyLength_64];
        // set V as default 00
        targetByteArr[0] = SM_DEFAULT_V_VALUE;
        System.arraycopy(signatureResult.getR(), 0, targetByteArr, 1, signatureResult.getR().length);
        System.arraycopy(signatureResult.getS(), 0, targetByteArr, signatureResult.getR().length + 1,
            signatureResult.getS().length);
        System.arraycopy(pubValue, 0, targetByteArr,
            signatureResult.getS().length + signatureResult.getR().length + 1,
            pubValue.length);
        return targetByteArr;
    }

    private static byte[] sigResult2ByteArrECDSA(ECDSASignatureResult signatureResult) {
        List<RlpType> sigRlpList = signatureResult.encode();
        byte[] vValueArray = ((RlpString) sigRlpList.get(0)).getBytes();
        byte vValue = vValueArray[0];

        byte[] targetByteArr;
        targetByteArr = new byte[1 + signatureResult.getR().length + signatureResult.getS().length];
        targetByteArr[0] = vValue;
        System.arraycopy(signatureResult.getR(), 0, targetByteArr, 1, signatureResult.getR().length);
        System.arraycopy(signatureResult.getS(), 0, targetByteArr, signatureResult.getR().length + 1,
            signatureResult.getS().length);
        return targetByteArr;
    }
    /* add in v1.4.2 */

    /**
     * check param valid result.
     */
    public static void checkParamBindResult(BindingResult result) {
        if (result.hasErrors()) {
            log.error("param exception. error:{}", JsonUtils.toJSONString(result.getAllErrors()));
            String errFieldStr = result.getAllErrors().stream()
                .map(obj -> JsonUtils.stringToJsonNode(JsonUtils.toJSONString(obj)))
                .map(err -> err.get("field").asText())
                .collect(Collectors.joining(","));
            StringUtils.removeEnd(errFieldStr, ",");
            String message = "These fields do not match:" + errFieldStr;

            ParamException paramException = new ParamException(CodeMessageEnums.PARAM_EXCEPTION);
            paramException.setMessage(message);
            throw paramException;
        }
    }

    /**
     * base response
     */
    public static BaseRspVo buildSuccessRspVo(Object data) {
        BaseRspVo baseRspVo = new BaseRspVo(CodeMessageEnums.SUCCEED);
        baseRspVo.setData(data);
        return baseRspVo;
    }

    /**
     * base page response
     */
    public static BaseRspVo buildSuccessPageRspVo(Object data, long totalCount) {
        BasePageRspVo basePageRspVo = new BasePageRspVo(CodeMessageEnums.SUCCEED);
        basePageRspVo.setData(data);
        basePageRspVo.setTotalCount(totalCount);
        return basePageRspVo;
    }

    /**
     * signUserId支持数字，字母与下划线"_"
     * @param str
     * @return
     */
    public static boolean isLetterDigit(String str) {
        String regex = "^[a-z0-9A-Z_]+$";
        return str.matches(regex);
    }

    /**
     * 0 < signUserId <= 64
     * @param input
     */
    public static boolean checkLengthWithin_64(String input) {
        if (input.isEmpty() || input.length() > publicKeyLength_64) {
            return false;
        }
        return true;
    }

    /**
     * base64Decode.
     *
     * @param str String
     * @return byte[]
     */
    public static byte[] base64Decode(String str) {
        if (str == null) {
            return new byte[0];
        }
        return Base64.getDecoder().decode(str);
    }
}
