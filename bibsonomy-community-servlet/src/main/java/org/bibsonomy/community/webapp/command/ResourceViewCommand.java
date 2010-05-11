/*
 * Created on 26.08.2007
 */
package org.bibsonomy.community.webapp.command;


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
	private String tagstype; // for queries for specific kinds of tags
	private String format = "html"; 
	private String layout; // if format="layout", here the requested layout is stored
	private boolean formatEmbedded; // 
	private String sortPage = "none";
	private String sortPageOrder = "asc";
	private String duplicates = "yes";
	private String applicationName = "";
	private boolean notags = false;

	/**
	 * For some pages we need to store the referer to send the user back
	 * to that page.
	 */
	private String referer;
	
	/**
	 * if true, the posts and tags of the requested user will be ranked / highlighted
	 * according to the logged-in user 
	 */
	private Boolean personalized = false;	
	
	/** retrieve only tags without resources */
	private boolean restrictToTags = false;

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
	 * @return The requested format.
	 * 
	 */
	public String getFormat() {
		if (this.format != null && !this.format.trim().equals("")) return this.format;
		/*
		 * the default is html
		 * */
		return "html";
	}

	/** 
	 * delegated to {@link RequestWrapperContext}
	 */
	/**
	 * @param format
	 */
	public void setFormat(final String format) {
		this.format = format;
	}

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

	public boolean getRestrictToTags() {
		return this.restrictToTags;
	}

	public void setRestrictToTags(boolean restrictToTags) {
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

	public String getLayout() {
		return this.layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public boolean getformatEmbedded() {
		return this.formatEmbedded;
	}

	public void setformatEmbedded(boolean formatEmbedded) {
		this.formatEmbedded = formatEmbedded;
	}

	public void setTagstype(String tagstype) {
		this.tagstype = tagstype;
	}

	public String getTagstype() {
		return tagstype;
	}

	public boolean isNotags() {
		return this.notags;
	}

	public void setNotags(boolean notags) {
		this.notags = notags;
	}

	public void setPersonalized(Boolean personalized) {
		this.personalized = personalized;
	}

	public Boolean getPersonalized() {
		return personalized;
	}
	
	public Boolean isPersonalized() {
		return personalized;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getApplicationName() {
		return applicationName;
	}
	public String getReferer() {
		return this.referer;
	}

	public void setReferer(String referer) {
		this.referer = referer;
	}
}