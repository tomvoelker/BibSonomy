package org.bibsonomy.model;

import java.net.URL;

import org.bibsonomy.util.StringUtils;

/**
 * This is a bookmark, which is derived from {@link Resource}.
 * 
 * @version $Id$
 */
public class Bookmark extends Resource {

	/**
	 * An {@link URL} pointing to some website.
	 * FIXME: Use URL instead of String
	 */
	private String url;

	/**
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * @param url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Bookmarks use the same hash value for both intrahash and interhash
	 */
	@Override
	public String getInterHash() {
		return super.getIntraHash();
	}

	/**
	 * @return hash
	 */
	public String getHash() {
		return StringUtils.getMD5Hash(this.url);
	}

	@Override
	public void recalculateHashes() {
		this.setIntraHash(getHash());
	}
	
	@Override
	public String toString() {
		return super.toString() + " = <" + url + ">";
	}
}