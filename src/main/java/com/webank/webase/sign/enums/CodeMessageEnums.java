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
package com.webank.webase.sign.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A-BB-CCC A:error level. <br/>
 * 1:system exception <br/>
 * 2:business exception <br/>
 * B:project number <br/>
 * WeBASE-Sign:03 <br/>
 * C: error code <br/>
 */
@Getter
@ToString
@AllArgsConstructor
public enum CodeMessageEnums {
    //success
    SUCCEED(0, "success"),

    //param
    PARAM_EXCEPTION(203003, "param exception"),
    PARAM_SIGN_USER_ID_IS_BLANK(203004, "sign user id cannot be blank"),
    PARAM_SIGN_USER_ID_IS_INVALID(203005, "invalid sign user id (max length of 64, only support letter and digit)"),
    PARAM_APP_ID_IS_BLANK(203006, "app id cannot be blank"),
    PARAM_APP_ID_IS_INVALID(203007, "app id invalid, only support letter and digit"),
    PARAM_ENCRYPT_TYPE_IS_INVALID(203008, "encrypt type should be 0 (ecdsa) or 1 (guomi)"),
    PARAM_ENCODED_DATA_INVALID(203009, "encoded data string must be hex string"),

    //business exception
    USER_EXISTS(303001, "user of this sign user id is already exists "),
    USER_DISABLE(303006, "user of this sign user id is  already been disable"),
    USER_NOT_EXISTS(303002, "user does not exist or already been disable"),
    PRIVATEKEY_IS_NULL(303003, "privateKey is null"),
    PRIVATE_KEY_DECODE_FAIL(303004, "privateKey decode fail"),
    PRIVATEKEY_FORMAT_ERROR(303005, "privateKey format error"),

    //system error
    SYSTEM_ERROR(103001, "system error"),
    PARAM_VAILD_FAIL(103002, "param valid fail");

    int code;
    @Setter
    String message;
}
