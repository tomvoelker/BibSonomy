/*
 * Created on 26.08.2007
 */
package org.bibsonomy.webapp.command;

import org.bibsonomy.webapp.util.RequestWrapperContext;

/**
 * command with fields for the resource lists.
 * 
 * is mainly a container for two list commands (bookmarks & bibtexs), the requested username
 * and a list of tags associated with the bookmarks / bibtexs
 * 
 * @see BaseCommand
 * @author Jens Illig
 * @author Dominik Benz
 */
public class ResourceViewCommand extends BaseCommand {
	private TagCloudCommand tagcloud = new TagCloudCommand();
	private String requestedUser;
	private String resourcetype;
	/**
	 * delegated to {@link RequestWrapperContext}
	 */
//	private String format = "html"; 
	private String sortPage = "none";
	private String sortPageOrder = "asc";
	private String duplicates = "yes";
	
	/** retrieve only tags without resources */
	private String restrictToTags = "false";

	/** callback function for JSON outputs */
	private String callback = "";	
	
	/** filter group resources  */
	private String filter = "";
		
	public String getDuplicates() {
		return this.duplicates;
	}

	public void setDuplicates(String duplicates) {
		this.duplicates = duplicates;
	}

	/**
	 * @return name of the user whose resources are requested
	 */
	public String getRequestedUser() {
		return this.requestedUser;
	}
	/**
	 * @param requestedUser name of the user whose resources are requested
	 */
	public void setRequestedUser(String requestedUser) {
		this.requestedUser = requestedUser;
	}

	/**
	 * @return the tagcloud command
	 */
	public TagCloudCommand getTagcloud() {
		return this.tagcloud;
	}

	/**
	 * @param tagcloud the tagcloud command
	 */
	public void setTagcloud(TagCloudCommand tagcloud) {
		this.tagcloud = tagcloud;
	}

	/**
	 * delegated to {@link RequestWrapperContext}
	 * 
	 * @return The requested format.
	 * 
	 */
	public String getFormat() {
		/**
		 * delegated to {@link RequestWrapperContext}
		 */
		return getContext().getFormat();
		//return this.format;
	}

	/** 
	 * delegated to {@link RequestWrapperContext}
	 */
//	/**
//	 * @param format
//	 */
//	public void setFormat(String format) {
//		this.format = format;
//	}

	public String getResourcetype() {
		return this.resourcetype;
	}

	public void setResourcetype(String resourcetype) {
		this.resourcetype = resourcetype;
	}

	public String getSortPage() {
		return this.sortPage;
	}

	public void setSortPage(String sortPage) {
		this.sortPage = sortPage;
	}

	public String getSortPageOrder() {
		return this.sortPageOrder;
	}

	public void setSortPageOrder(String sortPageOrder) {
		this.sortPageOrder = sortPageOrder;
	}

	public String getRestrictToTags() {
		return this.restrictToTags;
	}

	public void setRestrictToTags(String restrictToTags) {
		this.restrictToTags = restrictToTags;
	}

	public String getCallback() {
		return this.callback;
	}

	public void setCallback(String callBack) {
		this.callback = callBack;
	}		
	
	public String getFilter() {
		return this.filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}
}