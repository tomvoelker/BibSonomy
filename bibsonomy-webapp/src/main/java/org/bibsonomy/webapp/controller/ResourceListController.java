package org.bibsonomy.webapp.controller;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.ResourceType;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.common.enums.TagCloudStyle;
import org.bibsonomy.common.enums.TagsType;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.ResourceViewCommand;
import org.bibsonomy.webapp.command.SimpleResourceViewCommand;
import org.bibsonomy.webapp.command.TagCloudCommand;
import org.bibsonomy.webapp.view.Views;

/**
 * controller for retrieving multiple windowed lists with resources. 
 * These are currently the bookmark an the bibtex list
 * 
 * @author Jens Illig
 */
public abstract class ResourceListController {
	private static final Log log = LogFactory.getLog(ResourceListController.class);
	
	protected LogicInterface logic;
	protected UserSettings userSettings;
	protected Collection<Class<? extends Resource>> listsToInitialise;
	private Long startTime;
			
	/**
     * Retrieve a set of tags from the database logic and add them to the command object
     * 
	 * @param <T> extends Resource, the resource type
	 * @param <V> extends ResourceViewCommand, the command
	 * @param cmd the command
	 * @param resourceType the resource type
	 * @param groupingEntity the grouping entity
	 * @param groupingName the grouping name
	 * @param regex regular expression for tag filtering
	 * @param tags list of tags
	 * @param start start parameter
	 * @param end end parameter
	 */
	protected <T extends Resource, V extends ResourceViewCommand> void setTags(V cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, String hash, Order order, int start, int end, String search) {
		final TagCloudCommand tagCloudCommand = cmd.getTagcloud();
		// retrieve tags
		log.debug("getTags " + " " + groupingEntity + " " + groupingName);
		tagCloudCommand.setTags( this.logic.getTags(resourceType, groupingEntity, groupingName, regex, tags, hash, order, start, end, search, null));
		// retrieve tag cloud settings
		tagCloudCommand.setStyle(TagCloudStyle.getStyle(userSettings.getTagboxStyle()));
		tagCloudCommand.setSort(TagCloudSort.getSort(userSettings.getTagboxSort()));
		// overwrite minFreq only if not explicitly set by URL param
		if (tagCloudCommand.getMinFreq() == 0) {tagCloudCommand.setMinFreq(userSettings.getTagboxMinfreq());}
		tagCloudCommand.setMaxFreq(TagUtils.getMaxUserCount(tagCloudCommand.getTags()));
	}
	
	
	/**
	 * Initialize tag list, depending on chosen resourcetype
	 * 
	 * @param <T>
	 * @param <V>
	 * @param cmd
	 * @param listResourceType
	 * @param groupingEntity
	 * @param groupingName
	 * @param regex
	 * @param tags
	 * @param hash
	 * @param order
	 * @param start
	 * @param end
	 * @param search
	 */
	protected <V extends ResourceViewCommand> void handleTagsOnly(V cmd, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, String hash, Order order, int start, int end, String search) {
		final String tagsType = cmd.getTagstype();
		if (tagsType != null) {
			
			// if tags are requested (not related tags), remove non-systemtags from tags list
			if (tagsType.equalsIgnoreCase(TagsType.DEFAULT.getName() ) && tags != null ) {
				Iterator<String> it = tags.iterator();
				while (it.hasNext()) {
					if ( !SystemTagsUtil.isSystemTag( it.next() ) ) {
						it.remove();
					}
				}
			}
			
			// check if limitation to a single resourcetype is requested			
			Class<? extends Resource> resourcetype = Resource.class;
			if (this.isBibtexOnlyRequested(cmd)) {
				resourcetype = BibTex.class;
			}
			else if (this.isBookmarkOnlyRequested(cmd)) {
				resourcetype = Bookmark.class;
			}
					
			// fetch tags, store them in bean
			this.setTags(cmd, resourcetype, groupingEntity, groupingName, regex, tags, hash, order, start, end, search);
			
			// when tags only are requested, we don't need bibtexs and bookmarks
			this.listsToInitialise.remove(BibTex.class);
			this.listsToInitialise.remove(Bookmark.class);			
		}
	}
	
	
	/**
	 * do some post processing with the retrieved resources
	 * 
	 * @param cmd
	 */
	protected <T extends ResourceViewCommand> void postProcessAndSortList(T cmd, final List<Post<BibTex>> bibtexList) {
		for (Post<BibTex> post : bibtexList) {
			// insert openURL into bibtex objects
			post.getResource().setOpenURL(BibTexUtils.getOpenurl(post.getResource()));
		}
		if ("no".equals(cmd.getDuplicates())) {
			BibTexUtils.removeDuplicates(bibtexList);
			// re-sort list by date in descending order, if nothing else requested
			if ("none".equals(cmd.getSortPage())) {
				cmd.setSortPage("date");
				cmd.setSortPageOrder("desc");
			}
		}
		if (!"none".equals(cmd.getSortPage())) {
			BibTexUtils.sortBibTexList(bibtexList, SortUtils.parseSortKeys(cmd.getSortPage()), SortUtils.parseSortOrders(cmd.getSortPageOrder()) );
		}
	}
	
	
		
