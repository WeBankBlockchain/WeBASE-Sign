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
package com.webank.webase.sign.aspect;

import com.alibaba.fastjson.JSON;
import com.webank.webase.sign.manager.LoggerManager;
import java.lang.reflect.Method;
import java.time.Instant;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

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
        MethodSignature methodSignature = (MethodSignature)point.getSignature();
        Method method = methodSignature.getMethod();
        methodSignature.getName();
        String methodName = methodSignature.getName();
        Object[] args = point.getArgs();
        Logger logger = LoggerManager.getLogger(targetClass);
        logger.info("start time:{} class:{}", startTime);
        logger.info("start class:{}", targetClass);
        logger.info("start methodName:{}", methodName);
        logger.info("start args:{}", JSON.toJSONString(args));
        point.proceed();

        return null;
    }

}
