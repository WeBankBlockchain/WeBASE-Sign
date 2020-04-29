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

package com.webank.webase.sign.util;

import com.webank.webase.sign.enums.EncryptTypes;
import org.fisco.bcos.web3j.crypto.ECDSASign;
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.Sign;
import org.fisco.bcos.web3j.crypto.gm.sm2.SM2Sign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author marsli
 */
@Component
public class SignUtils {

	@Autowired
	private ECDSASign ecdsaSign;

	/**
	 * get signature data  by encrypt type
	 * @param message message to be signed
	 * @param keyPair keyPair for sign
	 * @param encryptType 1: guomi, 0: standard
	 * @return
	 */
	public Sign.SignatureData signMessageByType(byte[] message, ECKeyPair keyPair, int encryptType) {
		if (encryptType == EncryptTypes.GUOMI.getValue()) {
			return SM2Sign.sign(message, keyPair);
		} else {
			return ecdsaSign.signMessage(message, keyPair);
		}
	}
}
