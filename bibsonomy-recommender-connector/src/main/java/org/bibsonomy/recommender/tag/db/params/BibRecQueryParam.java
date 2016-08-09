package org.bibsonomy.recommender.tag.db.params;

import java.sql.Timestamp;

/**
 * Parameter used to insert tag recommender queries.
 */
public class BibRecQueryParam {
	
	private Long qid;
	/** ID for mapping posts to recommender queries */
	private int post_id;
	/** content type of {@link RecommendationEntity}, 1 for Bookmark, 2 for BibTex */
	private int contentType;
	private String userName;
	private Timestamp timeStamp;
	/** querie's timeout value */
	private int queryTimeout;
	
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public void setQid(long qid) {
		this.qid = qid;
	}
	public long getQid() {
		return qid;
	}
	public void setContentType(int content_type) {
		this.contentType = content_type;
	}
	public int getContentType() {
		return contentType;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}
	public int getPost_id() {
		return post_id;
	}
	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}
	public int getQueryTimeout() {
		return queryTimeout;
	}
}
