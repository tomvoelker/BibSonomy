package org.bibsonomy.ibatis.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ResourceUtils {

	/**
	 * Calculates the MD5-Hash of a String s and returns it encoded as a hex
	 * string of 32 characters length.
	 * 
	 * @param s
	 *            the string to be hashed
	 * @return the MD5 hash of s as a 32 character string
	 */
	public static String hash (String s) {
    	if (s == null) {
    		return null;
    	} else {
    		String charset = "UTF-8";
    		try {
    			MessageDigest md = MessageDigest.getInstance("MD5");
    			return toHexString(md.digest(s.getBytes(charset)));
    		} catch (java.io.UnsupportedEncodingException e) {
    			return null;
    		} catch (NoSuchAlgorithmException e) {
    			return null;
    		}
    		
    	}
    	}

	/**
	 * Converts a buffer of bytes into a string of hex values.
	 * 
	 * @param buffer
	 *            array of bytes which should be converted
	 * @return hex string representation of buffer
	 */
	public static String toHexString(byte[] buffer) {
		StringBuffer result = new StringBuffer();
		int i;
		for (i = 0; i < buffer.length; i++) {
			String hex = Integer.toHexString((int) buffer[i]);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			result.append(hex.substring(hex.length() - 2));
		}
		return result.toString();
	}
}