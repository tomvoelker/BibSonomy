package org.bibsonomy.webapp.controller;

import java.util.List;

import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.AdminAjaxCommand;
import org.bibsonomy.webapp.command.LoggingCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Controller for ajax clicklog requests 
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class LoggingController extends AjaxController implements MinimalisticController<LoggingCommand> {

	public View workOn(LoggingCommand command) {
		final String action = command.getAction();

		
//		System.out.println("1:"+command.getUserName());
//		System.out.println("10:"+command.getDompath());
//		System.out.println("11:"+command.getPageurl());
//		System.out.println("12:"+command.getAtitle());
//		System.out.println("13:"+command.getAhref());
		
		return Views.AJAX;
	}	


	
	public LoggingCommand instantiateCommand() {
//		System.out.println("Logging: Bibsonomy2");
		return new LoggingCommand();
	}
}