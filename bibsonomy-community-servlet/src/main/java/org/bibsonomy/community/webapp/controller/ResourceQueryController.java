package org.bibsonomy.community.webapp.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.community.webapp.command.ClusterViewCommand;
import org.bibsonomy.community.webapp.command.ListCommand;
import org.bibsonomy.community.webapp.util.CreateRandomPosts;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class ResourceQueryController extends AbstractCommandController {
	private static final String OUTPUT_FORMAT = "html"; // "html" or "json"
	public ResourceQueryController() {
		setCommandClass(ClusterViewCommand.class);
	}
	
	protected void initializeCommand(ClusterViewCommand command) {
	}
	
	@Override
	protected ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		ClusterViewCommand resources = (ClusterViewCommand)command;
		initializeCommand(resources);
		
		populateResources(resources, 10, 10);
		resources.setApplicationName("folkeTest");
		
        return new ModelAndView("export/"+OUTPUT_FORMAT+"/resources", "command", resources);
	}

	private void populateResources(ClusterViewCommand resources, int nPosts, int entriesPerPage) {
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