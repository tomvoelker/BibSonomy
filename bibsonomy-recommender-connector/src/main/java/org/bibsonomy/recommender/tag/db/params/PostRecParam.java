package org.bibsonomy.recommender.tag.db.params;

import java.util.Date;


/**
 * Parameter for insertion of tag recommender feedback.
 * 
 * @author fei
 */
public class PostRecParam {
	private int postID;
	private String userName;
	private String hash;
	private Date date;
	private Integer contentType;
	
	
	public void setPostID(int postID) {
		this.postID = postID;
	}
	public int getPostID() {
		return postID;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getHash() {
		return hash;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getDate() {
		return date;
	}
	public void setContentType(Integer contentType) {
		this.contentType = contentType;
	}
	public Integer getContentType() {
		return contentType;
	}
}
