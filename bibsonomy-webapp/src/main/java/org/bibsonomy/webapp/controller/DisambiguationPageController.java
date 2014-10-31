package org.bibsonomy.webapp.controller;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.webapp.command.DisambiguationPageCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.PersonLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Pfeiffer
 */
public class DisambiguationPageController extends SingleResourceListController implements MinimalisticController<DisambiguationPageCommand> {
	private static final Log log = LogFactory.getLog(DisambiguationPageController.class);
	
	private PersonLogic personLogic = new PersonLogic();
	
	@Override
	public View workOn(final DisambiguationPageCommand command) {
		
		switch(command.getRequestedAction()) {
			case "redirected" : return this.redirectAction(command);
			case "link" : return this.linkAction(command);
			case "details" : return this.detailsAction(command);
			default: throw new MalformedURLSchemeException("Controller " + this.getClass().toString() + " cant handle action " + command.getRequestedAction());
		}
	}


	/**
	 * @param command
	 * @return
	 */
	private View detailsAction(DisambiguationPageCommand command) {

		command.setPost(this.logic.getPostDetails(command.getRequestedHash(), command.getRequestedUser()));
		PersonName pn = new PersonName(command.getRequestedAuthorName().split(",")[1], command.getRequestedAuthorName().split(",")[0]);
		command.setSuggestedPersons(this.logic.getPersons(null, null, pn, PersonResourceRelation.valueOf(command.getRequestedRole())));
		
		return Views.DISAMBIGUATION;
	}

	@Override
	public DisambiguationPageCommand instantiateCommand() {
		return new DisambiguationPageCommand();
	}
	
	private View redirectAction(DisambiguationPageCommand command) {

		PersonName pn = new PersonName(command.getRequestedAuthorName().split(",")[1],command.getRequestedAuthorName().split(",")[0]);
		List<Person> matchingPersons = this.personLogic.getPersons(command.getRequestedHash(), command.getRequestedUser(), pn, PersonResourceRelation.valueOf(command.getRequestedRole()));
		
		if(matchingPersons.size() > 0 ) {
			log.warn("Too many persons for " + command.getRequestedHash());
			return new ExtendedRedirectView("/person/" + matchingPersons.get(0).getId()  + "/" + PersonNameUtils.serializePersonName(matchingPersons.get(0).getMainName()) + "/" + command.getRequestedHash() + "/" + command.getRequestedUser() + "/" + command.getRequestedRole());	
		}
		
		return new ExtendedRedirectView("/persondisambiguation/details/" + command.getRequestedAuthorName() + "/" + command.getRequestedHash() + "/" + command.getRequestedUser() + "/" + command.getRequestedRole());

	}
	
	private View linkAction(DisambiguationPageCommand command) {
		
		this.personLogic.addPersonRelation(command.getRequestedHash(), command.getRequestedUser(), command.getFormAddPersonId(), PersonResourceRelation.valueOf(command.getRequestedRole()));	
		
		return new ExtendedRedirectView("/person/"+command.getFormAddPersonId()+"/"+ command.getRequestedAuthorName() + "/" + command.getRequestedHash() + "/" + command.getRequestedUser() + "/" + command.getRequestedRole());
	}
}


