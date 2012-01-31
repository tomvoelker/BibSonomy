package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.UserRelationSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.comparators.UserComparator;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.util.EnumUtils;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.SphereResourceViewCommand;
import org.bibsonomy.webapp.command.TagCloudCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

/**
 * controller responsible for the following pages:
 *      - /spheres
 * 		- /spheres/RELATION
 * 		- /spheres/RELATION/TAG
 * 
 * @author Nils Raabe, Folke Mitzlaff
 * @version $Id$
 */
public class SpheresPageController extends SingleResourceListControllerWithTags implements MinimalisticController<SphereResourceViewCommand> {
	private static final Log log = LogFactory.getLog(SpheresPageController.class);

	private static final TagCloudSort TAG_CLOUD_SORT = TagCloudSort.ALPHA;
	private static final int TAG_CLOUD_MINFREQ = 3;
	private static final int TAG_CLOUD_SIZE = 25;
	
	@Override
	public View workOn(final SphereResourceViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()){
			throw new AccessDeniedException("please log in");
		}
		
		final String sphereName = command.getSphereName();
		if (present(sphereName)) {
			/*
			 * handle 
			 * - /spheres/RELATION
			 * - /spheres/RELATION/TAG
			 */
			log.debug("Displaying details for sphere '" + sphereName + "'");
			return handleDetailsView(command, context.getLoginUser());
		}
		
