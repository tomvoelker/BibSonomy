package org.bibsonomy.recommender.tags.database.params;

/**
 * @author fei
 * @version $Id$
 */
public class SelectorQueryMapParam {
	private Long qid;           // query id
	private Long sid;           // settings id
	
	
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
	
}
