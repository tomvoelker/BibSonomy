package org.bibsonomy.model.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ResourceUtils {

	/** To set groupId in case of spam detection. Use logical OR to set 2nd bit */
	private static final int CONST_SET_1ST_BIT = 0x80000000;
	/** To set/clear first bit of an integer. Use logical AND to clear 2nd bit */
	private static final int CONST_CLEAR_1ST_BIT = 0x7FFFFFFF;

	/**
	 * Calculates the MD5-Hash of a String s and returns it encoded as a hex
	 * string of 32 characters length.
	 * 
	 * @param s
	 *            the string to be hashed
	 * @return the MD5 hash of s as a 32 character string
	 */
	public static String hash(final String s) {
		if (s == null) return null;

		final String charset = "UTF-8";
		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");
			return toHexString(md.digest(s.getBytes(charset)));
		} catch (final UnsupportedEncodingException e) {
			return null;
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
	public static String toHexString(final byte[] buffer) {
		final StringBuffer rVal = new StringBuffer();
		for (int i = 0, n = buffer.length; i < n; i++) {
			String hex = Integer.toHexString(buffer[i]);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			rVal.append(hex.substring(hex.length() - 2));
		}
		return rVal.toString();
	}

	/**
	 * In case of spam detection
	 */
	public static int getGroupId(final int groupId, final boolean isSpammer) {
		if (isSpammer) return groupId | CONST_SET_1ST_BIT;
		return groupId & CONST_CLEAR_1ST_BIT;
	}
}