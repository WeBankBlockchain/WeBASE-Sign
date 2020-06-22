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
package com.webank.webase.sign.aspect;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import com.webank.webase.sign.util.JsonUtils;
import org.springframework.stereotype.Component;
import com.webank.webase.sign.manager.LoggerManager;
import org.springframework.validation.BindingResult;

@Aspect
@Component
public class LogAspect {

    private final String POINT_CUT = "execution(public * com.webank.webase.sign.api.controller.*.*(..))";

    @Pointcut(POINT_CUT)
    public void logPointCut() {
    }


    @Around("logPointCut()")
    public Object methodAround(ProceedingJoinPoint point) throws Throwable {
        Instant startTime = Instant.now();
        Class targetClass = point.getTarget().getClass();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        String methodName = methodSignature.getName();
        Object[] args = point.getArgs();
        Logger logger = LoggerManager.getLogger(targetClass);
        // log args of param in controller
        // if args contains BindingResult(recursive of request entity and itself), stack over flow
        logger.debug("startTime:{} methodName:{} args:{}", startTime, methodName,
            JsonUtils.toJSONString(this.excludeBindingResult(args)));
        Object result = null;
        try {
            result = point.proceed();
        } catch (Throwable throwable) {
            logger.warn("fail request. methodName:{} ", methodName, throwable);
            throw throwable;
        }

        String resultStr = Optional.ofNullable(result).map(JsonUtils::toJSONString).orElse(null);
        logger.debug("methodName:{} usedTime:{} result:{}", methodName,
            Duration.between(startTime, Instant.now()), resultStr);
        return result;
    }

    private List<Object> excludeBindingResult(Object[] params) {
        List<Object> retainParams = new ArrayList<>();
        for (int index = 0; index < params.length; index++) {
            if (!(params[index] instanceof BindingResult)) {
                retainParams.add(params[index]);
            }
        }
        return retainParams;
    }
}
