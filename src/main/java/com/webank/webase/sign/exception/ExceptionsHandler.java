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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.webase.sign.enums.CodeMessageEnums;
import com.webank.webase.sign.pojo.vo.BaseRspVo;
import com.webank.webase.sign.util.JsonUtils;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * ExceptionsHandler.
 */
@ControllerAdvice
@Slf4j
public class ExceptionsHandler {

    @Autowired
    ObjectMapper mapper;

    /**
     * myExceptionHandler.
     *
     * @param baseException e
     */
    @ResponseBody
    @ExceptionHandler(value = BaseException.class)
    public BaseRspVo baseExceptionHandler(BaseException baseException) {
        log.warn("catch baseException", baseException);
        CodeMessageEnums cme = Optional.ofNullable(baseException)
            .map(BaseException::getCodeMessageEnums).orElse(CodeMessageEnums.SYSTEM_ERROR);

        BaseRspVo rep = new BaseRspVo(cme);
        log.warn("baseException return:{}", JsonUtils.toJSONString(rep));
        return rep;
    }


    /**
     * catch:paramException
     */
    @ResponseBody
    @ExceptionHandler(value = ParamException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseRspVo paramExceptionHandler(ParamException paramException) {
        log.warn("catch param exception", paramException);
        CodeMessageEnums cme = Optional.ofNullable(paramException)
            .map(ParamException::getCodeMessageEnums).orElse(CodeMessageEnums.SYSTEM_ERROR);

        BaseRspVo bre = new BaseRspVo(cme);
        log.warn("param exception return:{}", JsonUtils.toJSONString(bre));
        return bre;
    }

    /**
     * parameter exception:TypeMismatchException
     */
    @ResponseBody
    @ExceptionHandler(value = TypeMismatchException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public BaseRspVo typeMismatchExceptionHandler(TypeMismatchException ex) {
        log.warn("catch typeMismatchException", ex);

        CodeMessageEnums cme = CodeMessageEnums.PARAM_EXCEPTION;
        cme.setMessage(ex.getMessage());
        BaseRspVo bre = new BaseRspVo(cme);
        log.warn("typeMismatchException return:{}", JsonUtils.toJSONString(bre));
        return bre;
    }


    /**
     * exceptionHandler.
     *
     * @param exc e
     */
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public BaseRspVo exceptionHandler(Exception exc) {
        log.info("catch  exception", exc);
        BaseRspVo rep = new BaseRspVo(CodeMessageEnums.SYSTEM_ERROR);
        log.warn("exception return:{}", JsonUtils.toJSONString(rep));
        return rep;
    }
}
