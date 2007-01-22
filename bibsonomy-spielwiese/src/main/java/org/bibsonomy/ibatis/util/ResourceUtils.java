package org.bibsonomy.ibatis.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Tag;

import question.generate.resource.Tags;
import recommender.TagVectorUpdater;
import recommender.db.backend.Database;
import recommender.db.backend.DatabaseAction;
import recommender.db.operations.tags.GetTagIds;
import recommender.db.operations.tagvector.AbstractGetVectorEntries;
import recommender.db.operations.tagvector.DecTagVectorSpace;
import recommender.db.operations.tagvector.IncTagVectorSpace;
import recommender.db.operations.tagvector.MarkModified;
import resources.Resource;

public class ResourceUtils {
	
    /*** to set Group_id in case of spamm detection ***/
	/*** const to set/clear first bit of an integer***/
	public static final int CONST_SET_1ST_BIT    = 0x80000000; //use logical OR  (|) to set second bit
	public static final int CONST_CLEAR_1ST_BIT  = 0x7FFFFFFF; //use logical AND (&) to clear second bit
	private static final Logger log = Logger.getLogger(TagVectorUpdater.class);
	/**
	 * Calculates the MD5-Hash of a String s and returns it encoded as a hex
	 * string of 32 characters length.
	 * 
	 * @param s
	 *            the string to be hashed
	 * @return the MD5 hash of s as a 32 character string
	 */
	public static String hash(final String s) {
		if (s == null) {
			return null;
		} else {
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
	}

	/**
	 * Converts a buffer of bytes into a string of hex values.
	 * 
	 * @param buffer
	 *            array of bytes which should be converted
	 * @return hex string representation of buffer
	 */
	public static String toHexString(final byte[] buffer) {
		final StringBuffer result = new StringBuffer();
		for (int i = 0, n = buffer.length; i < n; i++) {
			String hex = Integer.toHexString((int) buffer[i]);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			result.append(hex.substring(hex.length() - 2));
		}
		return result.toString();
	}
	
	/**  in case of spamm detection **/
	public static int getGroupid(int groupid, boolean isSpammer) {
		if (isSpammer) {
			return groupid | CONST_SET_1ST_BIT;			
		} else {
			// NOTE: "return groupid" is not enough, since we want to use that to unflag spammers posts, as well 
			return groupid & CONST_CLEAR_1ST_BIT;
		}
	}
	/*** TODO implement doUpdate Recommender ***/
	public static void doUpdate(final List<Tag> oldResourceTags, final Bookmark bookmark) {
	
}
}