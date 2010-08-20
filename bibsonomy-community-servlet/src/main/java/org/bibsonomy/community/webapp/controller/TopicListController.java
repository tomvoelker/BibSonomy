package org.bibsonomy.community.webapp.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.User;
import org.bibsonomy.community.webapp.command.ListCommand;
import org.bibsonomy.community.webapp.command.ResourceClusterViewCommand;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.topics.database.TopicManagerLocal;
import org.bibsonomy.topics.search.TopicSearcher;
import org.springframework.web.servlet.ModelAndView;

/**
 * controller for retrieving a list of topics
 * 
 * @author andi
 *
 */
public class TopicListController extends AbstractBaseController<ResourceClusterViewCommand> {
	
	@SuppressWarnings("unused")
	private final static Log log = LogFactory.getLog(TopicListController.class);

	private static final Integer CLUSTERLIMIT = 6;
	private static final int USERCLOUDLIMIT   = 25;
	private static final int TAGCLOUDLIMIT    = 25;
	private static final int BIBTEXLIMIT      = 5;
	private static final int BOOKMARKLIMIT    = 5;
	
	/** db access for topics */
	private TopicManagerLocal topicManager;
	
	public TopicListController() {
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

		int modelId = TopicSearcher.getInstance().getModelId();
		
		if( command.getContext().isUserLoggedIn() ) {
			User user = new User(command.getContext().getLoginUser());
			try {
				topicManager.addUserModel(modelId, user);
			} catch (SQLException e) {
				// TODO add error handling
				e.printStackTrace();
			}
		}	
		
		final int limit  = (command.getLimit()==0)?CLUSTERLIMIT:command.getLimit();
		final int offset = ((command.getOffset()==0)?0:command.getOffset());
		
		try {
			Collection<ResourceCluster> communities = topicManager.getTopics(modelId, USERCLOUDLIMIT, TAGCLOUDLIMIT, BIBTEXLIMIT, BOOKMARKLIMIT, limit, offset);
			int numTopics = topicManager.getNumTopics(modelId);
			command.setTotal(numTopics);
			command.setClusters(communities);
		} catch (SQLException e) {
			// TODO add error handling
			e.printStackTrace();
			command.setClusters(new ArrayList<ResourceCluster>());
		}
		
		command.setPageTitle("Topics");
		
		return new ModelAndView("export/json/resources", "command", command);
	}
	

	//------------------------------------------------------------------------
	// helper functions
	//------------------------------------------------------------------------
	@SuppressWarnings("unused")
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
	public TopicManagerLocal getTopicManager() {
		return topicManager;
	}

	public void setTopicManager(TopicManagerLocal topicManager) {
		this.topicManager = topicManager;
	}

}