	/**
     * retrieve a list of posts from the database logic and add them to the command object
     * 
	 * @param <T> extends Resource
	 * @param <V> extends ResourceViewComand
	 * @param cmd the command object
	 * @param resourceType the resource type
	 * @param groupingEntity the grouping entity
	 * @param groupingName the grouping name
	 * @param itemsPerPage number of items to be displayed on each page
	 */
	protected <T extends Resource, V extends SimpleResourceViewCommand> void setList(V cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, String search, int itemsPerPage) {
		final ListCommand<Post<T>> listCommand = cmd.getListCommand(resourceType);
		// retrieve posts		
		log.debug("getPosts " + resourceType + " " + groupingEntity + " " + groupingName + " " + listCommand.getStart() + " " + itemsPerPage + " " + filter);
		final int start = listCommand.getStart();
		listCommand.setList( this.logic.getPosts(resourceType, groupingEntity, groupingName, tags, hash, order, filter, start, start + itemsPerPage, search) );
		// list settings
		listCommand.setEntriesPerPage(itemsPerPage);
	}
	
	/**
     * retrieve the number of posts from the database logic and add it to the command object
     * 
	 * @param <T> extends Resource
	 * @param <V> extends ResourceViewComand
	 * @param cmd the command object
	 * @param resourceType the resource type
	 * @param groupingEntity the grouping entity
	 * @param groupingName the grouping name
	 * @param itemsPerPage number of items to be displayed on each page
	 * @param constraint
	 */
	protected <T extends Resource, V extends SimpleResourceViewCommand> void setTotalCount(V cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, List<String> tags, String hash, Order order, FilterEntity filter, String search, int itemsPerPage, StatisticsConstraint constraint) {
		final ListCommand<Post<T>> listCommand = cmd.getListCommand(resourceType);
		log.debug("getPostStatistics " + resourceType + " " + groupingEntity + " " + groupingName + " " + listCommand.getStart() + " " + itemsPerPage + " " + filter);
		final int start = listCommand.getStart();
		final int totalCount = this.logic.getPostStatistics(resourceType, groupingEntity, groupingName, tags, hash, order, filter, start, start + itemsPerPage, search, constraint);
		listCommand.setTotalCount(totalCount);
	}


