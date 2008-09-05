package org.bibsonomy.scrapingservice.beans;

import java.io.Serializable;

public class ScrapingResultBean implements Serializable {
	
	private static final long serialVersionUID = 8899554705056075887L;

	private String bibtex;
	
	private String errorMessage;
	
	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBibtex() {
		return bibtex;
	}

	public void setBibtex(String bibtex) {
		this.bibtex = bibtex;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
