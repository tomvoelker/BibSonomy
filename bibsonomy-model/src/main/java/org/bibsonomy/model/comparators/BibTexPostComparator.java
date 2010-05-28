/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
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
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;

/**
 * Comparator used to sort bibtex posts
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class BibTexPostComparator extends PostComparator implements Comparator<Post<BibTex>>, Serializable {
	private static final long serialVersionUID = 8550700973763853912L;

	/**
	 * Constructor
	 * 
	 * @param sortKeys
	 * @param sortOrders
	 */
	public BibTexPostComparator(final List<SortKey> sortKeys, final List<SortOrder> sortOrders) {
		super(sortKeys, sortOrders);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * 
	 * main comparison method
	 */
	public int compare(final Post<BibTex> post1, final Post<BibTex> post2) {
		for (final SortCriterium crit : this.sortCriteria) {
			try {
				// author
				if (SortKey.AUTHOR.equals(crit.sortKey)) {
					// if author not present, take editor
					final String personList1 = ( present(post1.getResource().getAuthor()) ? post1.getResource().getAuthor() : post1.getResource().getEditor() );
					final String personList2 = ( present(post2.getResource().getAuthor()) ? post2.getResource().getAuthor() : post2.getResource().getEditor() );
					return this.nomalizeAndCompare(BibTexUtils.getFirstPersonsLastName(personList1), BibTexUtils.getFirstPersonsLastName(personList2), crit.sortOrder);
				}
				// year
				else if (SortKey.YEAR.equals(crit.sortKey)) {
					return this.compare(BibTexUtils.getYear(post1.getResource().getYear()), BibTexUtils.getYear(post2.getResource().getYear()), crit.sortOrder);
				}
				// editor
				else if (SortKey.EDITOR.equals(crit.sortKey)) {
					return this.nomalizeAndCompare(BibTexUtils.getFirstPersonsLastName(post1.getResource().getEditor()), BibTexUtils.getFirstPersonsLastName(post2.getResource().getEditor()), crit.sortOrder);				
				}
				// entrytype
				else if (SortKey.ENTRYTYPE.equals(crit.sortKey)) {
					return this.nomalizeAndCompare(post1.getResource().getEntrytype(), post2.getResource().getEntrytype(), crit.sortOrder);
				}
				// title
				else if (SortKey.TITLE.equals(crit.sortKey)) {
					return this.nomalizeAndCompare(post1.getResource().getTitle(), post2.getResource().getTitle(), crit.sortOrder);
				}		
				// booktitle
				else if (SortKey.BOOKTITLE.equals(crit.sortKey)) {
					return this.nomalizeAndCompare(post1.getResource().getBooktitle(), post2.getResource().getBooktitle(), crit.sortOrder);
				}			
				// school
				else if (SortKey.SCHOOL.equals(crit.sortKey)) {
					return this.nomalizeAndCompare(post1.getResource().getSchool(), post2.getResource().getSchool(), crit.sortOrder);
				}
				// posting date
				else if (SortKey.DATE.equals(crit.sortKey)) {
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