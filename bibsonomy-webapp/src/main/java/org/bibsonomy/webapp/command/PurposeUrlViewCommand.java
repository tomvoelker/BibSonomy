package org.bibsonomy.webapp.command;

import org.bibsonomy.model.Tag;

import java.util.List;


/**
 * Bean for purpose sites
 *
 * @author  Christian Koerner
 */
public class PurposeUrlViewCommand extends TagResourceViewCommand {

	private List<Tag> purposeTags;
	
	private List<String> urls;
	
	/**
	 * @return urls
	 */
	public List<String> getUrls() {
		return this.urls;
	}
	
	/**
	 * set a list of urls
	 * @param urls to be set
	 */
	public void setUrls(List<String> urls) {
		this.urls = urls;
	}
	
	/**
	 * @return purpose tags
	 */
	public List<Tag> getPurposeTags() {
		return this.purposeTags;
	}
	
	/**
	 * set a list of purpose tags
	 * @param purposeTags
	 */
	public void setPurposeTags(List<Tag> purposeTags) {
		this.purposeTags = purposeTags;
	}
	
}