package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Group;
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

		// set the user name 
		g.setName(command.getContext().getLoginUser().getName());

		if (SHARE_DOCUMENTS.equals(command.getAction())) {
			g.setUserSharedDocuments(true);
			this.logic.updateGroup(g, GroupUpdateOperation.UPDATE_USER_SHARED_DOCUMENTS);
		} else if (UNSHARE_DOCUMENTS.equals(command.getAction())) {
			g.setUserSharedDocuments(false);
			this.logic.updateGroup(g, GroupUpdateOperation.UPDATE_USER_SHARED_DOCUMENTS);
		}

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
