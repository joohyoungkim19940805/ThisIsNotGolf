package com.hide_and_fps.project.config;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateRandomCodeUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(CreateRandomCodeUtil.class);
	
	
	private int keyBit = 256;
	private byte[] keySize = new byte[32];
	private SecureRandom random = new SecureRandom();
	private KeyGenerator keyGen;
	private Cipher cipher;
	
	public CreateRandomCodeUtil() {
		try {
			this.keyGen = KeyGenerator.getInstance("AES");
			this.keyGen.init(this.keyBit);
			this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			
			Key key = this.keyGen.generateKey();
			this.cipher.init(Cipher.ENCRYPT_MODE, key);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
			logger.error(e.getMessage(), e);
		}	
	}
	
	public String createCode() {
		return createCode(this.keySize);
	}
	public String createCode(byte[] keySize) {
		random.nextBytes(keySize);
		try {
			return new Base32().encodeToString(cipher.doFinal(keySize));
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
}
