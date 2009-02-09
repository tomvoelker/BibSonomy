package org.bibsonomy.recommender.params;

import java.sql.Timestamp;

/**
 * @author fei
 * @version $Id$
 */
public class PostParam extends ListParam {
	private Timestamp timestamp;
	private String userName;
	private Integer contentID;
	
	
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setUserName(String user_name) {
		this.userName = user_name;
	}
	public String getUserName() {
		return userName;
	}
	public void setContentID(Integer contentID) {
		this.contentID = contentID;
	}
	public Integer getContentID() {
		return contentID;
	}


}
