package org.bibsonomy.webapp.controller.actions;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.bibsonomy.webapp.command.actions.LimitedAccountActivationCommand;
import org.bibsonomy.webapp.controller.ResourceListController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.LimitedAccountActivationValidation;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;

/**
 * @author nilsraabe
 * @version $Id$
 */
public class LimitedAccountActivationController extends ResourceListController implements ErrorAware, ValidationAwareController<LimitedAccountActivationCommand>{

	/**
	 * After successful activation, the user is redirected to this page. 
	 */
	private final String successRedirect = "/register_saml_success";

	/**
	 * like the homepage, only 50 tags are shown in the tag cloud
	 */
	private static final int MAX_TAGS  = 50;
	private Errors errors              = null;
	private static final Log log       = LogFactory.getLog(LimitedAccountActivationController.class);
	
	private LogicInterface adminLogic;
	
	@Override
	public LimitedAccountActivationCommand instantiateCommand() {

		log.debug("UserSamlActivationCommand in UserSamlActivationController initialized");
		
		return new LimitedAccountActivationCommand();
	}

	@Override
	public View workOn(LimitedAccountActivationCommand command) {

		log.debug("UserSamlActivationController starts WorkOn()");
		
		final RequestWrapperContext context = command.getContext();

		/*
		 * only users which are logged in might post -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		User loginUser = AuthenticationUtils.getUser();
		
		if (!command.isSubmitted()) {
			final User u = command.getRegisterUser();
			u.setName(loginUser.getName());
			u.setRealname(loginUser.getRealname());
			u.setHomepage(loginUser.getHomepage());
			u.setEmail(loginUser.getEmail());
			if (VuFindUserInitController.UNKNOWN.equals(u.getEmail())) {
				u.setEmail("");
			}
		}
		
		if (!Role.LIMITED.equals(loginUser.getRole())) {
			errors.reject("limited_account.activation.user_not_limited");
		}
		
		if (!command.isSubmitted() || errors.hasErrors()) {
			/**
			 * Set the tags and the news in the sidebar
			 */
			setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, null, MAX_TAGS, null);
			command.setNews(this.logic.getPosts(Bookmark.class, GroupingEntity.GROUP, "kde", Arrays.asList("bibsonomynews"), null, null, null, null, null, null, 0, 3));
			
			return Views.LIMITED_ACCOUNT_ACTIVATION;
		}
		
		
		final User ru = command.getRegisterUser();
		loginUser.setRole(Role.DEFAULT);
		loginUser.setEmail(ru.getEmail());
		loginUser.setHomepage(ru.getHomepage());
		loginUser.setRealname(ru.getRealname());
		adminLogic.updateUser(loginUser, UserUpdateOperation.UPDATE_ACTIVATION);
		

		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		return new ExtendedRedirectView(successRedirect);

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
	public boolean isValidationRequired(LimitedAccountActivationCommand command) {
		return command.isSubmitted();
	}

	@Override
	public Validator<LimitedAccountActivationCommand> getValidator() {
		return new LimitedAccountActivationValidation();
	}

	/**
	 * @return the adminLogic
	 */
	public LogicInterface getAdminLogic() {
		return this.adminLogic;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
}
