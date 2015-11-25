/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.lucene.param.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.apache.lucene.document.Document;
import org.bibsonomy.lucene.index.LuceneFieldNames;

/**
 * comparator for avoiding duplicates in index-update-cache
 * 
 * FIXME: we have to consider the case, that a post is updated between
 *        two update sessions, after the user was unflagged as spammer
 *        THIS UPDATE WILL BE LOST!!!
 * 
 * @author fei
 */
public class DocumentCacheComparator implements Comparator<Document>, Serializable {
	private static final long serialVersionUID = -5135628928597791434L;

	@Override
	public int compare(final Document o1, final Document o2) {
		
		// most important: treat documents as equal, if their content_ids conincide
		final String id1 = o1.get(LuceneFieldNames.CONTENT_ID);
		final String id2 = o2.get(LuceneFieldNames.CONTENT_ID);
		if ((id1 != null) && (id2 != null) && (id1.equals(id2)) )
			return 0;
		
		// otherwise order according to the date field
		try {
			final Long date1 = Long.parseLong(o1.get(LuceneFieldNames.LAST_LOG_DATE));
			final Long date2 = Long.parseLong(o2.get(LuceneFieldNames.LAST_LOG_DATE));
			if ((date1 != null) && (date2 != null)) {
				final int cmp = date1.compareTo(date2);
				
				return cmp == 0 ? -1 : cmp;
			}
		} catch(final NumberFormatException e) {
			return -1;
		}
		
		// fallback
		return -1;
	}
}
