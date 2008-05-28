package org.bibsonomy.importer.filter;

import java.util.List;

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
		final List<Tag> tags = post.getTags();
		
		for (final Tag tag: tags) {
			tag.setName(tag.getName().toLowerCase());
		}
		
	}

}
