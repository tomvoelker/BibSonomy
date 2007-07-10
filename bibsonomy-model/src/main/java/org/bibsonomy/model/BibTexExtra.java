package org.bibsonomy.model;

import java.net.URL;
import java.util.Date;

public class BibTexExtra {

	private URL url;
	private String text;
	private Date date;

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public URL getUrl() {
		return this.url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}
}