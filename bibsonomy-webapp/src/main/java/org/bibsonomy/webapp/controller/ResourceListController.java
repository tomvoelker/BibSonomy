package org.bibsonomy.webapp.controller;


import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.common.enums.TagCloudStyle;
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
 * controller for retrieving multiple windowed lists with resources. These are currently the bookmark an the bibtex list
 * 
 * @author Jens Illig
 */
public abstract class ResourceListController {
	private static final Logger log = Logger.getLogger(ResourceListController.class);
	
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
		TagCloudCommand tagCloudCommand = cmd.getTagcloud();
		// retrieve tags
		log.debug("getTags " + " " + groupingEntity + " " + groupingName);
		tagCloudCommand.setTags( this.logic.getTags(resourceType, groupingEntity, groupingName, regex, tags, hash, order, start, end, search, null));
		// retrieve tag cloud settings
		tagCloudCommand.setStyle(TagCloudStyle.getStyle(userSettings.getTagboxStyle()));
		tagCloudCommand.setSort(TagCloudSort.getSort(userSettings.getTagboxSort()));
		tagCloudCommand.setMinFreq(userSettings.getTagboxMinfreq());
		tagCloudCommand.setMaxFreq(TagUtils.getMaxUserCount(tagCloudCommand.getTags()));
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
		listCommand.setList( this.logic.getPosts(resourceType, groupingEntity, groupingName, tags, hash, order, filter, listCommand.getStart(), listCommand.getStart() + itemsPerPage, search) );
		// list settings
		listCommand.setEntriesPerPage(itemsPerPage);
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
	 * @param format
	 * @param resourcetype
	 */
	protected void chooseListsToInitialize(String format, String resourcetype) {
		format = format.toLowerCase();
		if (Views.isBibtexOnlyFormat(format.toLowerCase()) 
				|| (resourcetype != null && resourcetype.toLowerCase().equals("bibtex"))) {
			this.listsToInitialise.remove(Bookmark.class);
		}
		if (Views.isBookmarkOnlyFormat(format.toLowerCase()) 
				|| (resourcetype != null && resourcetype.toLowerCase().equals("bookmark"))) {
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
}