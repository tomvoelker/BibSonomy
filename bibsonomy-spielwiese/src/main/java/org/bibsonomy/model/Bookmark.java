package org.bibsonomy.model;

import java.util.Date;
import java.util.List;

public class Bookmark {

	private int contentId;
	private List<Tag> tags;
	private String description;
	private String extended;
	private String userName;
	private Date date;
	private String urlHash;
	private String url;
	private int count;

	public int getContentId() {
		return this.contentId;
	}
	public void setContentId(int contentId) {
		this.contentId = contentId;
	}
	public int getCount() {
		return this.count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Date getDate() {
		return this.date;
	}
	public void setDate(Date date) {
		this.date = date;
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
	public String getUrl() {
		return this.url;
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
}