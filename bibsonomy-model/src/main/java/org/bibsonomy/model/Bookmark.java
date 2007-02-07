package org.bibsonomy.model;

import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.model.Resource;

/**
 * This is a bookmark, which is derived from
 * {@link org.bibsonomy.gen_model.Resource} like all resources in BibSonomy.
 */
public class Bookmark extends Resource {

	private String url;
	private String urlHash;
	private String description;
	private String extended;

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

	public String getHash() {
		return ResourceUtils.hash(this.url);
	}
}