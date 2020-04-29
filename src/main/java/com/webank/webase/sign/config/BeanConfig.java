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

package com.webank.webase.sign.config;

import org.fisco.bcos.web3j.crypto.ECDSASign;
import org.fisco.bcos.web3j.crypto.SHA3Digest;
import org.fisco.bcos.web3j.crypto.gm.sm3.SM3Digest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * init bean in utils
 * @author marsli
 */
@Configuration
public class BeanConfig {

	@Bean
	public ECDSASign getECDSASign() {
		return new ECDSASign();
	}

	@Bean
	public SM3Digest getSM3Digest() {
		return new SM3Digest();
	}

	@Bean
	public SHA3Digest getSHA3Digest() {
		return new SHA3Digest();
	}
}
