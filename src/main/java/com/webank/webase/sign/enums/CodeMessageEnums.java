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

    //business exception
    USER_NAME_IS_EXISTS(303001, "user is already exists"),
    USER_IS_NOT_EXISTS(303002, "user does not exist"),

    //system error
    SYSTEM_ERROR(103001, "system error"),
    PARAM_VAILD_FAIL(103001, "system error");

    int code;
    @Setter
    String message;
}
