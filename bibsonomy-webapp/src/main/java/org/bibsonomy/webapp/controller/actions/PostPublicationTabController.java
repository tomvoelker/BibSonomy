package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.PostPublicationTabCommand;
import org.bibsonomy.webapp.controller.MultiResourceListController;
import org.bibsonomy.webapp.controller.PopularPageController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
/**
 * @author ema
 * @version $Id$
 */
public class PostPublicationTabController extends MultiResourceListController implements MinimalisticController<PostPublicationTabCommand>{
	private static final Log log = LogFactory.getLog(PopularPageController.class);
	
	
	public View workOn(final PostPublicationTabCommand command) {
		
		log.debug(this.getClass().getSimpleName());
		this.startTiming(this.getClass(), command.getFormat());
		
		this.endTiming();
		return Views.POST_BIBTEX;			
	}
	
	public PostPublicationTabCommand instantiateCommand() {
		return new PostPublicationTabCommand();
	}

}
