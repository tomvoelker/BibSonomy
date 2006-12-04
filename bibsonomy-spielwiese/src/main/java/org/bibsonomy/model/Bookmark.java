package org.bibsonomy.model;

import java.util.Date;
import java.util.List;

/**
 * This is a bookmark, which is derived from
 * {@link org.bibsonomy.model.Resource} like all resources in BibSonomy.
 * 
 * @author Christian Schenk
 */
public class Bookmark extends Resource {

	private List<Tag> tags;
	private String description;
	private String extended;
	private String userName;
	private String urlHash;
//	private BookUrl url;
	private String url;
	private int count;
	private Date date;
	
	
	public int getCount() {
		return this.count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getExtended() {
		return this.extended;
	}
	public void setExtended(String extended) {
		this.extended = extended;
	}
	public List<Tag> getTags() {
		return this.tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
//	public BookUrl getUrl() {
//		return this.url;
//	}
//	public void setUrl(BookUrl url) {
//		this.url = url;
//	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrlHash() {
		return this.urlHash;
	}
	public void setUrlHash(String urlHash) {
		this.urlHash = urlHash;
	}
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}