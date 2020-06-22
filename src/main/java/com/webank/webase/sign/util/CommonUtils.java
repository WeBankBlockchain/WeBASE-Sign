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

import java.util.Base64;
import java.util.stream.Collectors;

import com.webank.webase.sign.pojo.vo.BasePageRspVo;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.crypto.Sign.SignatureData;
import org.fisco.bcos.web3j.utils.Numeric;
import org.springframework.validation.BindingResult;
import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.exception.ParamException;
import com.webank.webase.sign.pojo.vo.BaseRspVo;
import lombok.extern.slf4j.Slf4j;

/**
 * CommonUtils.
 */
@Slf4j
public class CommonUtils {

    public static final int publicKeyLength_64 = 64;

    /**
     * stringToSignatureData.
     * 19/12/24 support guomi： add byte[] pub in signatureData
     * @param signatureData signatureData
     * @return
     */
//    public static SignatureData stringToSignatureData(String signatureData) {
//        byte[] byteArr = Numeric.hexStringToByteArray(signatureData);
//        byte[] signR = new byte[32];
//        System.arraycopy(byteArr, 1, signR, 0, signR.length);
//        byte[] signS = new byte[32];
//        System.arraycopy(byteArr, 1 + signR.length, signS, 0, signS.length);
//        if (EncryptType.encryptType == 1) {
//            byte[] pub = new byte[64];
//            System.arraycopy(byteArr, 1 + signR.length + signS.length, pub, 0, pub.length);
//            return new SignatureData(byteArr[0], signR, signS, pub);
//        } else {
//            return new SignatureData(byteArr[0], signR, signS);
//        }
//    }

    /**
     * signatureDataToString.
     * 19/12/24 support guomi： add byte[] pub in signatureData
     * @param signatureData signatureData
     */
//    public static String signatureDataToString(SignatureData signatureData) {
//        byte[] byteArr;
//        if(EncryptType.encryptType == 1) {
//            byteArr = sigData2ByteArrGuomi(signatureData);
//        } else {
//            byteArr = sigData2ByteArrECDSA(signatureData);
//        }
//        return Numeric.toHexString(byteArr, 0, byteArr.length, false);
//    }

    /**
     * signatureDataToString by type
     * @param signatureData
     * @param encryptType
     * @return
     */
    public static String signatureDataToStringByType(SignatureData signatureData, int encryptType) {
        byte[] byteArr;
        if(encryptType == 1) {
            byteArr = sigData2ByteArrGuomi(signatureData);
        } else {
            byteArr = sigData2ByteArrECDSA(signatureData);
        }
        return Numeric.toHexString(byteArr, 0, byteArr.length, false);
    }

    private static byte[] sigData2ByteArrGuomi(SignatureData signatureData) {
        byte[] targetByteArr;
        targetByteArr = new byte[1 + signatureData.getR().length + signatureData.getS().length + publicKeyLength_64];
        targetByteArr[0] = signatureData.getV();
        System.arraycopy(signatureData.getR(), 0, targetByteArr, 1, signatureData.getR().length);
        System.arraycopy(signatureData.getS(), 0, targetByteArr, signatureData.getR().length + 1,
                signatureData.getS().length);
        System.arraycopy(signatureData.getPub(), 0, targetByteArr,
                signatureData.getS().length + signatureData.getR().length + 1,
                signatureData.getPub().length);
        return targetByteArr;
    }

    private static byte[] sigData2ByteArrECDSA(SignatureData signatureData) {
        byte[] targetByteArr;
        targetByteArr = new byte[1 + signatureData.getR().length + signatureData.getS().length];
        targetByteArr[0] = signatureData.getV();
        System.arraycopy(signatureData.getR(), 0, targetByteArr, 1, signatureData.getR().length);
        System.arraycopy(signatureData.getS(), 0, targetByteArr, signatureData.getR().length + 1,
                signatureData.getS().length);
        return targetByteArr;
    }


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
