/**
 * 
 */
package org.bibsonomy.webapp.command.ajax;

/**
 * @author bernd
 * @version $Id$
 */
public class AjaxURLCommand extends AjaxCommand {
	
	private String url;
	private String text;
	private String ckey;
	private String hash;
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}
	
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	
	/**
	 * @return the ckey
	 */
	public String getCkey() {
		return this.ckey;
	}
	
	/**
	 * @param ckey the ckey to set
	 */
	public void setCkey(String ckey) {
		this.ckey = ckey;
	}
	
	/**
	 * @return the hash
	 */
	public String getHash() {
		return this.hash;
	}
	
	/**
	 * @param hash the hash to set
	 */
	public void setHash(String hash) {
		this.hash = hash;
	}
}
