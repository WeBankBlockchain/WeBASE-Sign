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
import org.fisco.bcos.web3j.crypto.ECKeyPair;
import org.fisco.bcos.web3j.crypto.SHA3Digest;
import org.fisco.bcos.web3j.crypto.gm.sm3.SM3Digest;
import org.fisco.bcos.web3j.utils.Numeric;
import org.fisco.bcos.web3j.utils.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;


/**
 * get address by encrypt type
 * @author marsli
 */
@Component
public class AddressUtils {

	@Autowired
	private SHA3Digest sha3Digest;
	@Autowired
	private SM3Digest sm3Digest;

	private static final int PUBLIC_KEY_SIZE = 64;

	private static final int ADDRESS_SIZE = 160;
	private static final int ADDRESS_LENGTH_IN_HEX = ADDRESS_SIZE >> 2;

	private static final int PUBLIC_KEY_LENGTH_IN_HEX = PUBLIC_KEY_SIZE << 1;

	/**
	 *
	 * @param ecKeyPair ECKeyPair
	 * @param encryptType 1: guomi, 0: standard
	 * @return
	 */
	public String getAddressByType(ECKeyPair ecKeyPair, int encryptType) {
		return getAddressByType(ecKeyPair.getPublicKey(), encryptType);
	}

	/**
	 * @param publicKey BigInteger
	 * @param encryptType 1: guomi, 0: standard
	 * @return
	 */
	public String getAddressByType(BigInteger publicKey, int encryptType) {
		String publicKeyHex = Numeric.toHexStringWithPrefixZeroPadded(publicKey, PUBLIC_KEY_LENGTH_IN_HEX);
		return getAddressByType(publicKeyHex, encryptType);
	}

	/**
	 * @param publicKeyHex String in hex
	 * @param encryptType 1: guomi, 0: standard
	 * @return
	 */
	public String getAddressByType(String publicKeyHex, int encryptType) {
		String publicKeyHexNoPrefix = Numeric.cleanHexPrefix(publicKeyHex);

		if (publicKeyHexNoPrefix.length() < PUBLIC_KEY_LENGTH_IN_HEX) {
			publicKeyHexNoPrefix =
					Strings.zeros(PUBLIC_KEY_LENGTH_IN_HEX - publicKeyHexNoPrefix.length())
							+ publicKeyHexNoPrefix;
		}
		String hash = hashByType(publicKeyHexNoPrefix, encryptType);
		// right most 160 bits
		return hash.substring(hash.length() - ADDRESS_LENGTH_IN_HEX);
	}

	/**
	 * @param publicKey byte[]
	 * @param encryptType 1: guomi, 0: standard
	 * @return
	 */
	public byte[] getAddressByType(byte[] publicKey, int encryptType) {
		byte[] hash = hashByType(publicKey, encryptType);
		// right most 160 bits
		return Arrays.copyOfRange(hash, hash.length - 20, hash.length);
	}

	/**
	 * get hash value from hex string input by encrypt type
	 * @param hexInput string in hex
	 * @param encryptType 1: guomi, 0: standard
	 * @return
	 */
	public String hashByType(String hexInput, int encryptType) {
		if (encryptType == EncryptTypes.GUOMI.getValue()) {
			return sm3Digest.hash(hexInput);
		} else {
			return sha3Digest.hash(hexInput);
		}
	}

	/**
	 *
	 * @param input byte[]
	 * @param encryptType 1: guomi, 0: standard
	 * @return
	 */
	public  byte[] hashByType(byte[] input, int encryptType) {
		if (encryptType == EncryptTypes.GUOMI.getValue()) {
			return sm3Digest.hash(input);
		} else {
			return sha3Digest.hash(input);
		}
	}
}
