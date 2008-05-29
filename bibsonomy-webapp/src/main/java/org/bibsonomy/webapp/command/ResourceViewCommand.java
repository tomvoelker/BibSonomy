/*
 * Created on 26.08.2007
 */
package org.bibsonomy.webapp.command;

import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

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
	private ListCommand<Post<Bookmark>> bookmark = new ListCommand<Post<Bookmark>>();
	private ListCommand<Post<BibTex>> bibtex = new ListCommand<Post<BibTex>>();
	private TagCloudCommand tagcloud = new TagCloudCommand();
	private String requestedUser;
	private String resourcetype;
	private String format = "html";
	private String sortPage = "none";
	private String sortPageOrder = "asc";
	private String duplicates = "yes";
	
	/** retrieve only tags without resources */
	private String restrictToTags = "false";

	/** callback function for JSON outputs */
	private String callback = "";	
	
	/** filter group resources  */
	private String filter = "";
	
	/** show PDF files attached to resources - enabled by default*/
	private String showPDF = "true";	
	
	public String getDuplicates() {
		return this.duplicates;
	}

	public void setDuplicates(String duplicates) {
		this.duplicates = duplicates;
	}

	/**
	 * @param <T> type of the entities in the list
	 * @param resourceType type of the entities in the list
	 * @return the list with entities of type resourceType
	 */
	@SuppressWarnings("unchecked")
	public <T extends Resource> ListCommand<Post<T>> getListCommand(Class<T> resourceType) {
		if (resourceType == BibTex.class) {
			return (ListCommand) getBibtex();
		} else if (resourceType == Bookmark.class) {
			return (ListCommand) getBookmark();
		}
		throw new UnsupportedResourceTypeException(resourceType.getName());
	}
	
	/**
	 * @return the bibtex ListView
	 */
	public ListCommand<Post<BibTex>> getBibtex() {
		return this.bibtex;
	}
	/**
	 * @param bibtex the bibtex ListView
	 */
	public void setBibtex(ListCommand<Post<BibTex>> bibtex) {
		this.bibtex = bibtex;
	}
	/**
	 * @return the bookmark ListView
	 */
	public ListCommand<Post<Bookmark>> getBookmark() {
		return this.bookmark;
	}
	/**
	 * @param bookmark the bookmark ListView
	 */
	public void setBookmark(ListCommand<Post<Bookmark>> bookmark) {
		this.bookmark = bookmark;
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
	 * @return
	 */
	public String getFormat() {
		return this.format;
	}
	
	/**
	 * @param format
	 */
	public void setFormat(String format) {
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

	public String getShowPDF() {
		return this.showPDF;
	}

	public void setShowPDF(String showPDF) {
		this.showPDF = showPDF;
	}
}