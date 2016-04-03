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
package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.enums.GoldStandardRelation;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.webapp.command.ajax.EditGoldstandardRelationCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * Controller for editing references of a gold standard post
 *    - ajax/goldstandards/relation
 * 
 * @author lka
 */
public class EditGoldstandardRelationController extends AjaxController implements MinimalisticController<EditGoldstandardRelationCommand>, ErrorAware {
	
	@Override
	public EditGoldstandardRelationCommand instantiateCommand() {
		return new EditGoldstandardRelationCommand();
	}

	private Errors errors;
	
	@Override
	public View workOn(final EditGoldstandardRelationCommand command) {

		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(context.getLoginUser().getRole())) {
			throw new AccessDeniedException("You are not allowed to edit references of a goldstandard");
		}

		//check if ckey is valid
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.ERROR;
		}
		
		final String hash = command.getHash();
		final Set<String> references = command.getReferences();
		final GoldStandardRelation relation = command.getRelation();
		
		if (!present(hash) || !present(references) || !present(relation)) {
			this.responseLogic.setHttpStatus(HttpServletResponse.SC_BAD_REQUEST);
			return Views.AJAX_TEXT;
		}
		
		final HttpMethod httpMethod = this.requestLogic.getHttpMethod();
		
		switch (httpMethod) {
		case POST: 
		
			this.logic.createRelations(hash, references, relation);
			break;
		case DELETE: 
			this.logic.deleteRelations(hash, references, relation);
			break;
		default: 
			this.responseLogic.setHttpStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		
		return Views.AJAX_TEXT;
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

}
