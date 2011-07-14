package org.bibsonomy.webapp.command.ajax;

/**
 * @author bernd
 * @version $Id$
 */
public class AjaxURLCommand extends AjaxCommand {
	/**
	 * the hash of the resource
	 */
	private String hash;
	
	/**
	 * the text of the url
	 */
	private String text;
	
	/**
	 * TODO: could this be of type URL?!
	 * the url
	 */
	private String url;
	
	/**
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}
	
	/**
	 * @param url the url to set
	 */
	public void setUrl(final String url) {
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
	public void setText(final String text) {
		this.text = text;
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
	public void setHash(final String hash) {
		this.hash = hash;
	}
}
