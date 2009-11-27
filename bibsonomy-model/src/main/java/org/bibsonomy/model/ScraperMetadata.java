package org.bibsonomy.model;

import java.net.URL;

/**
 * represents meta data for scrapers 
 * 
 * @author rja
 * @version $Id$
 */
public class ScraperMetadata {

	private URL url;
	private String metaData;
	private String scraperClass;
	private int id;
	
	public URL getUrl() {
		return this.url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public String getMetaData() {
		return this.metaData;
	}
	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}
	public String getScraperClass() {
		return this.scraperClass;
	}
	public void setScraperClass(String scraperClass) {
		this.scraperClass = scraperClass;
	}
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
