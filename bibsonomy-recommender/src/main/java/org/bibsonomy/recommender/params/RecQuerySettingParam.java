package org.bibsonomy.recommender.params;

/**
 * @author fei
 * @version $Id$
 */
public class RecQuerySettingParam {
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
