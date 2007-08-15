package org.bibsonomy.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class PostsController implements Controller {
	
	private static final Logger log = Logger.getLogger(PostsController.class);

	public ModelAndView handleRequest(final HttpServletRequest request, final HttpServletResponse response) {
		log.debug(this.getClass().getSimpleName());
		log.debug("Path: " + request.getRequestURI());
		return new ModelAndView("posts");
	}
}