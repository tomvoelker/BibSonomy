/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.bibsonomy.model.Comment;
import org.bibsonomy.model.DiscussionItem;
import org.bibsonomy.model.Review;
import org.bibsonomy.util.StringUtils;

/**
 * @author dzo
 */
public class DiscussionItemUtils {
	
	
	private static final String FORMAT_STRING = "yyyy.MM.dd HH:mm:ss";
	
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
		DateFormat fmt = new SimpleDateFormat(FORMAT_STRING);
		return item.getUser().getName() + fmt.format(item.getDate());
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
