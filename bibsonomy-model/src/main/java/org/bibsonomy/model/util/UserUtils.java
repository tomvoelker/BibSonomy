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
		
	/**
	 * Transforms groupid in dependence of spammer status of a user
	 * @param groupid Id to transform
	 * @param isSpammer
	 * @return new groupId
	 */
	public static int getGroupId(int groupid, boolean isSpammer) {
		/*const to set/clear first bit of an integer*/
		final int CONST_SET_1ST_BIT    = 0x80000000; //use logical OR  (|) to set second bit
		final int CONST_CLEAR_1ST_BIT  = 0x7FFFFFFF; //use logical AND (&) to clear second bit
		
		if (isSpammer) {
			return groupid | CONST_SET_1ST_BIT;			
		} else {
			// NOTE: "return groupid" is not enough, since we want to use that to unflag spammers posts, as well 
			return groupid & CONST_CLEAR_1ST_BIT;
		}
	}
}