package org.bibsonomy.recommender.params;

/**
 * Parameter class for storing recommender results in database
 * @author fei
 * @version $Id$
 */
public class RecResponseParam {
	private Long qid;           /// query id
	private Long sid;           /// settings id
	private Long latency;
	private Double score;
	private Double confidence;
	private String tagName;
	
	public void setQid(long qid) {
		this.qid = qid;
	}
	public long getQid() {
		return qid;
	}
	public void setSid(long sid) {
		this.sid = sid;
	}
	public long getSid() {
		return sid;
	}
	public void setLatency(long latency) {
		this.latency = latency;
	}
	public long getLatency() {
		return latency;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getScore() {
		return score;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	public double getConfidence() {
		return confidence;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getTagName() {
		return tagName;
	}
}
