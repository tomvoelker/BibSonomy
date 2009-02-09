package org.bibsonomy.recommender.params;

/**
 * @author fei
 * @version $Id$
 */
public class QueryGuess {
	private Long qid;
	private Long diff;
	
	
	public void setQid(Long qid) {
		this.qid = qid;
	}
	public Long getQid() {
		return qid;
	}
	public void setDiff(Long diff) {
		this.diff = diff;
	}
	public Long getDiff() {
		return diff;
	}
}
