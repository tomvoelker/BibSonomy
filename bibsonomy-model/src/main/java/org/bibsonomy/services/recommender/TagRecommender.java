package org.bibsonomy.services.recommender;

import java.util.SortedSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;

/**
 * This interface provides methods to get tag recommendations given posts.
 * 
 * We don't provide a method to recommend tags given a prefix of a tag, i.e., 
 * to recommend matching tags during the user types them. This must be done
 * on the client using JavaScript and the user's tag cloud. 
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
	public SortedSet<RecommendedTag> getRecommendedTags(final Post<? extends Resource> post);
	
	/** To the given list of recommended tags, the recommender shall add further tags.
	 * If it adds them at the end, at the beginning, or else, is left to the implementation.
	 * <br/>
	 * If given an empty list, recommendedTags should contain the result of {@link #getRecommendedTags(Post)}.
	 * 
	 * @see #getRecommendedTags(Post)
	 * @param recommendedTags A list of recommended tags (from another recommender). Might be empty.
	 * @param post
	 */
	public void addRecommendedTags(final SortedSet<RecommendedTag> recommendedTags, final Post<? extends Resource> post);
	
	
	/** Provide some short information about this recommender.
	 * 
	 * @return A short string describing the recommender.
	 */
	public String getInfo();
	
}
