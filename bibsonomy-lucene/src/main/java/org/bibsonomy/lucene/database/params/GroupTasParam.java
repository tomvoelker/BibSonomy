package org.bibsonomy.lucene.database.params;

/**
 * @author fei
 * @version $Id$
 */
public class GroupTasParam extends GroupParam {
	private Integer contentID;

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
}
