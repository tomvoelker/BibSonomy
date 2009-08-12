package org.bibsonomy.spielwiese.springmvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class HomepageController implements Controller {

	private static final Log log = LogFactory.getLog(HomepageController.class);

	public ModelAndView handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		log.debug(this.getClass().getSimpleName());
		log.debug("Path: " + request.getRequestURI());

		final Map<String, Object> model = new HashMap<String, Object>();
		model.put("projectName", "BibSonomy");

		final List<Post<Bookmark>> bookmarks = new ArrayList<Post<Bookmark>>(); // FIXME: RestDatabaseManager.getInstance().getPosts("cschenk", Bookmark.class, null, null, null, null, null, 0, 10, null);
		model.put("bookmarks", bookmarks);

		return new ModelAndView("home", model);
	}
}