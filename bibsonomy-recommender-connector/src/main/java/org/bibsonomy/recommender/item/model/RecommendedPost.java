package org.bibsonomy.recommender.item.model;

import java.io.Serializable;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

import recommender.core.interfaces.model.RecommendationResult;

/**
 * TODO: add documentation to this class
 *
 * @author lha
 * @param <R> 
 */
public class RecommendedPost<R extends Resource> implements RecommendationResult, Serializable {
	private static final long serialVersionUID = -2529041486151001015L;
	
	private Post<R> post;
	
	private double score;
	private double confidence;
	
	/**
	 * @return the post
	 */
	public Post<R> getPost() {
		return this.post;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Post<R> post) {
		this.post = post;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#getScore()
	 */
	@Override
	public double getScore() {
		return score;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#getConfidence()
	 */
	@Override
	public double getConfidence() {
		return confidence;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#setScore(double)
	 */
	@Override
	public void setScore(double score) {
		this.score = score;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#setConfidence(double)
	 */
	@Override
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#getRecommendationId()
	 */
	@Override
	public String getRecommendationId() {
		return String.valueOf(this.post.getContentId());
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#compareToOtherRecommendationResult(recommender.core.interfaces.model.RecommendationResult)
	 */
	@Override
	public int compareToOtherRecommendationResult(RecommendationResult o) {
		if (o instanceof RecommendedPost) {
			final RecommendedPost<? extends Resource> otherRecommendedPost = (RecommendedPost<? extends Resource>) o;
			
		}
		return -1;
	}

	/* (non-Javadoc)
	 * @see recommender.core.interfaces.model.RecommendationResult#getTitle()
	 */
	@Override
	public String getTitle() {
		return this.post.getResource().getTitle();
	}

}
