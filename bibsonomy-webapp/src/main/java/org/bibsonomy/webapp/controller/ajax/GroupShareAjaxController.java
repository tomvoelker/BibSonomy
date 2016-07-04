/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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

import java.util.List;
import org.bibsonomy.common.enums.GroupRole;

import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.GroupMembership;
import org.bibsonomy.webapp.command.ajax.GroupShareAjaxCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * This controller handles the share documents and unshare documents action of
 * groups.
 * 
 * @author clemensbaier
 */
public class GroupShareAjaxController extends AjaxController implements MinimalisticController<GroupShareAjaxCommand>, ErrorAware {
	private static final String SHARE_DOCUMENTS = "shareDocuments";

	private static final String UNSHARE_DOCUMENTS = "unshareDocuments";

	private Errors errors;

	@Override
	public GroupShareAjaxCommand instantiateCommand() {
		return new GroupShareAjaxCommand();
	}

	@Override
	public View workOn(final GroupShareAjaxCommand command) {
		final RequestWrapperContext context = command.getContext();

		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException();
		}

		// check if ckey is valid
		if (!command.getContext().isValidCkey()) {
			this.errors.reject("error.field.valid.ckey");
			return this.getErrorView();
		}

		final Group g = getRequestedGroup(command.getContext().getLoginUser().getGroups(), command.getRequestedGroup());
		if (g == null) {
			/*
			 * TODO: custom error message
			 */
			this.errors.reject("error.field.valid.ckey");
			return this.getErrorView();
		}

		final GroupMembership ms = new GroupMembership(command.getContext().getLoginUser(), GroupRole.USER, false);

		 // TODO: Clean up
		if (null != command.getAction())
			ms.setUserSharedDocuments(command.getAction().equals(SHARE_DOCUMENTS));
		
		this.logic.updateGroup(g, GroupUpdateOperation.UPDATE_USER_SHARED_DOCUMENTS, ms);

		// forward to a certain page, if requested
		if (present(command.getForward())) {
			return new ExtendedRedirectView("/" + command.getForward());
		}

		// all done
		return Views.AJAX_JSON;
	}

	private Group getRequestedGroup(List<Group> groups, String requestedGroup) {
		Group res = null;
		for (Group g : groups) {
			if (g.getName().equals(requestedGroup)) {
				return g;
			}
		}
		return res;
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

}
