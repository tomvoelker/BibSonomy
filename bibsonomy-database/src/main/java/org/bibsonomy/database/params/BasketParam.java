package org.bibsonomy.database.params;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class BasketParam extends GenericParam{
	private int contentId;

	/**
	 * @return int
	 */
	public int getContentId() {
		return this.contentId;
	}

	/**
	 * @param contentId
	 */
	public void setContentId(final int contentId) {
		this.contentId = contentId;
	}
}
