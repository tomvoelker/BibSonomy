package org.bibsonomy.model.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.bibsonomy.util.StringUtils;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class UserUtils {

	/**
	 * Generates an Api key with a MD5 message digest from a random number.
	 * 
	 * @return String Api key
	 */
	public static String generateApiKey() {
		try {
			final byte[] randomArray = generateRandom();
			final MessageDigest md = MessageDigest.getInstance("MD5");
			return StringUtils.toHexString(md.digest(randomArray));
		} catch (final NoSuchAlgorithmException e) {
			// should never happen
			throw new RuntimeException(e);
		}
	}

	private static byte[] generateRandom() {
		final byte[] randomBytes = new byte[32];
		try {
			final Random random = new Random();
			random.nextBytes(randomBytes);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
		return randomBytes;
	}
}