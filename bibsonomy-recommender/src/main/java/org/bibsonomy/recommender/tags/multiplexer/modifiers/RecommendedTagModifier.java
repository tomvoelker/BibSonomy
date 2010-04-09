package org.bibsonomy.recommender.tags.multiplexer.modifiers;

import java.util.Collection;

import org.bibsonomy.model.RecommendedTag;

/**
 * Tag modifiers arbitrarily change a recommended tag's content.
 * 
 * @author fei
 * @version $Id$
 */
public interface RecommendedTagModifier {

	/**
	 * Tag modifiers arbitrarily change a recommended tag's content.
	 * 
	 * @param tags collection of recommended tags to filter
	 */
	public void alterTags(Collection<RecommendedTag> tags);

}