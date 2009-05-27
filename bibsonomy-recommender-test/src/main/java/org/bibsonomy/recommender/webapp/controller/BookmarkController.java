/**
 * 
 */
package org.bibsonomy.recommender.webapp.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class BookmarkController extends SimpleFormController {

	private static final Log log = LogFactory.getLog(BookmarkController.class);

	@Override
	protected ModelAndView onSubmit(Object command, BindException errors) throws Exception {
		
		System.out.println("got command " + command);
		System.out.println("got errors  " + errors);
		
		
		return super.onSubmit(command, errors);
	}
	
}

