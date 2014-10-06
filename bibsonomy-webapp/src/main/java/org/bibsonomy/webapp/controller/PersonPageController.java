package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.config.Parameters;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.PersonLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageController extends SingleResourceListController implements MinimalisticController<PersonPageCommand> {
	private static final Log log = LogFactory.getLog(PersonPageController.class);
	
	//TODO: get by injection
	private PersonLogic personLogic = new PersonLogic();
	
	@Override
	public View workOn(final PersonPageCommand command) {
		command.setPerson(new Person());
		command.getPerson().setId(12334);
		Set<PersonName> set = new HashSet<PersonName>();
		set.add(new PersonName("Christian", "Pfeiffer"));
		set.add(new PersonName("Viktor", "Hemsen"));
		command.getPerson().setAlternateNames(set);
		command.getPerson().setMainName(new PersonName(command.getRequestedPersonId(), ""));
		command.getPerson().setAcademicDegree("Doctor");
		
		switch(command.getRequestedAction()) {
			case "update": return this.updateAction(command);
			case "addName": return this.addNameAction(command);
			case "details": return this.detailsAction(command);
			case "editRole": return this.editRoleAction(command);
			case "unlink": return this.unlinkAction(command);
			case "new": return this.newAction(command);
			case "show": return this.showAction(command);
			default: throw new MalformedURLSchemeException("Controller " + this.getClass().toString() + " cant handle action " + command.getRequestedAction());
		}
	}


	/**
	 * @param command
	 * @return
	 */
	private View newAction(PersonPageCommand command) {
		
		return Views.PERSON_ADD;
	}


	/**
	 * @param command
	 * @return
	 */
	private View unlinkAction(PersonPageCommand command) {
		if (!present(command.getRequestedHash())) {
			throw new MalformedURLSchemeException("error.person_page_without_personname");
		}
		
		//TODO: where is the person<->publication-relation stored?
		
		log.info("Accessed unlinkAction -> getting redirected");
		
		this.personLogic.updatePerson(command.getPerson());
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + (present(command.getRequestedHash()) ? "/" + command.getRequestedHash() : "") + (present(command.getRequestedUser()) ? "/" + command.getRequestedUser() : ""));
	}


	/**
	 * @param command
	 * @return
	 */
	private View editRoleAction(PersonPageCommand command) {
		
		//TODO where is role-relation stored??
		
		log.info("Accessed editRoleAction -> getting redirected");
		
		this.personLogic.updatePerson(command.getPerson());
		command.getPerson().getAlternateNames().add(new PersonName(command.getFormGivenName(), command.getFormSurName()));
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + (present(command.getRequestedHash()) ? "/" + command.getRequestedHash() : "") + (present(command.getRequestedUser()) ? "/" + command.getRequestedUser() : ""));
	}


	/**
	 * @param command
	 */
	private View updateAction(PersonPageCommand command) {
		
		command.getPerson().setAcademicDegree(command.getFormGraduation());
		command.getPerson().setMainName(command.getFormSelectedName());
		
		this.personLogic.updatePerson(command.getPerson());
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + (present(command.getRequestedHash()) ? "/" + command.getRequestedHash() : "") + (present(command.getRequestedUser()) ? "/" + command.getRequestedUser() : ""));
	}


	/**
	 * @param requestedPersonId
	 * @param user
	 * @param command
	 * @return 
	 */
	private View addNameAction(PersonPageCommand command) {
		
		command.getPerson().getAlternateNames().add(new PersonName(command.getFormGivenName(), command.getFormSurName()));
		
		this.personLogic.updatePerson(command.getPerson());
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + (present(command.getRequestedHash()) ? "/" + command.getRequestedHash() : "") + (present(command.getRequestedUser()) ? "/" + command.getRequestedUser() : ""));
	}


	/**
	 * @param command
	 * @return
	 */
	private View detailsAction(PersonPageCommand command) {

		command.setPost(this.logic.getPostDetails(command.getRequestedHash(), command.getRequestedUser()));
		
		return this.showAction(command);
	}

	/**
	 * @param command
	 * @return
	 */
	private View showAction(PersonPageCommand command) {
		
		// retrieve and set the requested resource lists, along with total
		// counts
		Class<? extends Resource> toRemove = null;
		for (final Class<? extends Resource> resourceType : this.getListsToInitialize(command.getFormat(), command.getResourcetype())) {
			if(resourceType.getName().equals("org.bibsonomy.model.Bookmark")) {
				toRemove = resourceType;
				continue;
			}
			command.getResourcetype().remove(toRemove);
			final ListCommand<?> listCommand = command.getListCommand(resourceType);
			final int entriesPerPage = listCommand.getEntriesPerPage();
			
			this.setList(command, resourceType, GroupingEntity.USER, command.getRequestedPersonId(), command.getRequestedTagsList(), null, null, command.getFilter(), null, command.getStartDate(), command.getEndDate(), entriesPerPage);
			this.postProcessAndSortList(command, resourceType);
		}
		
		return Views.PERSON;
	}

	@Override
	public PersonPageCommand instantiateCommand() {
		return new PersonPageCommand();
	}
}


