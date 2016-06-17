package org.bibsonomy.database.params;

import java.util.Date;

/**
 * TODO: add documentation to this class
 *
 * @author niebler
 */
public class PostChangeLogParam {

	private String oldIntraHash;
	private String newIntraHash;
	private String postOwner;
	private String postEditor;
	private Date date;

	/**
	 * @return the oldIntraHash
	 */
	public String getOldIntraHash() {
		return this.oldIntraHash;
	}

	/**
	 * @param oldIntraHash
	 *            the oldIntraHash to set
	 */
	public void setOldIntraHash(final String oldIntraHash) {
		this.oldIntraHash = oldIntraHash;
	}

	/**
	 * @return the newIntraHash
	 */
	public String getNewIntraHash() {
		return this.newIntraHash;
	}

	/**
	 * @param newIntraHash
	 *            the newIntraHash to set
	 */
	public void setNewIntraHash(final String newIntraHash) {
		this.newIntraHash = newIntraHash;
	}

	/**
	 * @return the postOwner
	 */
	public String getPostOwner() {
		return this.postOwner;
	}

	/**
	 * @param postOwner
	 *            the postOwner to set
	 */
	public void setPostOwner(final String postOwner) {
		this.postOwner = postOwner;
	}

	/**
	 * @return the postEditor
	 */
	public String getPostEditor() {
		return this.postEditor;
	}

	/**
	 * @param postEditor
	 *            the postEditor to set
	 */
	public void setPostEditor(final String postEditor) {
		this.postEditor = postEditor;
	}

	/**
	 * @return the changeDate
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param changeDate
	 *            the changeDate to set
	 */
	public void setDate(final Date changeDate) {
		this.date = changeDate;
	}

}
