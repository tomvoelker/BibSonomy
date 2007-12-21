package org.bibsonomy.model.extra;

import java.net.URL;
import java.util.Date;

/**
 * Holds additional information about BibTexs.
 * 
 * @author Christian Schenk
 * @version $Id$
 */
public class BibTexExtra {

	private URL url;
	private String text;
	private Date date;

	/**
	 * @return date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return text
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * @param text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return url
	 */
	public URL getUrl() {
		return this.url;
	}

	/**
	 * @param url
	 */
	public void setUrl(URL url) {
		this.url = url;
	}
}