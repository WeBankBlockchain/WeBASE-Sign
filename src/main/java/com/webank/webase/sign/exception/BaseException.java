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
package com.webank.webase.sign.exception;

import com.webank.webase.sign.enums.CodeMessageEnums;

/**
 * BaseException.
 * 
 */
public class BaseException extends Exception {

    private static final long serialVersionUID = 1L;
    private CodeMessageEnums cme;

    public BaseException(CodeMessageEnums cme) {
        super(cme.getMessage());
        this.cme = cme;
    }

    public BaseException(String msg) {
        super(msg);
        this.cme.setMessage(msg);
    }

    public CodeMessageEnums getCodeMessageEnums() {
        return cme;
    }
}
