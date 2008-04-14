package org.bibsonomy.model.comparators;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.StringUtils;

/**
 * Comparator used to sort bibtex posts
 * 
 * @author dbenz
 * @version $Id$
 */
public class BibTexPostComparator implements Comparator<Post<BibTex>>, Serializable {
	
	private static final long serialVersionUID = 1L;
	private List<SortCriterium> sortCriteria = new ArrayList<SortCriterium>();
	
	/** helper structure to bind a sort key to a sort order */
	private class SortCriterium {
		/** sort key */
		public SortKey sortKey;
		/** sort order */
		public SortOrder sortOrder;
		/** constructor */
		public SortCriterium(final SortKey key, final SortOrder order) {
			this.sortKey = key;
			this.sortOrder = order;
		}
	}	
	
	/** helper exception */
	private class sortKeyIsEqualException extends Exception {
		private static final long serialVersionUID = 1L;		
	}
	
	/**
	 * instantiate comparator
	 * 
	 * @param sortKeys
	 * @param sortOrders
	 * 
	 */
	public BibTexPostComparator(final List<SortKey> sortKeys, final List<SortOrder> sortOrders) {
		for (int i = 0; i <= sortKeys.size() - 1; i++) {
			try {
				this.sortCriteria.add( new SortCriterium(sortKeys.get(i), sortOrders.get(i)) );
			}
			catch (IndexOutOfBoundsException iob) {
				// fill up with default ascending order
				this.sortCriteria.add( new SortCriterium(sortKeys.get(i), SortOrder.DESC ) );
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 * 
	 * main comparison method
	 */
	public int compare(final Post<BibTex> o1, final Post<BibTex> o2) {
		// TODO Auto-generated method stub
		for (SortCriterium crit : this.sortCriteria) {
			try {
				// author
				if (crit.sortKey.equals(SortKey.AUTHOR)) {
					return this.compare(BibTexUtils.getFirstPersonsLastName(o1.getResource().getAuthor()), BibTexUtils.getFirstPersonsLastName(o2.getResource().getAuthor()), crit.sortOrder);
				}
				// year
				else if (crit.sortKey.equals(SortKey.YEAR)) {
					return this.compare(BibTexUtils.getYear(o1.getResource().getYear()), BibTexUtils.getYear(o2.getResource().getYear()), crit.sortOrder);
				}
				// editor
				else if (crit.sortKey.equals(SortKey.EDITOR)) {
					return this.compare(BibTexUtils.getFirstPersonsLastName(o1.getResource().getEditor()), BibTexUtils.getFirstPersonsLastName(o2.getResource().getEditor()), crit.sortOrder);				
				}
				// entrytype
				else if (crit.sortKey.equals(SortKey.ENTRYTYPE)) {
					return this.compare(o1.getResource().getEntrytype(), o2.getResource().getEntrytype(), crit.sortOrder);
				}
				// title
				else if (crit.sortKey.equals(SortKey.TITLE)) {
					return this.compare(o1.getResource().getTitle(), o2.getResource().getTitle(), crit.sortOrder);
				}		
				// booktitle
				else if (crit.sortKey.equals(SortKey.BOOKTITLE)) {
					return this.compare(o1.getResource().getBooktitle(), o2.getResource().getBooktitle(), crit.sortOrder);
				}			
				// school
				else if (crit.sortKey.equals(SortKey.SCHOOL)) {
					return this.compare(o1.getResource().getSchool(), o2.getResource().getSchool(), crit.sortOrder);
				}
				else {
					return 0;
				}
			}
			catch (sortKeyIsEqualException e) {
				// the for-loop will jump to the next sort criterium in this case
			}
		}
		return 0;
	}
	
	
	/**
	 * compare two strings following a specified order
	 * 
	 * @param s1 first string
	 * @param s2 second string
	 * @param order sort order
	 * @return an int comparison value
	 * @throws sortKeyIsEqualException 
	 */
	private int compare (final String s1, final String s2, final SortOrder order) throws sortKeyIsEqualException {
		int comp = 0;
		if (order.equals(SortOrder.ASC)) {
			comp = StringUtils.secureCompareTo(s1, s2);
		}
		else {
			comp = StringUtils.secureCompareTo(s2, s1);
		}
		if ( comp == 0 ) throw new sortKeyIsEqualException();
		return comp;
	}
	
	/**
	 * compare two integers following a specified order
	 * 
	 * @param i1 first integer
	 * @param i2 second integer
	 * @param order sort order
	 * @return an int comparison value
	 * @throws sortKeyIsEqualException 
	 */
	private int compare (final int i1, final int i2, final SortOrder order) throws sortKeyIsEqualException {
		int comp = 0;
		if (order.equals(SortOrder.ASC)) {
			comp = i1 - i2;
		}
		else {
			comp = i2 - i1;
		}
		if ( comp == 0 ) throw new sortKeyIsEqualException();
		return comp;
	}

	
}
