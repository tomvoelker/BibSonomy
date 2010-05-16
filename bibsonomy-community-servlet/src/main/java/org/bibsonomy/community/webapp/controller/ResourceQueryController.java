package org.bibsonomy.community.webapp.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.webapp.command.ClusterViewCommand;
import org.bibsonomy.community.webapp.command.ListCommand;
import org.bibsonomy.community.webapp.util.CreateRandomPosts;
import org.bibsonomy.community.webapp.util.RequestWrapperContext;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ResourceQueryController extends AbstractBaseController {
	private final static Log log = LogFactory.getLog(ResourceQueryController.class); 
	
	public ResourceQueryController() {
		setCommandClass(ClusterViewCommand.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object commandObj, BindException errors)
			throws Exception {
		ClusterViewCommand<Tag> command = (ClusterViewCommand<Tag>)commandObj;
		initializeCommand(command, request);
		
		log.info("Got settings for "+command.getClusters().size()+" clusters");
		
		final RequestWrapperContext context = command.getContext();
		if( context.isUserLoggedIn() ) {
			populateResources(command, 10, 10);
			command.setApplicationName("folkeTest");
		}
		
        return new ModelAndView("export/"+getOutputFormat(command)+"/resources", "command", command);
	}

	private void populateResources(ClusterViewCommand<Tag> resources, int nPosts, int entriesPerPage) {
		ListCommand<Post<BibTex>> bibTex      = new ListCommand<Post<BibTex>>(resources);
		ListCommand<Post<Bookmark>> bookmarks = new ListCommand<Post<Bookmark>>(resources);

		List<Post<BibTex>> bibTexPosts     = new LinkedList<Post<BibTex>>(); 
		List<Post<Bookmark>> bookmarkPosts = new LinkedList<Post<Bookmark>>();
		
		for( int i=0; i<nPosts; i++) {
			bibTexPosts.add(CreateRandomPosts.bibTexPost());
			bookmarkPosts.add(CreateRandomPosts.bookmarkPost());
		}
		
		bibTex.setList(bibTexPosts);
		bibTex.setEntriesPerPage(entriesPerPage);
		bookmarks.setList(bookmarkPosts);
		bookmarks.setEntriesPerPage(entriesPerPage);
		
		resources.setBibtex(bibTex);
		resources.setBookmark(bookmarks);
	}
	
}