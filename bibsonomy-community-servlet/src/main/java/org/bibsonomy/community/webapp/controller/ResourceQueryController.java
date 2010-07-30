package org.bibsonomy.community.webapp.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.database.BibTexPostManager;
import org.bibsonomy.community.database.BookmarkPostManager;
import org.bibsonomy.community.database.CommunityManager;
import org.bibsonomy.community.database.TagManager;
import org.bibsonomy.community.enums.Ordering;
import org.bibsonomy.community.webapp.command.ClusterViewCommand;
import org.bibsonomy.community.webapp.command.ListCommand;
import org.bibsonomy.community.webapp.command.ResourceClusterViewCommand;
import org.bibsonomy.community.webapp.util.CreateRandomPosts;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.community.model.Cluster;
import org.bibsonomy.community.model.Post;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.Tag;
import org.springframework.web.servlet.ModelAndView;

public class ResourceQueryController extends AbstractBaseController<ResourceClusterViewCommand> {
	private final static Log log = LogFactory.getLog(ResourceQueryController.class);

	private static final Integer RESOURCELIMIT = 100;
	
	/** bibtex posts */
	private BibTexPostManager bibTexManager;
	/** bookmark posts */
	private BookmarkPostManager bookmarkManager;
	/** tag clouds */
	private TagManager tagManager;
	/** community access */
	private CommunityManager communityManager;
	
	public ResourceQueryController() {
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
			List<Post<BibTex>> bibTexPosts = new LinkedList<Post<BibTex>>();
			List<Post<Bookmark>> bookmarkPosts = new LinkedList<Post<Bookmark>>();
			
			final int limit  = (command.getLimit()==0)?RESOURCELIMIT:command.getLimit();
			final int offset = (command.getOffset()==0)?0:command.getOffset();
			
			final Ordering ordering = getResourceOrdering(command);
			
			int i = 0;
			for( ResourceCluster cluster : command.getClusters() ) {
				log.info("Querying for community "+cluster.getClusterID()+"...");
				Collection<Post<BibTex>> btposts = this.bibTexManager.getPostsForCommunity(cluster.getClusterID(), ordering, limit, offset);
				for( Post<?> post : btposts ) {
					post.setCustomFlag(i);
				}
				cluster.setBibtex(btposts);
				bibTexPosts.addAll(btposts);
				Collection<Post<Bookmark>> bmposts = this.bookmarkManager.getPostsForCommunity(cluster.getClusterID(), ordering, limit, offset); 
				for( Post<?> post : bmposts ) {
					post.setCustomFlag(i);
				}
				cluster.setBookmark(bmposts);
				bookmarkPosts.addAll(bmposts);
				i++;
			}
			
			populateResources(command, bibTexPosts, bookmarkPosts, 50);
		}
		return new ModelAndView("export/"+getOutputFormat(command)+"/resources", "command", command);
	}
	
	//------------------------------------------------------------------------
	// helper functions
	//------------------------------------------------------------------------
	private void populateResources(final ResourceClusterViewCommand resources, final List<Post<BibTex>> bibTexPosts, final List<Post<Bookmark>> bookmarkPosts, final int entriesPerPage) {
		ListCommand<Post<BibTex>> bibTex      = new ListCommand<Post<BibTex>>(resources);
		ListCommand<Post<Bookmark>> bookmarks = new ListCommand<Post<Bookmark>>(resources);
	
		bookmarks.setList(bookmarkPosts);
		bookmarks.setEntriesPerPage(entriesPerPage);
		bibTex.setList(bibTexPosts);
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

}