package org.bibsonomy.model.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class UserUtils {

	/**
	 * @return String Api Key using a random number
	 */

	public static String generateApiKey() {

		byte[] randomArray = generateRandom();

		try {
			final MessageDigest md = MessageDigest.getInstance("MD5");

			return ResourceUtils.toHexString(md.digest(randomArray));
		} catch (final NoSuchAlgorithmException e) {
			return null;
		}

	}

	private static byte[] generateRandom() {

		byte[] randomBytes = new byte[32];
		try {
			Random random = new Random();
			random.nextBytes(randomBytes);
		}

		catch (Exception e) {
			e.printStackTrace();

		}

		return randomBytes;
	}

}
