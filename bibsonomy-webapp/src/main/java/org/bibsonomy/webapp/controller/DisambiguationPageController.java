package org.bibsonomy.webapp.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.bibsonomy.model.util.PersonNameUtils;
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
	private static final Log log = LogFactory.getLog(DisambiguationPageController.class);
	
	@Override
	public DisambiguationPageCommand instantiateCommand() {
		return new DisambiguationPageCommand();
	}
	
	@Override
	public View workOn(final DisambiguationPageCommand command) {

		switch(command.getRequestedAction()) {
			case "redirected" : return this.redirectAction(command);
			default: return this.indexAction(command);
		}
	}


	/**
	 * @param command
	 * @return
	 */
	private View indexAction(DisambiguationPageCommand command) {

		command.setPost(this.logic.getPostDetails(command.getRequestedHash(), command.getRequestedUser()));
		// FIXME: use personnameutils
		PersonName pn = new PersonName(command.getRequestedAuthorName().split(", ")[0].trim()).withFirstName(command.getRequestedAuthorName().split(", ")[1].trim());
		command.setPersonName(pn);
		command.setSuggestedPersonNames(this.logic.getPersonSuggestion(pn.getLastName(), pn.getFirstName()));
		
		return Views.DISAMBIGUATION;
	}
	
	private View redirectAction(DisambiguationPageCommand command) {
		command.setPost(this.logic.getPostDetails(command.getRequestedHash(), command.getRequestedUser()));
		PersonName pn = new PersonName(command.getRequestedAuthorName().split(", ")[0]).withFirstName(command.getRequestedAuthorName().split(", ")[1]);
		List<ResourcePersonRelation> matchingPersons = this.logic.getResourceRelations(pn, command.getPost().getResource().getInterHash(),command.getPost().getResource().getIntraHash(), command.getRequestedUser(), PersonResourceRelation.AUTHOR);
		
		if(matchingPersons.size() > 0 ) {
			log.warn("Too many persons for " + command.getRequestedHash());
			return new ExtendedRedirectView(new URLGenerator().getPersonUrl(matchingPersons.get(0).getPersonName().getPersonId(), PersonNameUtils.serializePersonName(matchingPersons.get(0).getPersonName().getPerson().getMainName()), command.getRequestedHash(), command.getRequestedUser(), command.getRequestedRole()));	
		}

		return new ExtendedRedirectView(new URLGenerator().getDisambiguationUrl("details", command.getRequestedAuthorName(), command.getRequestedHash(), command.getRequestedUser(), command.getRequestedRole()));

	}
}


