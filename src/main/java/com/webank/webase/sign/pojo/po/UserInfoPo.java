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
package com.webank.webase.sign.pojo.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * UserInfo Plain Object of tb_user table
 */
@Data
public class UserInfoPo {
	/**
	 * id of table tb_user
	 */
	private Integer userId;
	/**
	 * business user id
	 */
    private String signUserId;
	/**
	 * app that user belong to
	 */
	private String appId;
	private String address;
	private String publicKey;
	private String privateKey;
    private String description;
	/**
	 * 0 is standard, 1 is guomi
	 */
	private Integer encryptType;

	private String status;

	private LocalDateTime gmtModify;
}
