package org.bibsonomy.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author rja
 * @version $Id$
 */
public class HashUtils {
	/**
	 * Calculates the MD5-Hash of a byte array and returns it encoded as a hex
	 * string of 32 characters length.
	 * 
	 * @param data
	 *            the byte array to be hashed
	 * @return the MD5 hash of s as a 32 character string
	 */
	public static String getMD5Hash(final byte[] data) {
		if (data == null) return null;

		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			return toHexString(md.digest(data));
		} catch (final NoSuchAlgorithmException e) {
			return null;
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
		final StringBuffer result = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
			String hex = Integer.toHexString(buffer[i]);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			result.append(hex.substring(hex.length() - 2));
		}
		return result.toString();
	}
	
}
