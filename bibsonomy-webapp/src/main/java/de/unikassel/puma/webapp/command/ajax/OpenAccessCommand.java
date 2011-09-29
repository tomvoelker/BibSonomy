package de.unikassel.puma.webapp.command.ajax;

import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * @author clemens
 * @version $Id$
 */
public class OpenAccessCommand extends AjaxCommand {

	/**
	 * publisher to check
	 */
	private String publisher;
	private String jTitle;
	private String qType;
	private String interhash = "";	

	/**
	 * @return publisher
	 */
	public String getPublisher() {
		return this.publisher;
	}

	/**
	 * @param publisher
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	/**
	 * @return jTitle
	 */
	public String getjTitle() {
		return this.jTitle;
	}

	/**
	 * @param jTitle
	 */
	public void setjTitle(String jTitle) {
		this.jTitle = jTitle;
	}

	/**
	 * @return qType
	 */
	public String getqType() {
		return this.qType;
	}

	/**
	 * @param qType
	 */
	public void setqType(String qType) {
		this.qType = qType;
	} 	
	
	
	/**
	 * @param interhash 
	 */
	public void setInterhash(String interhash) {
		this.interhash = interhash;
	}

	/**
	 * @return the interhash
	 */
	public String getInterhash() {
		return interhash;
	}

	
	
}
