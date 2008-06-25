package org.bibsonomy.importer.filter;

import java.util.HashSet;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;

/**
 * Removes duplicate tags. 
 * 
 * @author rja
 * @version $Id$
 */
public class KeywordDuplicateRemovalFilter implements PostFilterChainElement {

	/** Removes duplicate tags.
	 * 
	 * @see org.bibsonomy.importer.filter.PostFilterChainElement#filterPost(org.bibsonomy.model.Post)
	 */
	public void filterPost(final Post<BibTex> post) {
		post.setTags(new HashSet<Tag>(post.getTags()));
	}

}
