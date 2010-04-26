package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

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
 * TODO: check methods (auto generated)
 * 
 * @author mwa
 * @version $Id$
 */
public class GroupAdminPageController extends SingleResourceListController implements MinimalisticController<GroupAdminCommand>, ErrorAware, ValidationAwareController<GroupAdminCommand>, RequestAware {
	private static final Log log = LogFactory.getLog(GroupAdminPageController.class);
	
	public View workOn(final GroupAdminCommand command) {
		log.debug("GroupAdminPageController: workOn() called");
		
		final String groupName = command.getRequestedGroup();
		
		// no group given -> error
		if (!present(groupName)) {
			log.error("Invalid query /group without groupname");
			throw new MalformedURLSchemeException("error.group_page_without_groupname");
		}
		
		command.setGroup(this.logic.getGroupDetails(groupName));
		log.debug("tagsets: " + command.getGroup().getTagSets().size());
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
