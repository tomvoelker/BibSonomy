package org.bibsonomy.webapp.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.RecognitionException;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.ConceptUpdateOperation;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.EditTagsPageViewCommand;
import org.bibsonomy.webapp.command.actions.EditTagsCommand;
import org.bibsonomy.webapp.command.actions.RelationsEditCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;


/**
 * Controller for the edit_tags page 
 * 
 * @author Henrik Bartholmai
 * @version $Id$
 * 
 */
public class EditTagsPageViewController extends SingleResourceListControllerWithTags implements MinimalisticController<EditTagsPageViewCommand> {

	private RequestLogic requestLogic;

	@Override
	public View workOn(EditTagsPageViewCommand command) {
		/*
		 * no user given -> error
		 */
		if (!command.getContext().isUserLoggedIn()) {
			return getAccessDeniedView(command, "error.general.login");
		}

		switch (command.getForcedAction()) {

		case 1:
			return workOnEditTagsHandler(command);
			
		case 2:
			return workOnRelationsHandler(command);
		}
		
		/*
		 * set grouping entity, grouping name, tags
		 */
		final GroupingEntity groupingEntity = GroupingEntity.USER;
		final User user = command.getContext().getLoginUser();
		final String groupingName = user.getName();

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
		
		return Views.EDIT_TAGS;
		
		
	}
	
	private View workOnEditTagsHandler(EditTagsPageViewCommand cmd) {
		User user = cmd.getContext().getLoginUser();
		EditTagsCommand command = cmd.getEditTags();
		int updatedTags = 0;
		
		try {
			final Set<Tag> tagsToReplace = TagUtils.parse(command.getDelTags());

			if(tagsToReplace.size() <= 0) {
				return Views.EDIT_TAGS;
			}
			
			final Set<Tag> replacementTags = TagUtils.parse(command.getAddTags());

			//remove possible relations!
			Iterator<Tag> it = tagsToReplace.iterator();
			while(it.hasNext()) {
				final Tag t = it.next();
				
				if(t.getSuperTags().size() != 0) {
					it.remove();
					continue;
				}
				
				if(t.getSubTags().size() != 0)
					t.getSubTags().clear();
			}
			
			it = replacementTags.iterator();
			while(it.hasNext()) {
				final Tag t = it.next();
				
				if(t.getSuperTags().size() != 0) {
					it.remove();
					continue;
				}
				
				if(t.getSubTags().size() != 0)
					t.getSubTags().clear();
			}
			
			if(!command.isUpdateRelations()) {
				
				updatedTags = logic.updateTags(user, new LinkedList<Tag>(tagsToReplace), new LinkedList<Tag>(replacementTags), false);
			} else {
				if(tagsToReplace.size() != 1 || replacementTags.size() != 1) 
					throw new MalformedURLSchemeException("edittags.main.note");
				
				updatedTags = logic.updateTags(user, new LinkedList<Tag>(tagsToReplace), new LinkedList<Tag>(replacementTags), true);
			}
			
		} catch (RecognitionException ex) {
			// TODO How can i handle this
		}
		
		if(command.isUpdateRelations()) 
			return new ExtendedRedirectView("/edit_tags?updatedRelationsCount=" +updatedTags);

		return new ExtendedRedirectView("/edit_tags?updatedTagsCount=" +updatedTags);
	}
	
	private View workOnRelationsHandler(EditTagsPageViewCommand cmd) {
		User user = cmd.getContext().getLoginUser();
		RelationsEditCommand command = cmd.getRelationsEdit();
		
		switch (command.getForcedAction()) {
		case 0:
			try {
	
			Set<Tag> upperList = TagUtils.parse(command.getUpper());
			final Set<Tag> lowerList = TagUtils.parse(command.getLower());
			
			if(upperList.size() != 1 || lowerList.size() != 1)
				break;
			
			Tag upper = upperList.iterator().next();
			Tag lower = lowerList.iterator().next();
			
			if(upper.getSubTags().size() != 0 || upper.getSuperTags().size() != 0 ||
					lower.getSubTags().size() != 0 || lower.getSuperTags().size() != 0)
				break;
			
			upper.setSubTags(new LinkedList<Tag>(lowerList));
			
			logic.updateConcept(upper, GroupingEntity.USER, user.getName(),ConceptUpdateOperation.UPDATE);
			break;
			

			} catch (RecognitionException ex) {
				// TODO how should i handle this
				break;
			}

		case 1:
			logic.deleteRelation(command.getUpper(), command.getLower(), GroupingEntity.USER, user.getName());
			break;
			
		}
		
		/*
		 * return the appropriate view
		 */
		return new ExtendedRedirectView("/edit_tags");
	}

	@Override
	public EditTagsPageViewCommand instantiateCommand() {
		return new EditTagsPageViewCommand();
	}
	
	/**
	 * redirect to the login page - and back
	 * 
	 * @param command the command FIXME: unused
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