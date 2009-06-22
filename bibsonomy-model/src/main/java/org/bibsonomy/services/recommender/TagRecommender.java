package org.bibsonomy.services.recommender;

import java.util.Collection;
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
	 * The post should contain valid intra- and inter-hashes as well as the name of the posting 
	 * user. The method {@link Post#getContentId()} can be called to get a unique id for this
	 * post which later can be used by {@link #setFeedback(Post)} to connect the two posts.  
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
	
	/** Into the given collection, the recommender shall add its recommended tags.
	 * Then, recommendedTags should contain the result of {@link #getRecommendedTags(Post)}.
	 * <br/>
	 * The reason for having this method in addition to {@link #getRecommendedTags(Post)} is
	 * to allow use of special collections and comparators.  
	 * 
	 * @see #getRecommendedTags(Post)
	 * @param recommendedTags An empty collection.
	 * @param post
	 */
	public void addRecommendedTags(final Collection<RecommendedTag> recommendedTags, final Post<? extends Resource> post);
	
	/**
	 * Finishes the recommendation process and provides as feedback the post as 
	 * it will be stored. The method {@link Post#getContentId()} provides an ID
	 * to connect this post with the one of the {@link #getRecommendedTags(Post)}
	 * call.
	 * 
	 * @param post The complete post as it will be stored.
	 */
	public void setFeedback(final Post<? extends Resource> post);
	
	
	/** Provide some short information about this recommender.
	 * 
	 * @return A short string describing the recommender.
	 */
	public String getInfo();
	
}
