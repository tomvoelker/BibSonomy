package org.bibsonomy.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author philipp
 * @version $Id$
 */
public class Wiki implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8307551847065128002L;
	
	private String wikiText;
	
	private Date date;
	
	/**
	 * 
	 */
	public Wiki() {
		wikiText = "";
	}

	/**
	 * @param wikiText the wikiText to set
	 */
	public void setWikiText(String wikiText) {
		this.wikiText = wikiText;
	}

	/**
	 * @return the wikiText1
	 */
	public String getWikiText() {
		return wikiText;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

}
