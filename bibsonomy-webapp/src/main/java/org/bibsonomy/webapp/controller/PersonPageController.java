package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.bibsonomy.model.util.PersonNameUtils;
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
	
	private PersonLogic personLogic = new PersonLogic();
	
	@Override
	public View workOn(final PersonPageCommand command) {
		
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
		
		command.setPost(this.logic.getPostDetails(command.getRequestedHash(), command.getRequestedUser()));
		
		if(present(command.getFormNewPersonSubmit())) {
			Person person = new Person();
			person.setMainName(new PersonName(command.getFormFirstName(), command.getFormLastName()));
			person.setAcademicDegree(command.getFormAcademicDegree());
			this.personLogic.createOrUpdatePerson(person);
			
			return new ExtendedRedirectView("/person/details/" + person.getId() + "/" + PersonNameUtils.serializePersonName(person.getMainName()) + "/" + command.getRequestedHash() + "/" + command.getRequestedUser());
		}

		return Views.PERSON_NEW;
	}


	/**
	 * @param command
	 * @return
	 */
	private View unlinkAction(PersonPageCommand command) {
		if (!present(command.getRequestedHash())) {
			throw new MalformedURLSchemeException("error.person_page_without_personname");
		}
		
		this.personLogic.removePersonRelation(command.getRequestedHash(), command.getRequestedUser(), command.getRequestedPersonId(), PersonResourceRelation.valueOf(command.getRequestedRole()));
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + (present(command.getRequestedHash()) ? "/" + command.getRequestedHash() : "") + (present(command.getRequestedUser()) ? "/" + command.getRequestedUser() : ""));
	}


	/**
	 * @param command
	 * @return
	 */
	private View editRoleAction(PersonPageCommand command) {
		
		//TODO
		this.personLogic.createOrUpdatePerson(command.getPerson());
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + (present(command.getRequestedHash()) ? "/" + command.getRequestedHash() : "") + (present(command.getRequestedUser()) ? "/" + command.getRequestedUser() : ""));
	}


	/**
	 * @param command
	 */
	private View updateAction(PersonPageCommand command) {
		
		command.getPerson().setAcademicDegree(command.getFormAcademicDegree());
		//TODO
		//command.getPerson().setMainName(command.getFormSelectedName());
		
		this.personLogic.createOrUpdatePerson(command.getPerson());
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + (present(command.getRequestedHash()) ? "/" + command.getRequestedHash() : "") + (present(command.getRequestedUser()) ? "/" + command.getRequestedUser() : ""));
	}


	/**
	 * @param requestedPersonId
	 * @param user
	 * @param command
	 * @return 
	 */
	private View addNameAction(PersonPageCommand command) {
		command.getPerson().setAcademicDegree(command.getFormAcademicDegree());
		command.getPerson().getAlternateNames().add(new PersonName(command.getFormFirstName(), command.getFormLastName()));
		this.personLogic.createOrUpdatePerson(command.getPerson());
		
		return new ExtendedRedirectView("/person/show/" + command.getRequestedPersonId() + (present(command.getRequestedHash()) ? "/" + command.getRequestedHash() : "") + (present(command.getRequestedUser()) ? "/" + command.getRequestedUser() : ""));
	}
	
	/**
	 * TODO
	 * remove/edit alternative names
	 * select name fpr logged in person
	 * add person to thesis
	 */


	/**
	 * @param command
	 * @return
	 */
	private View detailsAction(PersonPageCommand command) {

		command.setPost(this.logic.getPostDetails(command.getRequestedHash(), command.getRequestedUser()));
		
		return Views.PERSON_DETAILS;
	}

	/**
	 * @param command
	 * @return
	 */
	private View showAction(PersonPageCommand command) {
		//command.setPerson(this.personLogic.getPersonById(Integer.parseInt(command.getRequestedPersonId())));
		Person p = new Person();
		p.setMainName(new PersonName("Christian", "Pfeiffer"));
		p.setAlternateNames(new HashSet<PersonName>());
		p.getAlternateNames().add(new PersonName("Viktor", "Hemsen"));
		command.setPerson(p);
		
		//TODO set correct parameters
		command.setThesis(this.logic.getPosts(BibTex.class, GroupingEntity.PERSON_GRADUTED, null, null, null, command.getRequestedPersonId(), null, null, null, null, 0, 3));
		command.setAdvisedThesis(this.logic.getPosts(BibTex.class, GroupingEntity.PERSON_ADVISOR, null, null, null, command.getRequestedPersonId(), null, null, null, null, 0, 3));
		command.setAllPosts(this.logic.getPosts(BibTex.class, GroupingEntity.USER, command.getRequestedPersonId(), null, null, null, null, null, null, null, 0, 3));
		
		return Views.PERSON;
	}

	@Override
	public PersonPageCommand instantiateCommand() {
		return new PersonPageCommand();
	}
}


