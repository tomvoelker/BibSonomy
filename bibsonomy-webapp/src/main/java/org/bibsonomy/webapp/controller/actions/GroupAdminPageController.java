package org.bibsonomy.webapp.controller.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.webapp.command.actions.GroupAdminCommand;
import org.bibsonomy.webapp.controller.SingleResourceListController;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author mwa
 * @version $Id$
 */
public class GroupAdminPageController extends SingleResourceListController implements MinimalisticController<GroupAdminCommand>, ErrorAware, ValidationAwareController<GroupAdminCommand>, RequestAware {
	
	private static final Log log = LogFactory.getLog(GroupAdminPageController.class);
	
	public View workOn(GroupAdminCommand command) {
		log.debug("--> GroupAdminPageController: workOn() called");
		
		// no group given -> error
		if (command.getRequestedGroup() == null) {
			log.error("Invalid query /group without groupname");
			throw new MalformedURLSchemeException("error.group_page_without_groupname");
		}	
		
		String groupName = command.getRequestedGroup();
		
		command.setGroup(this.logic.getGroupDetails(groupName));
		System.out.println("tagsets: "+command.getGroup().getTagSets().size());
		return Views.ADMIN_GROUP;
	}
	
	public GroupAdminCommand instantiateCommand() {
		return new GroupAdminCommand();
	}
	
	public Errors getErrors() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setErrors(Errors errors) {
		// TODO Auto-generated method stub
		
	}
	
	public Validator<GroupAdminCommand> getValidator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean isValidationRequired(GroupAdminCommand command) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setRequestLogic(RequestLogic requestLogic) {
		// TODO Auto-generated method stub
		
	}

}
