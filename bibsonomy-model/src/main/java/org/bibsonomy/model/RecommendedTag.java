package org.bibsonomy.model;


/** Adds scores and confidence to {@link Tag}.
 * 
 * TODO: move this to org.bibsonomy.model?
 * 
 * @author rja
 * @version $Id$
 */
public class RecommendedTag extends Tag implements Comparable<Tag> {

	private double score;
	private double confidence;
	
	// 2008/12/10,fei: added standard constructor for bean-compatibility
	public RecommendedTag() {
	}
	
	public RecommendedTag(String name, double score, double confidence) {
		super(name);
		this.score = score;
		this.confidence = confidence;
	}
	
	public double getScore() {
		return this.score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getConfidence() {
		return this.confidence;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
}
