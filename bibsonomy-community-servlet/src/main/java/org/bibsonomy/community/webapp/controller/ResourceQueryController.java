package org.bibsonomy.community.webapp.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.community.webapp.command.ClusterViewCommand;
import org.bibsonomy.community.webapp.command.ListCommand;
import org.bibsonomy.community.webapp.command.SimpleResourceViewCommand;
import org.bibsonomy.community.webapp.util.CreateRandomPosts;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractCommandController;

public class ResourceQueryController extends AbstractCommandController {

	public ResourceQueryController() {
		setCommandClass(ClusterViewCommand.class);
	}
	
	@Override
	protected ModelAndView handle(HttpServletRequest request,
			HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		ClusterViewCommand resources = (ClusterViewCommand)command;
		
		
		populateResources(resources, 10);
		resources.setApplicationName("folkeTest");
		
        return new ModelAndView("export/json/resources", "command", resources);
	}

	private void populateResources(ClusterViewCommand resources, int nPosts) {
		ListCommand<Post<BibTex>> bibTex      = new ListCommand<Post<BibTex>>(resources);
		ListCommand<Post<Bookmark>> bookmarks = new ListCommand<Post<Bookmark>>(resources);

		List<Post<BibTex>> bibTexPosts     = new LinkedList<Post<BibTex>>(); 
		List<Post<Bookmark>> bookmarkPosts = new LinkedList<Post<Bookmark>>();
		
		for( int i=0; i<nPosts; i++) {
			bibTexPosts.add(CreateRandomPosts.bibTexPost());
			bookmarkPosts.add(CreateRandomPosts.bookmarkPost());
		}
		
		bibTex.setList(bibTexPosts);
		bookmarks.setList(bookmarkPosts);
		
		resources.setBibtex(bibTex);
		resources.setBookmark(bookmarks);
	}
	
}