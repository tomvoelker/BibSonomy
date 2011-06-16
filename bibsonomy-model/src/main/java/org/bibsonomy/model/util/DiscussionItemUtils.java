package org.bibsonomy.model.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.bibsonomy.model.Comment;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Review;
import org.bibsonomy.util.StringUtils;

/**
 * @author dzo
 * @version $Id$
 */
public class DiscussionItemUtils {
	
	private static final DateFormat FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	/**
	 * Calculates a hash for a discussionItem
	 * 
	 * @param discussionItem
	 * @return the new hash
	 */
	public static String recalculateHash(final DiscussionItem discussionItem) {
		if (discussionItem instanceof Comment) {
			return recalculateHash((Comment) discussionItem);
		}
		
		if (discussionItem instanceof Review) {
			return recalculateHash((Review) discussionItem);
		}
		
		throw new IllegalArgumentException("discussion item class not supported");
	}
	
	private static String getUserAndDate(final DiscussionItem item) {
		return item.getUser().getName() + FORMAT.format(item.getDate());
	}
	
	private static String recalculateHash(final Review review) {
		final String text = review.getText() == null ? "" : review.getText();
		final String toHash = getUserAndDate(review) + text;
		return StringUtils.getMD5Hash(toHash);
	}

	private static String recalculateHash(final Comment comment) {
		final String text = comment.getText();
		final String toHash = getUserAndDate(comment) + text;
		return StringUtils.getMD5Hash(toHash);
	}
}
