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
package com.webank.webase.sign.pojo.vo;

import com.webank.webase.sign.enums.CodeMessageEnums;
import lombok.Data;

@Data
public class BaseRspVo {

    private int code;
    private String message;
    private Object data;

    public BaseRspVo() {
    }

    public BaseRspVo(int code) {
        this.code = code;
    }

    public BaseRspVo(CodeMessageEnums cme) {
        this.code = cme.getCode();
        this.message = cme.getMessage();
    }

    /**
     * constructor.
     *
     * @param cme not null
     * @param obj result
     */
    public BaseRspVo(CodeMessageEnums cme, Object obj) {
        this.code = cme.getCode();
        this.message = cme.getMessage();
        this.data = obj;
    }

    /**
     * constructor.
     *
     * @param code not null
     * @param message not null
     * @param obj result
     */
    public BaseRspVo(int code, String message, Object obj) {
        this.code = code;
        this.message = message;
        this.data = obj;
    }
}
