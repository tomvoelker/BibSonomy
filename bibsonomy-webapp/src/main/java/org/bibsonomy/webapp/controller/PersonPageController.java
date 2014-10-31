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
		
		/**
		 * PreDefinitions (for presentation)
		 */
		
		Person p = new Person();
		p.setMainName(new PersonName("Max", "Musterman"));
		p.setAlternateNames(new HashSet<PersonName>());
		p.getAlternateNames().add(new PersonName("Viktor", "Hemsen"));
		p.getAlternateNames().add(new PersonName("Max", "Musterman"));
		command.setPerson(p);
		
		if(present(command.getFormAction())) {
			switch(command.getFormAction()) {
				case "Save": return this.updateAction(command);
				case "Add name": return this.addNameAction(command);
				case "Change role": return this.editRoleAction(command);
				case "unlink": return this.unlinkAction(command);
				case "New person": return this.newAction(command);
				case "Thats me": return this.assignAction(command);
				default: return this.showAction(command);
			}
		}
		return this.showAction(command);
		
	}


	/**
	 * @param command
	 * @return
	 */
	@SuppressWarnings("boxing")
	private View assignAction(PersonPageCommand command) {
		
//		Person p = this.personLogic.getPersonById(Integer.valueOf(command.getRequestedPersonId()));
//		p.setUser(this.logic.getAuthenticatedUser());
//		this.personLogic.createOrUpdatePerson(p);
		
		return new ExtendedRedirectView("/person/" +command.getRequestedPersonId() + "/" + command.getRequestedPersonName() + "/" + command.getRequestedHash() + "/" + command.getRequestedUser() + "/AUTHOR");
	}


	/**
	 * @param command
	 * @return
	 */
	private View newAction(PersonPageCommand command) {
		
		command.setPost(this.logic.getPostDetails(command.getRequestedHash(), command.getRequestedUser()));
		if(present(command.getFormFirstName()) || present(command.getFormLastName())) {
			Person person = new Person();
			person.setMainName(new PersonName(command.getFormFirstName(), command.getFormLastName()));
			person.setAcademicDegree(command.getFormAcademicDegree());
			this.personLogic.createOrUpdatePerson(person);
			
			return new ExtendedRedirectView("/person/" +command.getRequestedPersonId() + "/" + command.getRequestedPersonName() + "/" + command.getRequestedHash() + "/" + command.getRequestedUser() + "/AUTHOR");
		}
		
		return Views.PERSON_NEW;
	}


	/**
	 * @param command
	 * @return
	 */
	private View unlinkAction(PersonPageCommand command) {
		
		this.personLogic.removePersonRelation(command.getRequestedHash(), command.getRequestedUser(), command.getRequestedPersonId(), PersonResourceRelation.valueOf(command.getRequestedRole()));
		return new ExtendedRedirectView("/person/" + command.getRequestedPersonId() + "/" + command.getRequestedPersonName() + "/" + command.getRequestedHash() + "/" + command.getRequestedUser() + "/AUTHOR");
		
	}


	/**
	 * @param command
	 * @return
	 */
	private View editRoleAction(PersonPageCommand command) {
		
		for(String role : command.getFormRoles()) {
			this.personLogic.addPersonRelation(command.getRequestedHash(), command.getRequestedUser(), command.getRequestedPersonId(), PersonResourceRelation.valueOf(role));
		}
				
		return new ExtendedRedirectView("/person/" + command.getRequestedPersonId() + "/" + command.getRequestedPersonName() + "/" + command.getRequestedHash() + "/" + command.getRequestedUser() + "/AUTHOR");	
	}


	/**
	 * @param command
	 */
	private View updateAction(PersonPageCommand command) {
		
		command.getPerson().setAcademicDegree(command.getFormAcademicDegree());
		PersonName pn = new PersonName();
		pn.setFirstName(command.getFormSelectedName().split(",")[1].trim());
		pn.setLastName(command.getFormSelectedName().split(",")[0].trim());
		command.getPerson().setMainName(pn);
		this.personLogic.createOrUpdatePerson(command.getPerson());
		
		
		return new ExtendedRedirectView("/person/" + command.getRequestedPersonId() + "/" + command.getRequestedPersonName() + "/" + command.getRequestedHash() + "/" + command.getRequestedUser() + "/AUTHOR");
	}

	/**
	 * 
	 * @param command
	 */
	private View addNameAction(PersonPageCommand command) {
		
		command.getPerson().getAlternateNames().add(new PersonName(command.getFormFirstName(), command.getFormLastName()));
		this.personLogic.createOrUpdatePerson(command.getPerson());
		
		return new ExtendedRedirectView("/person/" +command.getRequestedPersonId() + "/" + command.getRequestedPersonName() + "/" + command.getRequestedHash() + "/" + command.getRequestedUser() + "/AUTHOR");

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
	private View showAction(PersonPageCommand command) {
		//command.setPerson(this.personLogic.getPersonById(Integer.parseInt(command.getRequestedPersonId())));
		Person p = new Person();
		p.setMainName(new PersonName("Max", "Musterman"));
		p.setAlternateNames(new HashSet<PersonName>());
		p.getAlternateNames().add(new PersonName("Viktor", "Hemsen"));
		p.getAlternateNames().add(new PersonName("Max", "Musterman"));
		command.setPerson(p);
		
		command.setThesis(this.logic.getPosts(BibTex.class, GroupingEntity.PERSON_GRADUTED, null, null, null, command.getRequestedPersonId(), null, null, null, null, 0, 3));
		command.setAdvisedThesis(this.logic.getPosts(BibTex.class, GroupingEntity.PERSON_ADVISOR, null, null, null, command.getRequestedPersonId(), null, null, null, null, 0, 3));
		command.setAllPosts(this.logic.getPosts(BibTex.class, GroupingEntity.USER, command.getRequestedUser(), null, null, null, null, null, null, null, 0, 3));
		
		return Views.PERSON;
	}

	@Override
	public PersonPageCommand instantiateCommand() {
		return new PersonPageCommand();
	}
}


