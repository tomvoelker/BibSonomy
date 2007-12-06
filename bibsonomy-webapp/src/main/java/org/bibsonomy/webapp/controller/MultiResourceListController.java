package org.bibsonomy.webapp.controller;


import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagCloudStyle;
import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.ResourceViewCommand;
import org.bibsonomy.webapp.command.TagCloudCommand;

/**
 * controller for retrieving multiple windowed lists with resources. These are currently the bookmark an the bibtex list
 * 
 * @author Jens Illig
 */
public abstract class MultiResourceListController {
	private static final Logger log = Logger.getLogger(MultiResourceListController.class);
	
	protected LogicInterface logic;
	protected UserSettings userSettings;
	protected Collection<Class<? extends Resource>> listsToInitialise;
			
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
	protected <T extends Resource, V extends ResourceViewCommand> void setTags(V cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, String regex, List<String> tags, int start, int end) {
		TagCloudCommand tagCloudCommand = cmd.getTagcloud();
		// retrieve tags
		tagCloudCommand.setTags( this.logic.getTags(resourceType, groupingEntity, groupingName, regex, tags, start, end));
		log.debug("getTags " + " " + groupingEntity + " " + groupingName);
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
	protected <T extends ResourceViewCommand> void postProcessList(T cmd, Class<? extends Resource> resourceType) {				
		if ( resourceType == BibTex.class ) {
			for (Post<BibTex> post : cmd.getBibtex().getList()) {
				// insert openURL into bibtex objects
				post.getResource().setOpenURL(BibTexUtils.getOpenurl(post.getResource()));
			}
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
	 * @param totalCount total number of items in the list
	 */
	protected <T extends Resource, V extends ResourceViewCommand> void setList(V cmd, Class<T> resourceType, GroupingEntity groupingEntity, String groupingName, int itemsPerPage, int totalCount) {
		ListCommand<Post<T>> listCommand = cmd.getListCommand(resourceType); 
		// retrieve posts		
		listCommand.setList( this.logic.getPosts(resourceType, groupingEntity, groupingName, null, null, null, listCommand.getStart(), listCommand.getStart() + userSettings.getListItemcount(), null) );
		log.debug("getPosts " + resourceType + " " + groupingEntity + " " + groupingName + " " + listCommand.getStart() + " " + userSettings.getListItemcount());
		// list settings
		listCommand.setEntriesPerPage(itemsPerPage);
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
}