/**
 * Copyright 2014-2020  the original author or authors.
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

package com.webank.webase.sign.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.webank.webase.sign.constant.ConstantProperties;

@Slf4j
@Component
public class AesUtils {

    @Autowired
    private ConstantProperties constants;


    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_IV = "abcdefgh12345678";
    private static final String CBC_PATTERN = "CBC";

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @return 加密数据
     */
    public  String aesEncrypt(String content) {
        return  aesEncrypt( content,  constants.getAesKey(),null);
    }

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @param password 加密密码
     * @param iv 使用CBC模式，需要一个向量iv，可增加加密算法的强度
     * @return 加密数据
     */
    public  String aesEncrypt(String content, String password, String iv) {
        if(StringUtils.isBlank(iv)) {
            iv = DEFAULT_IV;
        }
        try {
            //创建密码器
            Cipher cipher = Cipher.getInstance(this.getDefaultAesCipherPattern());

            //密码key(超过16字节即128bit的key，需要替换jre中的local_policy.jar和US_export_policy.jar，否则报错：Illegal key size)
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes("utf-8"), KEY_ALGORITHM);

            //向量iv
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("utf-8"));

            //初始化为加密模式的密码器
            if (CBC_PATTERN.equals(constants.getAesPattern())) {
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            }

            //加密
            byte[] byteContent = content.getBytes("utf-8");
            byte[] result = cipher.doFinal(byteContent);

            return Base64.getEncoder().encodeToString(result);
        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
            return null;
        }
    }


    /**
     * AES 解密操作
     *
     * @param content 密文
     * @return 明文
     */
    public  String aesDecrypt(String content) {
        return aesDecrypt(content,  constants.getAesKey(),null);
    }


    /**
     * AES 解密操作
     *
     * @param content 密文
     * @param password 密码
     * @param iv 使用CBC模式，需要一个向量iv，可增加加密算法的强度
     * @return 明文
     */
    public  String aesDecrypt(String content, String password,String iv) {
        if(StringUtils.isBlank(iv)) {
            iv = DEFAULT_IV;
        }

        try {
            //创建密码器
            Cipher cipher = Cipher.getInstance(this.getDefaultAesCipherPattern());

            //密码key
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(StandardCharsets.UTF_8),KEY_ALGORITHM);

            //向量iv
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(
                StandardCharsets.UTF_8));

            //初始化为解密模式的密码器
            if (CBC_PATTERN.equals(constants.getAesPattern())) {
                cipher.init(Cipher.DECRYPT_MODE,keySpec,ivParameterSpec);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, keySpec);
            }
            //执行操作
            byte[] encrypted1 = Base64.getDecoder().decode(content);
            byte[] result = cipher.doFinal(encrypted1);

            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error(ex.getMessage(),ex);
        }

        return null;
    }

    /**
     * before v1.4.0, pattern default "ECB", after v1.4.0 use "CBC" as default
     * @return
     */
    private String getDefaultAesCipherPattern() {
        // CBC as default
        String aesPattern = constants.getAesPattern();
        String cipherPattern = "AES/" + aesPattern + "/PKCS5Padding";
        log.info("getDefaultAesCipherPattern aes cipher pattern: {}", cipherPattern);
        return cipherPattern;
    }



}
