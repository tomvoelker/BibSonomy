package org.bibsonomy.batch.searchlucene.database.params;

public class TasParam {
	private Integer contentID;
	private String tagName;
	
	
	public void setContentID(Integer contentID) {
		this.contentID = contentID;
	}
	public Integer getContentID() {
		return contentID;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getTagName() {
		return tagName;
	}
}
