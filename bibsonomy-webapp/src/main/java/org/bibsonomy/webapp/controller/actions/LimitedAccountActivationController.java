/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.UrlBuilder;
import org.bibsonomy.webapp.command.actions.LimitedAccountActivationCommand;
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
 */
public class LimitedAccountActivationController implements ErrorAware, ValidationAwareController<LimitedAccountActivationCommand>{
	
	/** after successful activation, the user is redirected to this page. */
	private String successRedirect;
	
	private URLGenerator urlGenerator;

	private Errors errors= null;
	
	private LogicInterface adminLogic;
	
	@Override
	public LimitedAccountActivationCommand instantiateCommand() {
		return new LimitedAccountActivationCommand();
	}

	@Override
	public View workOn(LimitedAccountActivationCommand command) {
		final RequestWrapperContext context = command.getContext();
		
		/*
		 * only users which are logged in might post -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException("please log in");
		}
		
		final User loginUser = command.getContext().getLoginUser();
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
			return Views.LIMITED_ACCOUNT_ACTIVATION;
		}
		
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		final User ru = command.getRegisterUser();
		loginUser.setRole(Role.DEFAULT);
		loginUser.setEmail(ru.getEmail());
		loginUser.setHomepage(ru.getHomepage());
		loginUser.setRealname(ru.getRealname());
		this.adminLogic.updateUser(loginUser, UserUpdateOperation.UPDATE_LIMITED_USER);
		
		/*
		 * redirect on success
		 * add the hash of the application and username as "hash" parameter
		 * to the redirect
		 * TODO: document why we add the hash here!
		 */
		final String hash = StringUtils.getMD5Hash(this.urlGenerator.getProjectHome() + "register_saml_success+" + loginUser.getName());
		final String redirectUrl = new UrlBuilder(this.successRedirect).addParameter("hash", hash).asString();
		return new ExtendedRedirectView(redirectUrl);
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
	 * @param successRedirect the successRedirect to set
	 */
	public void setSuccessRedirect(String successRedirect) {
		this.successRedirect = successRedirect;
	}

	/**
	 * 
	 * @param urlGenerator
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
}
