package resources;

import helpers.constants;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;

@Deprecated
public abstract class Resource implements Cloneable {

	public static final int UNDEFINED_CONTENT_ID =   -1;	
	private static final int MAX_LEN_TITLE       = 6000;
	private static final int MAX_LEN_URL         = 6000;
	private static final String BROKEN_URL              = "/brokenurl#";

	private HashMap<String, String> extended_fields = null;

	private int contentID   = UNDEFINED_CONTENT_ID;
	private int groupid     = constants.SQL_CONST_GROUP_PUBLIC;
	private String group    = "public";
	private String title    = "";
	private String privnote;
	private String url      = "";
	private String user     = "";
	private String oldHash  = "";
	private String docHash  = null;  // hash of referenced document in document table and in file system
	private String docName  = null;  // real file name of referenced document
	private Date date = null;
	private int ctr;

	private int rating = 0; // rating of user


	/* every subclass of Resource has a unique content_type 
	 * in the subclasses it should not be possible to change the content_type (it is fixed)
	 * so overload setContentType to not change the type
	 * the reason that we have setContentType inside Resource is, that we want to
	 * use Resources for just inserting tas into the DB and tas have to have a contentType  
	 */

	private boolean validTitle = false;
	private boolean validUrl   = false;

	private boolean toDel = false;
	private boolean toIns = false;



	public static String clean (String s) {
		return s.replaceAll("\\s+"," ").trim();
	}


	/**
	 * Calculates the MD5-Hash of a String s and returns it encoded as a hex string of 32 characters length.
	 * 
	 * @param s the string to be hashed
	 * @return the MD5 hash of s as a 32 character string 
	 */
	public static String hash (String s) {
		if (s == null) {
			return null;
		} 

		String charset = "UTF-8";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return toHexString(md.digest(s.getBytes(charset)));
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}



	/**
	 * Converts a buffer of bytes into a string of hex values.
	 * 
	 * @param buffer array of bytes which should be converted
	 * @return hex string representation of buffer
	 */
	public static String toHexString (byte[] buffer) {
		StringBuffer result = new StringBuffer();
		int i;
		for (i = 0; i < buffer.length; i++) {
			String hex = Integer.toHexString (buffer[i]);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			result.append(hex.substring(hex.length() - 2));
		}
		return result.toString();
	}






	/**
	 * Get a copy of this resource.
	 * 
	 * @return a copy of the resource including the tags (for more information how 
	 * the tags are cloned see {@link Tag}).
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		Resource copy = (Resource)super.clone();
		// copy taglist
		return copy;
	}


	/**
	 * Cleans a URL and makes it valid. This includes 
	 * <ul>
	 * <li> checking if it starts with a known protocol (http, ftp, gopher, https) or {@link #BROKEN_URL} (which means it is valid, too),
	 * <li> checking if it is a <em>BibTeX</em> URL and removing the surrounding macro,
	 * <li> checking if it is an empty string or 
	 * <li> else just returning it marked as a broken url (see {@link #BROKEN_URL}).
	 * </ul>
	 * 
	 * 
	 * @param url the URL which should be checked and cleaned
	 * @return the checked and cleaned URL
	 */
	public static String cleanUrl (String url) {
		if (url == null) {
			return null;
		} 
		// remove linebreaks, etc.
		url = url.replaceAll("\\n|\\r", "");
		// this should be the most common case: a valid URL
		if (url.startsWith("http://") || 
				url.startsWith("ftp://") ||
				url.startsWith("file://") ||
				url.startsWith(BROKEN_URL) ||
				url.startsWith("gopher://") ||
				url.startsWith("https://")) {
			return cropToLength(url, MAX_LEN_URL);
		} else if (url.startsWith("\\url{") && url.endsWith("}")) {
			// remove \\url{...}
			return cropToLength(url.substring(5,url.length()-1), MAX_LEN_URL);
		} else if (url.trim().equals("")){
			// handle an empty URL
			return "";
		} else {
			// URL is neither empty nor valid: mark it as broken
			return cropToLength(BROKEN_URL + url, MAX_LEN_URL);
		}

	}
	// URL
	public void setUrl (String url) {
		validUrl = !(url.equals("") || (url.indexOf(' ') != -1));
		this.url = cleanUrl (url);
	}
	public String getUrl () {
		return url;
	}

	public String getShortUrl () {
		if (url.length() > 60) {
			return url.substring(0, 60) + "..."; 
		}
		return url;
	}

	// Title
	public void setTitle (String title) {
		validTitle = title != null && !title.equals("");
		this.title = cropToLength(title, MAX_LEN_TITLE);
	}
	public String getTitle () {
		return title;
	}

	/* crops s to length if it is longer than length
	 * 
	 */
	protected static String cropToLength (String s, int length) {
		if (s != null && s.length() > length) {
			return s.substring(0, length);
		} else {
			return s;
		}
	}

	// Group
	public void setGroup (String r) {
		if (r != null) {
			this.group = r;
		}
	}
	public String getGroup () {
		return group;
	}
	// User
	public void setUser (String u) {
		this.user = u;
	}
	public String getUser () {
		return user;
	}

	// Date
	public void setDate (Date d) {
		this.date = d;
	}
	public Date getDate () {
		return date;
	}



	// Content-ID
	public void setContentID (int c) {
		this.contentID = c;
	}
	public int getContentID () {
		return contentID;
	}


	// ToDel
	public boolean isToDel() {
		return toDel;
	}
	public void setToDel(boolean toDel) {
		this.toDel = toDel;
	}

	// ToIns
	public boolean isToIns() {
		return toIns;
	}
	public void setToIns(boolean toIns) {
		this.toIns = toIns;
	}


	// GroupID
	public int getGroupid() {
		return groupid;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}

	// counter (counts how many of this resource exist)
	public int getCtr() {
		return ctr;
	}
	public void setCtr(int ctr) {
		this.ctr = ctr;
	}

	// stores old hash (for deletion)
	public String getOldHash() {
		return oldHash;
	}
	public void setOldHash(String oldHash) {
		this.oldHash = oldHash;
	}



	public String getPrivnote() {
		return privnote;
	}
	public void setPrivnote(String privnote) {
		this.privnote = privnote;
	}



	/** Returns the hash for the document belonging to the resource
	 * @return The hash for the document belonging to the resource. Under this hash
	 * the document is stored in the file system and also referenced in the document table.
	 */
	public String getDocHash() {
		return docHash;
	}
	public void setDocHash(String docHash) {
		this.docHash = docHash;
	}

	/**
	 * @return The real file name of the document belonging to the resource. 
	 */
	public String getDocName() {
		return docName;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public HashMap<String, String> getExtended_fields() {
		return extended_fields;
	}

	public void setExtended_fields(HashMap<String, String> extended_fields) {
		this.extended_fields = extended_fields;
	}

	public void addExtended_fields(String key, String value) {
		if (this.extended_fields == null) {
			extended_fields = new HashMap<String,String>();
		}
		extended_fields.put(key, value);
	}

	public int getRating() {
		return rating;
	}

	public void setRating(String rating) {
		try {
			this.setRating(Integer.parseInt(rating));
		} catch (NumberFormatException e) {
		}
	}

	public void setRating(int rating) {
		if (rating < 0 || rating > 5) {
			this.rating = 0;
		} else {
			this.rating = rating;
		}
	}

}