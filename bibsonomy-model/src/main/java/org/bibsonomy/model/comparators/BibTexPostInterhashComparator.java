package org.bibsonomy.model.comparators;

import java.io.Serializable;
import java.util.Comparator;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.util.StringUtils;

/**
 * Comparator used to eliminate duplicates (when used in conjuction with a TreeSet)
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class BibTexPostInterhashComparator implements Comparator<Post<BibTex>>, Serializable {

	private static final long serialVersionUID = -8523955200241922144L;

	public int compare(final Post<BibTex> post1, final Post<BibTex> post2) {
		return StringUtils.secureCompareTo(post1.getResource().getInterHash(), post2.getResource().getInterHash());
	}
}