package org.bibsonomy.webapp.controller;

import java.util.List;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.EditTagsPageViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;


/**
 * Controller for the edit_tags page (only the view!)
 * 
 * FIXME: Is this class ever used???
 * 
 * @author Henrik Bartholmai
 * @version $Id$
 * 
 */

public class EditTagsPageController extends SingleResourceListControllerWithTags implements MinimalisticController<EditTagsPageViewCommand> {

	private RequestLogic requestLogic;
	
	public View workOn(EditTagsPageViewCommand command) {
		/*
		 * no user given -> error
		 */
		if (!command.getContext().isUserLoggedIn()) {
			return getAccessDeniedView(command, "error.general.login");
		}

		/*
		 * set grouping entity, grouping name, tags
		 */
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		final String groupingName = command.getContext().getLoginUser().getName();

		command.setPageTitle("edit tags :: " + groupingName);

		/*
		 * set the tags of the user to get his tag cloud
		 */
		this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, 20000, null);

		/*
		 * get all concepts of the user 
		 */
		final List<Tag> concepts = this.logic.getConcepts(null, groupingEntity, groupingName, null, null, ConceptStatus.ALL, 0, Integer.MAX_VALUE);
		command.getConcepts().setConceptList(concepts);
		command.getConcepts().setNumConcepts(concepts.size());

		
		/*
		 * return the appropriate view
		 */
		return Views.EDIT_TAGS;
	}

	public EditTagsPageViewCommand instantiateCommand() {
		return new EditTagsPageViewCommand();
	}
	
	/**
	 * redirect to the login page - and back
	 * 
	 * @param command the command
	 * @param notice a notice to display at the login page
	 * @return
	 */
	protected View getAccessDeniedView(final EditTagsPageViewCommand command, String notice) {
		return new ExtendedRedirectView("/login" + 
				"?notice=" + notice + 
				"&referer=" + UrlUtils.safeURIEncode(getRequestLogic().getCompleteRequestURL()));
	}

	/**
	 * @param requestLogic the request logic
	 */
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * @return the request logic
	 */
	public RequestLogic getRequestLogic() {
		return requestLogic;
	}
}