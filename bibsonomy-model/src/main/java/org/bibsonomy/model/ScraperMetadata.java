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
	private Class scraperClass;
	private int postId;
	
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
	public Class getScraperClass() {
		return this.scraperClass;
	}
	public void setScraperClass(Class scraperClass) {
		this.scraperClass = scraperClass;
	}
	public int getPostId() {
		return this.postId;
	}
	public void setPostId(int postId) {
		this.postId = postId;
	}
	
}
