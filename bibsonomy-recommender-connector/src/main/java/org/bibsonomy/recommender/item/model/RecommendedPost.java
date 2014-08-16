package org.bibsonomy.recommender.item.model;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

import recommender.core.interfaces.model.RecommendationResult;

/**
 * TODO: add documentation to this class
 *
 * @author lha
 * @param <T> 
 */
public class RecommendedPost<T extends Resource> extends Post<T> implements RecommendationResult {
	private static final long serialVersionUID = -2529041486151001015L;
	
	private double score;
	private double confidence;
	
	
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

}
