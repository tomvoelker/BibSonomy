package org.bibsonomy.recommender.params;

import java.sql.Timestamp;

/**
 * @author fei
 * @version $Id$
 */
public class TasEntry {
	private Timestamp timeStamp;
	private String userName;
	private Integer contentID;
	private String tag;
	
	
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
	public void setContentID(Integer contentID) {
		this.contentID = contentID;
	}
	public Integer getContentID() {
		return contentID;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTag() {
		return tag;
	}
}
