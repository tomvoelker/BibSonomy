package org.bibsonomy.recommender.connector.database.params;

/**
 * @author fei
  */
public class PostGuess {
	private Integer contentID;
	private Long diff;
	
	
	public void setDiff(Long diff) {
		this.diff = diff;
	}
	public Long getDiff() {
		return diff;
	}
	public void setContentID(Integer contentID) {
		this.contentID = contentID;
	}
	public Integer getContentID() {
		return contentID;
	}
}
