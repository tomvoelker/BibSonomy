package org.bibsonomy.importer.filter;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Describes a chain element which can filter {@link BibTex} posts.
 * 
 * @author rja
 * @version $Id$
 */
public interface PostFilterChainElement {

	public void filterPost(final Post<BibTex> post);
	
}
