/*
 * Copyright 2014-2021 the original author or authors.
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
package com.webank.webase.sign.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 *  constant configuration from file
 */
@Data
@Configuration
@ConfigurationProperties(prefix = ConstantProperties.CONSTANT_PREFIX)
public class ConstantProperties {
    public static final String CONSTANT_PREFIX = "constant";
    private String aesKey = "EfdsW23D23d3df43";
    private String aesPattern = "CBC";
    private int keepAliveRequests = 100;
    private int syncUsrCacheTaskFixedDelay = 10000;
    private boolean supportPrivateKeyTransfer = true;
}
