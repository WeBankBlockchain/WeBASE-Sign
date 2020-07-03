/**
 * Copyright 2014-2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.webase.sign.pojo.vo;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * import private key entity
 * @author marsli
 */
@Data
public class ReqNewUserVo {
	@NotBlank
	private String signUserId;
	@NotBlank
	private String appId;
	private Integer encryptType;
	/**
	 * encoded by base64
	 */
	@NotBlank
	private String privateKey;
}
