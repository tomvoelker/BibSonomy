package org.bibsonomy.lucene.database.params;

/**
 * @author fei
 * @version $Id$
 */
public class TasParam {
	private Integer contentID;
	private String tagName;
	
	/**
	 * @return the contentID
	 */
	public Integer getContentID() {
		return contentID;
	}
	
	/**
	 * @param contentID the contentID to set
	 */
	public void setContentID(Integer contentID) {
		this.contentID = contentID;
	}
	
	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}
	
	/**
	 * @param tagName the tagName to set
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
