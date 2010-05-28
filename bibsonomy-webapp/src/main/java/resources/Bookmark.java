package resources;

import java.net.MalformedURLException;
import java.net.URL;

@Deprecated
public class Bookmark extends Resource {
	public static final int CONTENT_TYPE=1;
	
	private static final int MAX_DESCRIPTION_LENGTH_SCREEN = 155;
	
	private String extended = "";
	private String hash     = "";
	private String oldurl   = "";
	
	
	public Bookmark () {
	  //super.setContentType(Bookmark.CONTENT_TYPE);	
	}
	
	@Override
	public void setUrl (String url) {
		super.setUrl(url);
		this.hash = hash(super.getUrl());
	}
	
	public String getUrlHost () {
		String url = super.getUrl();
		URL u = null;
		try {
			u = new URL(url);
		} catch (Exception e) {
			try {
				u = new URL("http://brokenurl.local");
			} catch (MalformedURLException ex) {
			}
		}
		return u.getHost();
	}
	
	@Override
	public String toString () {
		return super.toString() + " [URL = " + super.getUrl() + "]";
	}

	public void setExtended (String e) {
		this.extended = e;
	}
	public String getExtended () {
		return extended;
	}
	public String getShortExtended () {
		if (getIsLongExtended()) {
			return extended.substring(0, MAX_DESCRIPTION_LENGTH_SCREEN);
		}
		return extended;
	}

	public boolean getIsLongExtended() {
		return extended != null && extended.length() > MAX_DESCRIPTION_LENGTH_SCREEN;
	}

	
	// Hash Handling
	public static String hash (String url) { // exists, so that everybody can calculate Hashes of an URL
		return Resource.hash(url);
	}
	// this is for deleting bookmarks when hash is known only
	public void setHash (String hash) {
		this.hash = hash;
	}
	@Override
	public String getHash () { // return the hash of THIS url
		return hash;
	}
	
	@Override
	public int getContentType () {
		return Bookmark.CONTENT_TYPE;
	}
	
	/*
	 * this is for tagging tags
	 */
	public void addUpperTag(String t){
		tag.addUpper(t);
	}
	public void addLowerTag(String t){
		tag.addLower(t);
	}

	/** 
	 * @return the old URL of the bookmark, if the user changed the URL during edit.
	 */
	public String getOldurl() {
		return oldurl;
	}
	public void setOldurl(String oldurl) {
		this.oldurl = oldurl;
	}

}