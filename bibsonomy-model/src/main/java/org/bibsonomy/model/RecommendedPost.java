package org.bibsonomy.model;

import java.io.Serializable;


/**
 * Wraps a {@link Post} and adds score and confidence.
 * 
 * @author lukas
 * @version $Id$
 * 
 * @param <T> the resourcetype
 */
public class RecommendedPost<T extends Resource> implements Serializable {

private static final long serialVersionUID = -1872430526599241544L;
	
	private double score;
	private double confidence;
	private Post<T> post;
	
	/**
	 * for bean-compatibility
	 * @param post the post
	 */
	public RecommendedPost(Post<T> post) {
		this.post = post;
	}
	
	/**
	 * @return the post
	 */
	public Post<T> getPost() {
		return this.post;
	}
	
	/**
	 * @param post the post
	 */
	public void setPost(Post<T> post) {
		this.post = post;
	}
	
	/**
	 * @return the score
	 */
	public double getScore() {
		return this.score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return this.confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
}