	/**
	 * @param userSettings the loginUsers userSettings
	 */
	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}

	/**
	 * @param listsToInitialise which lists shall be initialised by this controller instance
	 */
	public void setListsToInitialise(final Collection<Class<? extends Resource>> listsToInitialise) {
		this.listsToInitialise = listsToInitialise;
	}
	
	/**
	 * @param logic logic interface
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
	
	/**
	 * Choose which lists of resources to load for the current view. By default,
	 * bibtex and bookmark resources are loaded.  
	 * 
	 * There are some views which are "resource-specific"; when one of these is used,
	 * (e.g., a bibtex-specific one), then all not-needed resource types (e.g., boomark) 
	 * are removed from the lists which are to be initialised.
	 * 
	 * In addition, one can restrict explicitly to resourcetypes via URL parameter; in such 
	 * a case, only the requested resourcetype is kept.
	 * 
	 * 
	 * @param format 
	 * 			- a string describing the requested format (e.g. "html")
	 * @param resourcetype 
	 * 			- a string describing the requested resourcetype (e.g. "bibtex")
	 */
	protected void chooseListsToInitialize(String format, String resourcetype) {
		format = format.toLowerCase();
		if (Views.isBibtexOnlyFormat(format.toLowerCase()) 
				|| (resourcetype != null && resourcetype.equalsIgnoreCase(ResourceType.BIBTEX.getLabel()))) {
			// bibtex only -> remove bookmark
			this.listsToInitialise.remove(Bookmark.class);
		}
		if (Views.isBookmarkOnlyFormat(format.toLowerCase()) 
				|| (resourcetype != null && resourcetype.equalsIgnoreCase(ResourceType.BOOKMARK.getLabel()))) {
			// bookmark only -> remove bibtex
			this.listsToInitialise.remove(BibTex.class);
		}
	}	
	
	protected void startTiming(Class<? extends ResourceListController> controller, String format) {
		log.info("Handling Controller: " + controller.getSimpleName() + ", format: " + format);
		this.startTime = System.currentTimeMillis();
	}
	
	protected void endTiming() {
		Long elapsed = System.currentTimeMillis() - this.startTime;
		log.info("Processing time: " + elapsed + " ms");
	}
	
	/**
	 * Check if only Bibtexs are requested
	 * 
	 * @param <V> - type of the current command object
	 * @param cmd - the current command object
	 * @return true if only bibtexs are requested, false otherwise
	 */
	private  <V extends ResourceViewCommand> Boolean isBibtexOnlyRequested(V cmd) {
		if (ResourceType.BIBTEX.getLabel().equalsIgnoreCase(cmd.getResourcetype()) || 
			(this.listsToInitialise != null && this.listsToInitialise.size() == 1 && this.listsToInitialise.contains(BibTex.class)) ) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check if only Bookmarks are requested
	 * 
	 * @param <V> - type of the current command object
	 * @param cmd - the current command object
	 * @return true if only bookmarks are requested, false otherwise
	 */
	private  <V extends ResourceViewCommand> Boolean isBookmarkOnlyRequested(V cmd) {
		if (ResourceType.BOOKMARK.getLabel().equalsIgnoreCase(cmd.getResourcetype()) || 
			(this.listsToInitialise != null && this.listsToInitialise.size() == 1 && this.listsToInitialise.contains(Bookmark.class)) ) {
			return true;
		}
		return false;
	}	
	
	
	/**
	 * Restrict result lists by range from startIndex to endIndex.
	 * 
	 * @param <T> - resource type
	 * @param <V> - command type
	 * @param cmd - the command object
	 * @param resourceType - the requested resourcetype
	 * @param startIndex - start index
	 * @param endIndex - end index
	 */
	protected <V extends SimpleResourceViewCommand> void restrictResourceList(V cmd, Class<? extends Resource> resourceType, final int startIndex, final int endIndex) {			
			if (BibTex.class.equals(resourceType)) {
				cmd.getBibtex().setList(cmd.getBibtex().getList().subList(startIndex, endIndex));
			}
			if (Bookmark.class.equals(resourceType)) {
				cmd.getBookmark().setList(cmd.getBookmark().getList().subList(startIndex, endIndex));
			}				
	}
}