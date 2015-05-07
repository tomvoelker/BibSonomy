package org.bibsonomy.webapp.controller;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.DisambiguationPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Pfeiffer
 */
public class DisambiguationPageController extends SingleResourceListController implements MinimalisticController<DisambiguationPageCommand> {
	//private static final Log log = LogFactory.getLog(DisambiguationPageController.class);
	
	@Override
	public DisambiguationPageCommand instantiateCommand() {
		return new DisambiguationPageCommand();
	}
	
	@Override
	public View workOn(final DisambiguationPageCommand command) {
		command.setPost(this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, command.getRequestedHash(), null, null, null, null, null, null, 0, 100).get(0));
		
		final List<ResourcePersonRelation> matchingPersons = this.logic.getResourceRelations(command.getPost().getResource().getInterHash(), PersonResourceRelationType.AUTHOR, new Integer(command.getRequestedIndex()));		
		if (matchingPersons.size() > 0 ) {
			return new ExtendedRedirectView(new URLGenerator().getPersonUrl(matchingPersons.get(0).getPersonId()));	
		}

		final PersonName requestedName = command.getPost().getResource().getAuthor().get(command.getRequestedIndex());
		command.setPersonName(requestedName);
		command.setSuggestedPersonNames(this.logic.getPersonSuggestion(requestedName.getLastName(), requestedName.getFirstName()));
		
		return Views.DISAMBIGUATION;
	}
}


