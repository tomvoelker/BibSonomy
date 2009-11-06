package org.bibsonomy.webapp.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.CvPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
/**
 * @author philipp
 * @version $Id$
 */
public class CvPageController extends SingleResourceListController implements MinimalisticController<CvPageCommand> {
	private static final Log log = LogFactory.getLog(CvPageController.class);


		/**
		 * implementation of {@link MinimalisticController} interface
		 */
		public View workOn(CvPageCommand command) {
			// fill out overhead
			command.setPageTitle("Curriculum vitae");
			
			if(!(command.getRequestedUser() == null)) {
			command.setUser(this.logic.getUserDetails(command.getRequestedUser()));
			} else {
				
				return Views.ERROR;
			}
			/*
			//user has to be logged in to see his cv
			if(command.getContext().isUserLoggedIn()) {
			command.setUser(command.getContext().getLoginUser());
			} else {

				log.warn("Invalid query /user without username");
				throw new MalformedURLSchemeException("error.general.login");
//				return Views.ERROR;
			}
			*/
			return Views.CVPAGE;
		}

		/**
		 * implementation of {@link MinimalisticController} interface
		 */
		public CvPageCommand instantiateCommand() {
			return new CvPageCommand();
		}	
	}

	
