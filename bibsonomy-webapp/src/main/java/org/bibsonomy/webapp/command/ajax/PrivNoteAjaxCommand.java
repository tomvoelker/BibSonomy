package org.bibsonomy.webapp.command.ajax;

/**
 * @author wla
 * @version $Id$
 */
public class PrivNoteAjaxCommand extends AjaxCommand {

	private String intraHash;
	private String privNote;

	/**
	 * @return the intraHash
	 */
	public String getIntraHash() {
		return intraHash;
	}

	/**
	 * @param intraHash the intraHash to set
	 */
	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}

	/**
	 * @return the privNote
	 */
	public String getPrivNote() {
		return privNote;
	}

	/**
	 * @param privNote the privNote to set
	 */
	public void setPrivNote(String privNote) {
		this.privNote = privNote;
	}
}
