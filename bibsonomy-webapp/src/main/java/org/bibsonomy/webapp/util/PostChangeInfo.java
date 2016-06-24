package org.bibsonomy.webapp.util;

/**
 * Contains information about changed posts.
 *
 * @author niebler
 */
public class PostChangeInfo {
	private boolean checked;
	
	// FIXME: should be of type Set<Tag>
	private String oldTags;
	// FIXME: should be of type Set<Tag>
	private String newTags;

	/**
	 * @return the marked
	 */
	public boolean isChecked() {
		return this.checked;
	}

	/**
	 * @param marked
	 *            the marked to set
	 */
	public void setChecked(final boolean checked) {
		this.checked = checked;
	}

	/**
	 * @return the oldTags
	 */
	public String getOldTags() {
		return this.oldTags;
	}

	/**
	 * @param oldTags
	 *            the oldTags to set
	 */
	public void setOldTags(final String oldTags) {
		this.oldTags = oldTags;
	}

	/**
	 * @return the newTags
	 */
	public String getNewTags() {
		return this.newTags;
	}

	/**
	 * @param newTags
	 *            the newTags to set
	 */
	public void setNewTags(final String newTags) {
		this.newTags = newTags;
	}

}
