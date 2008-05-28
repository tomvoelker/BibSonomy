package org.bibsonomy.importer.filter;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;

/**
 * Splits all tags at whitespace positions.
 * 
 * @author rja
 * @version $Id$
 */
public class KeywordSplitFilter implements PostFilterChainElement {

	public void filterPost(final Post<BibTex> post) {
		final List<Tag> oldTags = post.getTags();
		final List<Tag> newTags = new LinkedList<Tag>();
		
		for (final Tag oldTag: oldTags) {
			/*
			 * split tag
			 */
			final String[] splittedTag = oldTag.getName().split("\\s");
			for (final String newTag: splittedTag) {
				/*
				 * add new tag 
				 */
				final Tag tag = new Tag();
				tag.setName(newTag);
				newTags.add(tag);
			}
		}
		post.setTags(newTags);
		
	}

}
