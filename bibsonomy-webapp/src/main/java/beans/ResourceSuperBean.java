package beans;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;

import resources.Resource;

public abstract class ResourceSuperBean<T extends Resource> implements Serializable {
	
	protected T resource;
	private Hashtable<String,String> errors;
	private String tagstring;
	private String copytag;
	
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	public ResourceSuperBean () {
		errors = new Hashtable<String,String>();
	}

	/*
	 * returns the resource object 
	 */
	public T getResource() {
		return resource;
	}
	
	/*
	 * returns the tagstring as entered by the user
	 * (if available) or alternatively the generated
	 * tag string from the resource (which contains
	 * also the for:users)
	 */
	public String getTagstring() {
		if (tagstring != null) {
			return tagstring;
		}
		return resource.getFullTagString();
	}
	
	/*
	 * set tag string and remember it
	 * the tag string is parsed, when doing bookmark.setTags()
	 */
	public void setTags(String t) {
		tagstring = getTrimmed(t);
		resource.setTags(tagstring);
	}
	
	
	/*
	 * Error storing methods
	 */
	public Hashtable getErrors() {
		return errors;
	}
	public Set getErrorKeys () {
		return errors.keySet();
	}
	public void addError(String key, String msg) {
		errors.put(key,msg);
	}
	
	/*
	 * Copytag handling
	 * this is used to remember the tags of the copied item
	 */
	public void setCopytag(String copytag) {
		this.copytag=copytag;
	}

	public Enumeration getCopytag() {
		if (copytag == null) {
			return null;
		} else {
			return new StringTokenizer(copytag);
		}
	}
	
	/*
	 * cleans up a string
	 */
	protected String getTrimmed (String s) {
		if (s == null) {
			return "";
		} else {
			return s.trim();
		}
	}
	
	
	/*
	 * delegated methods from Resource object
	 */
	
	public void setRating(String r) {
		resource.setRating(r);
	}
	public String getRating() {
		return Integer.toString(resource.getRating());
	}
	
	public void addTag(String t) {
		resource.addTag(t);
	}
	public String getGroup() {
		return resource.getGroup();
	}
	public void setGroup(String r) {
		resource.setGroup(r);
	}
	public void setDate(String d) {
		try {
			resource.setDate(df.parse(d));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}