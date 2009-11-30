/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model.comparators;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;

/**
 * Comparator used to sort bibtex posts
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class BookmarkPostComparator extends PostComparator implements Comparator<Post<Bookmark>>, Serializable {

	/**
	 * Constructor
	 * 
	 * @param sortKeys
	 * @param sortOrders
	 */
	public BookmarkPostComparator(final List<SortKey> sortKeys, final List<SortOrder> sortOrders) {
		super(sortKeys, sortOrders);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * 
	 * main comparison method
	 */
	public int compare(final Post<Bookmark> post1, final Post<Bookmark> post2) {
		for (final SortCriterium crit : this.sortCriteria) {
			try {				
				// posting date
				if (SortKey.DATE.equals(crit.sortKey)) {
					return this.compare(post1.getDate(), post2.getDate(), crit.sortOrder);
				}
				// ranking
				else if (SortKey.RANKING.equals(crit.sortKey)) {
					return this.compare(post1.getRanking(), post2.getRanking(), crit.sortOrder);
				}
				else {
					return 0;
				}
			}
			catch (SortKeyIsEqualException ignore) {
				// the for-loop will jump to the next sort criterium in this case
			}
		}
		return 0;
	}

}