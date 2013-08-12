package org.bibsonomy.webapp.command.ajax;

import java.util.List;

import org.bibsonomy.common.Pair;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;

/**
 * @author nilsraabe
 * @version $Id$
 */
public class TitleSuggestionCommand extends SimpleResourceViewCommand {


	private List<Pair<String, Integer>> postSuggestionTitle;
	
	private String postType;

	private String postPrefix;


	/**
	 * @param postSuggestionTitle the postSuggestionTitle to set
	 */
	public void setPostSuggestionTitle(List<Pair<String, Integer>> postSuggestionTitle) {
		this.postSuggestionTitle = postSuggestionTitle;
	}
	
	/**
	 * @return khg
	 *	 
	 */
	public List<Pair<String, Integer>> getPostSuggestionTitle() {
		return this.postSuggestionTitle;
	}

	/**
	 * @return the postType
	 */
	public String getPostType() {
		return this.postType;
	}


	/**
	 * @param postType the postType to set
	 */
	public void setPostType(String postType) {
		this.postType = postType;
	}


	/**
	 * @return the postPrefix
	 */
	public String getPostPrefix() {
		return postPrefix;
	}


	/**
	 * @param postPrefix the postPrefix to set
	 */
	public void setPostPrefix(String postPrefix) {
		this.postPrefix = postPrefix;
	}
}
