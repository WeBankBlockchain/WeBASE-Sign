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
package com.webank.webase.sign.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum CodeMessageEnums {
    //success
    SUCCEED(0, "success"),

    //param
    PARAM_EXCEPTION(203003, "param exception"),
    PARAM_ADDRESS_IS_BLANK(203004, "address cannot be blank"),
    PARAM_SIGN_USER_ID_IS_BLANK(203005, "sign user id cannot be blank"),
    PARAM_SIGN_USER_ID_IS_INVALID(203006, "invalid sign user id, only support letter and digit"),
    PARAM_APP_ID_IS_BLANK(203007, "app id cannot be blank"),
    PARAM_APP_ID_IS_INVALID(203008, "app id invalid, only support letter and digit"),
    PARAM_ENCRYPT_TYPE_IS_INVALID(203009, "encrypt type should be 0 (guomi) or 1 (ecdsa)"),

    //business exception
    USER_EXISTS(303001, "user of this sign user id is already exists"),
    USER_NOT_EXISTS(303002, "user does not exist"),
    PRIVATEKEY_IS_NULL(303003, "privateKey is null"),
    PRIVATE_KEY_DECODE_FAIL(303004, "privateKey decode fail"),
    PRIVATEKEY_FORMAT_ERROR(303005, "privateKey format error"),

    //system error
    SYSTEM_ERROR(103001, "system error"),
    PARAM_VAILD_FAIL(103001, "param valid fail");

    int code;
    @Setter
    String message;
}
