package org.bibsonomy.webapp.command.ajax;

/**
 * @author wla
 * @version $Id$
 */
public class PrivateNoteAjaxCommand extends AjaxCommand {

	private String intraHash;
	private String privateNote;

	/**
	 * @return the intraHash
	 */
	public String getIntraHash() {
		return this.intraHash;
	}

	/**
	 * @param intraHash the intraHash to set
	 */
	public void setIntraHash(final String intraHash) {
		this.intraHash = intraHash;
	}

	/**
	 * @return the privateNote
	 */
	public String getPrivateNote() {
		return this.privateNote;
	}

	/**
	 * @param privateNote the privateNote to set
	 */
	public void setPrivateNote(final String privateNote) {
		this.privateNote = privateNote;
	}
}
