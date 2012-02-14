package org.bibsonomy.webapp.command;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.common.enums.Duplicates;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.TagsType;
import org.bibsonomy.model.Resource;

/**
 * command with fields for the resource lists.
 * 
 * is mainly a container for two list commands (bookmarks & publications), the requested username
 * and a list of tags associated with the bookmarks / publications
 * 
 * @see BaseCommand
 * @author Jens Illig
 * @author Dominik Benz
 * @version $Id$
 */
public class ResourceViewCommand extends BaseCommand {	
	/** default value for sortPage */
	public static final String DEFAULT_SORTPAGE = "none";
	/** default value for sortPageOrder */
	public static final String DEFAULT_SORTPAGEORDER = "asc";
	
	private TagCloudCommand tagcloud = new TagCloudCommand();
	
	private Date startDate;
	private Date endDate;
	
	private String requestedUser;
	private Set<Class<? extends Resource>> resourcetype = new HashSet<Class<? extends Resource>>();
	
	private TagsType tagstype; // for queries for specific kinds of tags
	
	private String format = "html"; 
	private String layout; // if format="layout", here the requested layout is stored
	private boolean formatEmbedded; // 
	
	// TODO: could be a list of SortKeys
	private String sortPage = DEFAULT_SORTPAGE;
	// TODO: could be a list of SortOrders
	private String sortPageOrder = DEFAULT_SORTPAGEORDER;
	
	/** show duplicates? */
	private Duplicates duplicates = Duplicates.YES;

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
	private FilterEntity filter;

	
	/**
	 * @return name of the user whose resources are requested
	 */
	public String getRequestedUser() {
		return this.requestedUser;
	}
	/**
	 * @param requestedUser name of the user whose resources are requested
	 */
	public void setRequestedUser(final String requestedUser) {
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
	public void setTagcloud(final TagCloudCommand tagcloud) {
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

	/**
	 * @return the resourcetype
	 */
	public Set<Class<? extends Resource>> getResourcetype() {
		return this.resourcetype;
	}

	/**
	 * @param resourcetype the resourcetype to set
	 */
	public void setResourcetype(final Set<Class<? extends Resource>> resourcetype) {
		this.resourcetype = resourcetype;
	}

	/**
	 * @return the sortPage
	 */
	public String getSortPage() {
		return this.sortPage;
	}

	/**
	 * @param sortPage the sortPage to set
	 */
	public void setSortPage(final String sortPage) {
		this.sortPage = sortPage;
	}

	/**
	 * @return the sortPageOrder
	 */
	public String getSortPageOrder() {
		return this.sortPageOrder;
	}

	/**
	 * @param sortPageOrder the sortPageOrder to set
	 */
	public void setSortPageOrder(final String sortPageOrder) {
		this.sortPageOrder = sortPageOrder;
	}

	/**
	 * @return the restrictToTags
	 */
	public boolean getRestrictToTags() {
		return this.restrictToTags;
	}

	/**
	 * @param restrictToTags the restrictToTags to set
	 */
	public void setRestrictToTags(final boolean restrictToTags) {
		this.restrictToTags = restrictToTags;
	}

	/**
	 * @return the callback
	 */
	public String getCallback() {
		return this.callback;
	}

	/**
	 * @param callback the callback to set
	 */
	public void setCallback(final String callback) {
		this.callback = callback;
	}

	/**
	 * @return the filter
	 */
	public FilterEntity getFilter() {
		return this.filter;
	}

	/**
	 * @param filter the filter to set
	 */
	public void setFilter(final FilterEntity filter) {
		this.filter = filter;
	}

	/**
	 * @return the layout
	 */
	public String getLayout() {
		return this.layout;
	}

	/**
	 * @param layout the layout to set
	 */
	public void setLayout(final String layout) {
		this.layout = layout;
	}

	/**
	 * @return the formatEmbedded
	 */
	public boolean getformatEmbedded() {
		return this.formatEmbedded;
	}

	/**
	 * @param formatEmbedded the formatEmbedded to set
	 */
	public void setformatEmbedded(final boolean formatEmbedded) {
		this.formatEmbedded = formatEmbedded;
	}
	
	/**
	 * @return the tagstype
	 */
	public TagsType getTagstype() {
		return this.tagstype;
	}

	/**
	 * @param tagstype the tagstype to set
	 */
	public void setTagstype(final TagsType  tagstype) {
		this.tagstype = tagstype;
	}

	/**
	 * @return the notags
	 */
	public boolean isNotags() {
		return this.notags;
	}

	/**
	 * @param notags the notags to set
	 */
	public void setNotags(final boolean notags) {
		this.notags = notags;
	}

	/**
	 * @param personalized the personalized to set
	 */
	public void setPersonalized(final boolean personalized) {
		this.personalized = personalized;
	}
	
	/**
	 * @return the personalized
	 */
	public boolean isPersonalized() {
		return personalized;
	}

	/**
	 * @return the referer
	 */
	public String getReferer() {
		return this.referer;
	}

	/**
	 * @param referer the referer to set
	 */
	public void setReferer(final String referer) {
		this.referer = referer;
	}
	
	/**
	 * @return enum how to handle duplicates
	 */
	public Duplicates getDuplicates() {
		return this.duplicates;
	}
	
	/**
	 * @param duplicates  the duplicates
	 */
	public void setDuplicates(Duplicates duplicates) {
		this.duplicates = duplicates;
	}
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}