package org.bibsonomy.community.webapp.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.database.BibTexPostManager;
import org.bibsonomy.community.database.BookmarkPostManager;
import org.bibsonomy.community.database.CommunityManager;
import org.bibsonomy.community.database.TagManager;
import org.bibsonomy.community.database.UserSettingsManager;
import org.bibsonomy.community.enums.Ordering;
import org.bibsonomy.community.webapp.command.ClusterViewCommand;
import org.bibsonomy.community.webapp.command.ListCommand;
import org.bibsonomy.community.webapp.command.ResourceClusterViewCommand;
import org.bibsonomy.community.webapp.command.ResourceViewCommand;
import org.bibsonomy.community.webapp.util.CreateRandomPosts;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.springframework.web.servlet.ModelAndView;

public class ClusterListController extends AbstractBaseController<ResourceClusterViewCommand> {
	private final static Log log = LogFactory.getLog(ClusterListController.class);

	private static final Integer CLUSTERLIMIT = 6;
	private static final int USERCLOUDLIMIT   = 25;
	private static final int TAGCLOUDLIMIT    = 25;
	private static final int BIBTEXLIMIT      = 5;
	private static final int BOOKMARKLIMIT    = 5;

	
	/** bibtex posts */
	private BibTexPostManager bibTexManager;
	/** bookmark posts */
	private BookmarkPostManager bookmarkManager;
	/** tag clouds */
	private TagManager tagManager;
	/** community access */
	private CommunityManager communityManager;
	/** user settings accsess */
	private UserSettingsManager userSettingsManager;
	
	public ClusterListController() {
		setCommandClass(ResourceClusterViewCommand.class);
	}

	//------------------------------------------------------------------------
	// controller interface
	//------------------------------------------------------------------------
	@Override
	protected ResourceClusterViewCommand instantiateCommand() {
		return new ResourceClusterViewCommand();
	}

	@Override
	public ModelAndView workOn(ResourceClusterViewCommand command) {
		if( command.getContext().isUserLoggedIn() ) {
			final int limit  = (command.getLimit()==0)?CLUSTERLIMIT:command.getLimit();
			final int offset = (command.getOffset()==0)?0:command.getOffset();
			org.bibsonomy.model.User user = command.getContext().getLoginUser(); 
			Integer runId = 0; 
			try {
				runId = this.userSettingsManager.getCurrentAlgorithm(user.getName());
				Integer communityCount = this.communityManager.getNumberOfCommunities(runId);
				command.setTotal(communityCount);
			} catch (Exception e) {
				log.error("Error fetching number of clusters", e);
			}
			Collection<ResourceCluster> communities = this.communityManager.getCommunities(runId, USERCLOUDLIMIT, TAGCLOUDLIMIT, BIBTEXLIMIT, BOOKMARKLIMIT, limit, offset);
			command.setClusters(communities);
			// populateResources(command,25);
		} else {
			command.setClusters(new ArrayList<ResourceCluster>());
		}
		
		if( "json".equals(getOutputFormat(command)) ) {
			return new ModelAndView("export/json/resources", "command", command);
		} else {
			populateResources(command, 25);
			return new ModelAndView("export/html/clusterlist", "command", command);
		}
	}
	

	//------------------------------------------------------------------------
	// helper functions
	//------------------------------------------------------------------------
	private void populateResources(final ResourceClusterViewCommand command, final int entriesPerPage) {
		for( ResourceCluster community : command.getClusters() ) {
			ResourceClusterViewCommand newCommand = new ResourceClusterViewCommand();
			populateResources(newCommand, community.getBibtex(), community.getBookmark(), entriesPerPage);
			Collection<ResourceCluster> clusters = new ArrayList<ResourceCluster>();
			clusters.add(community);
			newCommand.setClusters(clusters);
		}
		
	}
	
	private void populateResources(final ResourceClusterViewCommand resources, final Collection<Post<BibTex>> bibTexPosts, final Collection<Post<Bookmark>> bookmarkPosts, final int entriesPerPage) {
		ListCommand<Post<BibTex>> bibTex      = new ListCommand<Post<BibTex>>(resources);
		ListCommand<Post<Bookmark>> bookmarks = new ListCommand<Post<Bookmark>>(resources);
	
		bookmarks.setList((List<Post<Bookmark>>) bookmarkPosts);
		bookmarks.setEntriesPerPage(entriesPerPage);
		bibTex.setList((List<Post<BibTex>>) bibTexPosts);
		bibTex.setEntriesPerPage(entriesPerPage);

		resources.setBibtex(bibTex);
		resources.setBookmark(bookmarks);
	}

	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setBibTexManager(BibTexPostManager bibtexManager) {
		this.bibTexManager = bibtexManager;
	}

	public BibTexPostManager getBibTexManager() {
		return bibTexManager;
	}

	public void setBookmarkManager(BookmarkPostManager bookmarkManager) {
		this.bookmarkManager = bookmarkManager;
	}

	public BookmarkPostManager getBookmarkManager() {
		return bookmarkManager;
	}

	public void setTagManager(TagManager tagManager) {
		this.tagManager = tagManager;
	}

	public TagManager getTagManager() {
		return tagManager;
	}

	public void setCommunityManager(CommunityManager communityManager) {
		this.communityManager = communityManager;
	}

	public CommunityManager getCommunityManager() {
		return communityManager;
	}

	public void setUserSettingsManager(UserSettingsManager userSettingsManager) {
		this.userSettingsManager = userSettingsManager;
	}

	public UserSettingsManager getUserSettingsManager() {
		return userSettingsManager;
	}

}