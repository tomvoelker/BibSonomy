package org.bibsonomy.webapp.controller.actions;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.webapp.command.actions.UserSamlActivationCommand;
import org.bibsonomy.webapp.controller.ResourceListController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.UserSamlActivationValidation;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * @author nilsraabe
 * @version $Id$
 */
public class UserSamlActivationController extends ResourceListController implements ErrorAware, ValidationAwareController<UserSamlActivationCommand>{

	/**
	 * After successful activation, the user is redirected to this page. 
	 */
	private final String successRedirect = "/register_saml_success";

	/**
	 * like the homepage, only 50 tags are shown in the tag cloud
	 */
	private static final int MAX_TAGS 	= 50;
	private Errors errors 				= null;
	private static final Log log 		= LogFactory.getLog(UserSamlActivationController.class);
	
	@Override
	public UserSamlActivationCommand instantiateCommand() {

		log.info("UserSamlActivationCommand in UserSamlActivationController initialized");
		
		return new UserSamlActivationCommand();
	}

	@Override
	public View workOn(UserSamlActivationCommand command) {

		log.info("UserSamlActivationController starts WorkOn()");
		
		final RequestWrapperContext context 	= command.getContext();
		final String ckey 						= context.getCkey();

		/**
		 * If User pressed the "activate submit button", the checkbox must be checked to redirect to 
		 * the success page /register_saml_success
		 * 
		 * otherwise the UserSamlActivationValidation.java validator throws an error message 
		 */
		if(command.isCheckboxAccept()) {
			return new ExtendedRedirectView(successRedirect);
		}		

		/**
		 * Set the tags and the news in the sidebar
		 */
		setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, null, MAX_TAGS, null);
		command.setNews(this.logic.getPosts(Bookmark.class, GroupingEntity.GROUP, "kde", Arrays.asList("bibsonomynews"), null, null, null, null, null, null, 0, 3));
		
		return Views.ACTIVATION_USER_SAML;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	@Override
	public boolean isValidationRequired(UserSamlActivationCommand command) {
		return true;
	}

	@Override
	public Validator<UserSamlActivationCommand> getValidator() {
		return new UserSamlActivationValidation();
	}
}
