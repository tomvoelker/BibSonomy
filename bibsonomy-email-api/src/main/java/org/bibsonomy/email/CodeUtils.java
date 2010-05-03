package org.bibsonomy.email;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class CodeUtils {

	public static byte[] convertToByte(final String s) throws DecoderException {
		/*
		 * decode into bytes
		 */
		return Hex.decodeHex(s.toCharArray());
	}
	
	public static String toHexString(byte[] bytes) {
		final StringBuffer result = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i]);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			result.append(hex.substring(hex.length() - 2));
		}
		return result.toString();
	}

	public static String convertToBase64(final String s) {
		try {
			return convertToBase64(s.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return convertToBase64(s.getBytes());
		}
	}
	
	public static String convertToBase64(final byte[] bytes) {
		return new String(Base64.encodeBase64(bytes));
		
	}
}
