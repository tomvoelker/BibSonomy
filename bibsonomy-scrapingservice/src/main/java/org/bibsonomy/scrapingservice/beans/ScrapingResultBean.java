package org.bibsonomy.scrapingservice.beans;

import java.io.Serializable;
import java.net.URL;

public class ScrapingResultBean implements Serializable {
	
	private static final long serialVersionUID = 8899554705056075887L;

	private String bibtex;
	private String errorMessage;
	private URL url;
	
	public ScrapingResultBean() {
		// TODO Auto-generated constructor stub
	}
	

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
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
