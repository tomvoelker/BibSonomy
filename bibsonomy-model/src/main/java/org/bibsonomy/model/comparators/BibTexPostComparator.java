/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.DateUtils;
import org.bibsonomy.util.StringUtils;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * Comparator used to sort bibtex posts
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class BibTexPostComparator implements Comparator<Post<BibTex>>, Serializable {

	private static final long serialVersionUID = 1L;
	private List<SortCriterium> sortCriteria = new ArrayList<SortCriterium>();

	/** Helper structure to bind a sort key to a sort order */
	private class SortCriterium {
		/** sort key */
		public final SortKey sortKey;
		/** sort order */
		public final SortOrder sortOrder;
		/**
		 * Constructor
		 * @param key 
		 * @param order
		 */
		public SortCriterium(final SortKey key, final SortOrder order) {
			this.sortKey = key;
			this.sortOrder = order;
		}
	}	

	/** helper exception */
	private class SortKeyIsEqualException extends Exception {
		private static final long serialVersionUID = 1L;		
	}

	/**
	 * instantiate comparator
	 * 
	 * @param sortKeys
	 * @param sortOrders
	 */
	public BibTexPostComparator(final List<SortKey> sortKeys, final List<SortOrder> sortOrders) {
		for (int i = 0; i <= sortKeys.size() - 1; i++) {
			try {
				this.sortCriteria.add(new SortCriterium(sortKeys.get(i), sortOrders.get(i)));
			} catch (IndexOutOfBoundsException ignore) {
				// fill up with default ascending order
				this.sortCriteria.add(new SortCriterium(sortKeys.get(i), SortOrder.ASC));
			}
		}
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
				if (crit.sortKey.equals(SortKey.AUTHOR)) {
					// if author not present, take editor
					final String personList1 = ( present(post1.getResource().getAuthor()) ? post1.getResource().getAuthor() : post1.getResource().getEditor() );
					final String personList2 = ( present(post2.getResource().getAuthor()) ? post2.getResource().getAuthor() : post2.getResource().getEditor() );
					return this.nomalizeAndCompare(BibTexUtils.getFirstPersonsLastName(personList1), BibTexUtils.getFirstPersonsLastName(personList2), crit.sortOrder);
				}
				// year
				else if (crit.sortKey.equals(SortKey.YEAR)) {
					return this.compare(BibTexUtils.getYear(post1.getResource().getYear()), BibTexUtils.getYear(post2.getResource().getYear()), crit.sortOrder);
				}
				// editor
				else if (crit.sortKey.equals(SortKey.EDITOR)) {
					return this.nomalizeAndCompare(BibTexUtils.getFirstPersonsLastName(post1.getResource().getEditor()), BibTexUtils.getFirstPersonsLastName(post2.getResource().getEditor()), crit.sortOrder);				
				}
				// entrytype
				else if (crit.sortKey.equals(SortKey.ENTRYTYPE)) {
					return this.nomalizeAndCompare(post1.getResource().getEntrytype(), post2.getResource().getEntrytype(), crit.sortOrder);
				}
				// title
				else if (crit.sortKey.equals(SortKey.TITLE)) {
					return this.nomalizeAndCompare(post1.getResource().getTitle(), post2.getResource().getTitle(), crit.sortOrder);
				}		
				// booktitle
				else if (crit.sortKey.equals(SortKey.BOOKTITLE)) {
					return this.nomalizeAndCompare(post1.getResource().getBooktitle(), post2.getResource().getBooktitle(), crit.sortOrder);
				}			
				// school
				else if (crit.sortKey.equals(SortKey.SCHOOL)) {
					return this.nomalizeAndCompare(post1.getResource().getSchool(), post2.getResource().getSchool(), crit.sortOrder);
				}
				// posting date
				else if (crit.sortKey.equals(SortKey.DATE)) {
					return this.compare(post1.getDate(), post2.getDate(), crit.sortOrder);
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

	/**
	 * Compare two strings following a specified order
	 * 
	 * @param s1 first string
	 * @param s2 second string
	 * @param order sort order
	 * @return an int comparison value
	 * @throws SortKeyIsEqualException 
	 */
	private int nomalizeAndCompare(String s1, String s2, final SortOrder order) throws SortKeyIsEqualException {
		// normalization
		if (present(s1)) s1 = BibTexUtils.cleanBibTex(s1).trim();
		if (present(s2)) s2 = BibTexUtils.cleanBibTex(s2).trim();
		// comparison
		int comp = 0;
		if (order.equals(SortOrder.ASC)) {
			comp = StringUtils.secureCompareTo(s1, s2);
		} else {
			comp = StringUtils.secureCompareTo(s2, s1);
		}
		if (comp == 0) throw new SortKeyIsEqualException();
		return comp;
	}

	/**
	 * Compare two integers following a specified order
	 * 
	 * @param i1 first integer
	 * @param i2 second integer
	 * @param order sort order
	 * @return an int comparison value
	 * @throws SortKeyIsEqualException 
	 */
	private int compare (final int i1, final int i2, final SortOrder order) throws SortKeyIsEqualException {
		int comp = 0;
		if (order.equals(SortOrder.ASC)) {
			comp = i1 - i2;
		} else {
			comp = i2 - i1;
		}
		if (comp == 0) throw new SortKeyIsEqualException();
		return comp;
	}	
	
	/**
	 * Compare two dates following a specified order
	 * 
	 * @param d1 first date
	 * @param d2 second date
	 * @param order sort order
	 * @return an int comparison value
	 * @throws SortKeyIsEqualException
	 */
	private int compare (final Date d1, final Date d2, final SortOrder order) throws SortKeyIsEqualException {
		int comp = 0;
		if (order.equals(SortOrder.ASC)) {
			comp = DateUtils.secureCompareTo(d1, d2);
		}
		else {
			comp = DateUtils.secureCompareTo(d2, d1);
		}
		if (comp == 0) throw new SortKeyIsEqualException();
		return comp;
	}
}