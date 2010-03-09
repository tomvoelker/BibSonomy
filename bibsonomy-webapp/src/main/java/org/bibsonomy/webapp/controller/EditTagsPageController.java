package org.bibsonomy.webapp.controller;

import java.util.List;

import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.EditTagsPageViewCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;


/**
 * Controller for the edit_tags page (only the view!)
 * 
 * @author Henrik Bartholmai
 * @version $Id$
 * 
 */

public class EditTagsPageController extends SingleResourceListControllerWithTags implements MinimalisticController<EditTagsPageViewCommand> {

	public View workOn(EditTagsPageViewCommand command) {
		/*
		 * no user given -> error
		 */
		if (!command.getContext().isUserLoggedIn()) {
			throw new MalformedURLSchemeException("error.user_page_without_username");
		}

		/*
		 * set grouping entity, grouping name, tags
		 */
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		final String groupingName = command.getContext().getLoginUser().getName();

		command.setPageTitle("edit tags :: " + groupingName);
		command.setUserName(groupingName);

		/*
		 * set the tags of the user to get his tag cloud
		 */
		this.setTags(command, Resource.class, groupingEntity, groupingName, null, null, null, null, 0, 20000, null);

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
}