		/*
		 *  handle
		 *  - /spheres
		 */
		log.debug("Displaying list of all spheres");
		return handleListView(command, context.getLoginUser());
	}


	/**
	 * display details for given sphere, filtering posts according to given tag
	 * 
	 * @param command the parameter object
	 * @param loginUser login user
	 * @return
	 */
	private View handleDetailsView(final SphereResourceViewCommand command, final User loginUser) {
		final String sphereName 				= command.getSphereName();
		final List<String> requestedTags		= command.getRequestedTagsList();
		final String format 					= command.getFormat();
		final List<String> requestedUserTags 	= command.getRequestedTagsList();

		// if no Userrelation given -> error
		if (!present(sphereName)) {
			throw new MalformedURLSchemeException("error.group_page_without_groupname");
		}

		// get all friends of the given sphere
		final List<User> relatedUsers = this.logic.getUserRelationship(loginUser.getName(), UserRelation.OF_FRIEND, SystemTagsUtil.buildSystemTagString(UserRelationSystemTag.NAME, sphereName));
		
		// if no friends are in this relation -> error
		if (!present(relatedUsers)) {
			throw new MalformedURLSchemeException("error.no_friends_in_this_friendrelation");
		}

		// get all bookmarks and publication posts for the requested tag - if no tag given -> relationTags is an empty List
		final List<String> queryTags = new ArrayList<String>();
		
		// add the requested sphere name's system tag to the relation tags
		queryTags.add(SystemTagsUtil.buildSystemTagString(UserRelationSystemTag.NAME, sphereName));
		
		// add the tags from the user
		if (present(requestedTags)) {
			queryTags.addAll(requestedTags);
		}
		
		// set all resourcetypes for the given sphere
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(format, command.getResourcetype())) {			
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			this.setList(command, resourceType, GroupingEntity.FRIEND, loginUser.getName(), queryTags, null, Order.ADDED, null, null, entriesPerPage);
			this.postProcessAndSortList(command, resourceType);
		}	
		
		// set the tags / related tags for the sphere
		this.setTags(command, Resource.class, GroupingEntity.FRIEND, loginUser.getName(), null, queryTags, null, Integer.MAX_VALUE, null);

		if (present(requestedUserTags)) {
			this.setRelatedTags(command, Resource.class, GroupingEntity.FRIEND, loginUser.getName(), null, queryTags, Order.ADDED, 0, 20, null);
		}
		
		//Set the Users in the Sphere
		command.setRelatedUsers(relatedUsers);

		if ("html".equals(format)) {
			return Views.SPHEREDETAILS;
		}
		return Views.getViewByFormat(format);
	}


	@Override
	public SphereResourceViewCommand instantiateCommand() {
		return new SphereResourceViewCommand();
	}
	
	/**
	 * display list of all spheres for the given login user
	 * 
	 * TODO: limit number of spheres per page
	 * 
	 * @param command the parameter object
	 * @param loginUser login user
	 * @return
	 */
	private View handleListView(final SphereResourceViewCommand command, final User loginUser) {
		final List<User> relatedUsers = this.logic.getUserRelationship(loginUser.getName(), UserRelation.OF_FRIEND, null);
		
		// XXX: we collect all information by hand - this should be done already
		//      in an appropriate database query and result mapping
		final Map<String, Set<User>> spheres = new TreeMap<String, Set<User>>();
		
		// loop over each related user and add to each sphere he/she 
		// belongs to (as given by the relation system tags)
		for (final User relatedUser : relatedUsers) {
			for (final Tag tag : relatedUser.getTags() ) {
				String relationName = null;
				if (SystemTagsUtil.isSystemTag(tag.getName(), UserRelationSystemTag.NAME)) {
					relationName = SystemTagsUtil.extractArgument(tag.getName());
				}
				if (present(relationName)) {
					if (!spheres.containsKey(relationName)) {
						spheres.put(relationName, new TreeSet<User>(new UserComparator()));
					}
					// add user to the sphere given by the relation name
					final Set<User> sphereUsers = spheres.get(relationName);
					sphereUsers.add(relatedUser);
				}
			}
		}
		command.setSpheres(spheres);
		
		// XXX: we collect all information by hand - this should be done already
		//      in an appropriate database query and result mapping
		final Map<String, ListCommand<Post<Bookmark>>> spheresBMPosts = new HashMap<String, ListCommand<Post<Bookmark>>>();
		final Map<String, ListCommand<Post<BibTex>>> spheresPBPosts = new HashMap<String, ListCommand<Post<BibTex>>>();
		final Map<String, TagCloudCommand> spheresTagClouds = new HashMap<String, TagCloudCommand>();
		
		for (final Entry<String,Set<User>> sphere : spheres.entrySet() ) {
			// get tag cloud for current sphere
			final List<String> sphereTags = new ArrayList<String>();
			sphereTags.add(SystemTagsUtil.buildSystemTagString(UserRelationSystemTag.NAME, sphere.getKey()));
			
			// get bookmarks and publications for current sphere 
			final List<Post<Bookmark>> bmPosts = new ArrayList<Post<Bookmark>>();// logic.getPosts(Bookmark.class, GroupingEntity.FRIEND, loginUser.getName(), sphereTags, null, Order.ADDED, null, 0, 5, null);
			final List<Post<BibTex>> pbPosts = new ArrayList<Post<BibTex>>(); // logic.getPosts(BibTex.class, GroupingEntity.FRIEND, loginUser.getName(), sphereTags, null, Order.ADDED, null, 0, 5, null);
			
			// pack resource lists into resource list commands (for according jsps)
			final ListCommand<Post<Bookmark>> bmListCommand = new ListCommand<Post<Bookmark>>(command);
			final ListCommand<Post<BibTex>> pbListCommand = new ListCommand<Post<BibTex>>(command);
			
			bmListCommand.setList(bmPosts);
			pbListCommand.setList(pbPosts);
			
			// store posts into result map
			spheresBMPosts.put(sphere.getKey(), bmListCommand);
			spheresPBPosts.put(sphere.getKey(), pbListCommand);

			// set tag cloud
			final List<Tag> aspectTagCloud= logic.getTags(Resource.class, GroupingEntity.FRIEND, loginUser.getName(), null, sphereTags, null, Order.FREQUENCY, 0, 25, null, null);
			final TagCloudCommand tagCloudCommand = new TagCloudCommand();
			tagCloudCommand.setMaxCount(TAG_CLOUD_SIZE);
			tagCloudCommand.setMinFreq(TAG_CLOUD_MINFREQ);
			tagCloudCommand.setSort(TAG_CLOUD_SORT);
			tagCloudCommand.setTags(aspectTagCloud);
			spheresTagClouds.put(sphere.getKey(), tagCloudCommand);
			
		}
		
		// retrieve similar users, by the given user similarity measure
		final UserRelation userRelation = EnumUtils.searchEnumByName(UserRelation.values(), command.getUserSimilarity());
		final List<User> similarUsers = this.logic.getUsers(null, GroupingEntity.USER, command.getContext().getLoginUser().getName(), null, null, null, userRelation, null, 0, 10);	
		command.getRelatedUserCommand().setRelatedUsers(similarUsers);
		
		
		// fill command object
		command.setSpheresBMPosts(spheresBMPosts);
		command.setSpheresPBPosts(spheresPBPosts);
		command.setSpheresTagClouds(spheresTagClouds);
		log.debug("return sphere list "+ command.getFormat());
		// all done
		final String format = command.getFormat();
		if ("html".equals(format)) {
			return Views.SPHERELIST;
		}
		return Views.getViewByFormat(format);
	}	

}
