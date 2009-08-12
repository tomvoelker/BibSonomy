package org.bibsonomy.spielwiese.springmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class PostsHandler implements Controller {
	
	private static final Log log = LogFactory.getLog(PostsHandler.class);

	public ModelAndView handleRequest(final HttpServletRequest request, final HttpServletResponse response) {
		log.debug(this.getClass().getSimpleName());
		log.debug("Path: " + request.getRequestURI());
		return new ModelAndView("posts");
	}
}