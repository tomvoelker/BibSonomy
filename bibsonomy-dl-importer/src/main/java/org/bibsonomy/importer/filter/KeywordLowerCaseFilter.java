package org.bibsonomy.importer.filter;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;

/**
 * Makes all tags lower case.
 * 
 * @author rja
 * @version $Id$
 */
public class KeywordLowerCaseFilter implements PostFilterChainElement {

	/** Makes all tags lower case.
	 * 
	 * @see org.bibsonomy.importer.filter.PostFilterChainElement#filterPost(org.bibsonomy.model.Post)
	 */
	public void filterPost(final Post<BibTex> post) {
		for (final Tag tag: post.getTags()) {
			tag.setName(tag.getName().toLowerCase());
		}
		
	}

}
