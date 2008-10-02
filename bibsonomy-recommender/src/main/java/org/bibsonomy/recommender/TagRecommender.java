package org.bibsonomy.recommender;

import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

/**
 * This interface provides methods to get tag recommendations given posts.
 * 
 * @author rja
 * @version $Id$
 */
public interface TagRecommender {

	/**
	 * Provide tag recommendations for the given post.
	 * 
	 * @param post A post describing the resource for which recommendations should be produced.
	 * The post should contain valid intra- and inter-hashes as well as the name of the posting user.
	 * <br/>
	 * The post might also contain already some tags (e.g., when the user changes a post) and the
	 * recommender should/could take those into account.
	 * 
	 * @return A list of recommended tags in descending order of their relevance.
	 * 
	 * TODO: do we need weights for tags to allow composition of recommenders?
	 * 
	 */
	public List<Tag> getRecommendedTags(final Post<? extends Resource> post);
	
	/** To the given list of recommended tags, the recommender shall add further tags.
	 * If it adds them at the end, at the beginning, or else, is left to the implementation.
	 * <br/>
	 * If given an empty list, recommendedTags should contain the result of {@link #getRecommendedTags(Post)}.
	 * 
	 * @see #getRecommendedTags(Post)
	 * @param recommendedTags A list of recommended tags (from another recommender). Might be empty.
	 * @param post
	 */
	public void addRecommendedTags(final List<Tag> recommendedTags, final Post<? extends Resource> post);
	
	
	/** Provide some short information about this recommender.
	 * 
	 * @return A short string describing the recommender.
	 */
	public String getInfo();
	
}
