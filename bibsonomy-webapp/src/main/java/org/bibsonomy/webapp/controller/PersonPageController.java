package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashSet;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.PersonLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageController extends SingleResourceListController implements MinimalisticController<PersonPageCommand> {
	private PersonLogic personLogic;
	private DBSessionFactory dbSessionFactory;
	
	@Override
	public View workOn(final PersonPageCommand command) {
		this.personLogic = new PersonLogic(null, this.dbSessionFactory);
		
		if(present(command.getFormAction())) {
			switch(command.getFormAction()) {
				case "update": return this.updateAction(command);
				case "addName": return this.addNameAction(command);
				case "deleteName": return this.deleteNameAction(command);
				case "addRole": return this.addRoleAction(command);
				case "editRole": return this.editRoleAction(command);
				case "deleteRole": return this.deleteRoleAction(command);
				case "unlink": return this.unlinkAction(command);
				case "new": return this.newAction(command);
				case "link": return this.assignAction(command);
				case "search": return this.indexAction(command);
				default: return this.indexAction(command);
			}
		} else if(present(command.getRequestedPersonId())) {
			return this.showAction(command);
		}
		return this.indexAction(command);
		
	}

	/**
	 * @param command
	 * @return
	 */
	private View assignAction(PersonPageCommand command) {
		
		command.setPerson(this.personLogic.getPersonById(Integer.parseInt(command.getRequestedPersonId())));
		command.getPerson().setUser(this.logic.getAuthenticatedUser().getName());
		this.personLogic.createOrUpdatePerson(command.getPerson());
		
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getId().intValue(), command.getPerson().getMainName().toString(), command.getPost().getResource().getInterHash(), command.getPost().getUser().getName(), command.getRequestedRole()));
	}
	
	@SuppressWarnings({ "static-method", "unused" })
	private View indexAction(PersonPageCommand command) {	
		return Views.PERSON;
	}


	/**
	 * Action which is called a new person button somewhere in an modal dialog
	 * @param command
	 * @return
	 */
	private View newAction(PersonPageCommand command) {
		
		command.setPost(this.logic.getPostDetails(command.getFormResourceHash(), command.getFormUser()));
		if(present(command.getFormLastName())) {
			Person person = new Person();
			person.setMainName(new PersonName(command.getFormFirstName(), command.getFormLastName()));
			person.setAcademicDegree(command.getFormAcademicDegree());
			person.setUser(command.getFormUser());
			System.out.println("halo");
			
			System.out.println("halo");
			System.out.println("halo");
			System.out.println("halo");
			
			this.personLogic.createOrUpdatePerson(person);		
			this.personLogic.addPersonRelation(
					command.getFormResourceHash(), 
					command.getFormUser(), 
					person.getId(), 
					PersonResourceRelation.AUTHOR);
			return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getId().intValue(), command.getPerson().getMainName().toString(), command.getPost().getResource().getInterHash(), command.getPost().getUser().getName(), command.getRequestedRole()));
		}
		
		return Views.PERSON_NEW;
	}


	/**
	 * Action called when a user want to unlink an author from a publication
	 * @param command
	 * @return
	 */
	private View unlinkAction(PersonPageCommand command) {
		
		this.personLogic.removePersonRelation(command.getRequestedHash(), command.getRequestedUser(), Integer.parseInt(command.getRequestedPersonId()), PersonResourceRelation.valueOf(command.getRequestedRole()));
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getId().intValue(), command.getPerson().getMainName().toString(), command.getPost().getResource().getInterHash(), command.getPost().getUser().getName(), command.getRequestedRole()));
		
	}
	
	/**
	 * Action called when a user wants to add a person role to a thesis
	 * @param command
	 * @return
	 */
	private View addRoleAction(PersonPageCommand command) {
		
		this.personLogic.addPersonRelation(command.getRequestedHash(), command.getRequestedUser(), Integer.parseInt(command.getRequestedPersonId()), PersonResourceRelation.valueOf(command.getRequestedRole()));
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getId().intValue(), command.getPerson().getMainName().toString(), command.getPost().getResource().getInterHash(), command.getPost().getUser().getName(), command.getRequestedRole()));	
	}

	/**
	 * Action called when a user wants to edit the role of a person in a thesis
	 * @param command
	 * @return
	 */
	private View editRoleAction(PersonPageCommand command) {
		//TODO add new role types to view
		for(String role : command.getFormPersonRoles()) {
			this.personLogic.addPersonRelation(command.getRequestedHash(), command.getRequestedUser(), Integer.parseInt(command.getRequestedPersonId()), PersonResourceRelation.valueOf(role));
		}
				
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getId().intValue(), command.getPerson().getMainName().toString(), command.getPost().getResource().getInterHash(), command.getPost().getUser().getName(), command.getRequestedRole()));	
	}
	
	private View deleteRoleAction(PersonPageCommand command) {
		
		this.personLogic.removePersonRelation(command.getFormResourceHash(), command.getFormUser(), Integer.parseInt(command.getRequestedPersonId()), PersonResourceRelation.valueOf(command.getFormPersonRole().toUpperCase()));
				
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getId().intValue(), command.getPerson().getMainName().toString(), command.getPost().getResource().getInterHash(), command.getPost().getUser().getName(), command.getRequestedRole()));	
	}

	/**
	 * Action called when a user updates preferences of a person
	 * @param command
	 */
	private View updateAction(PersonPageCommand command) {
		
		command.getPerson().setAcademicDegree(command.getFormAcademicDegree());
		PersonName pn = new PersonName();
		pn.setFirstName(command.getFormSelectedName().split(",")[1].trim());
		pn.setLastName(command.getFormSelectedName().split(",")[0].trim());
		command.getPerson().setMainName(pn);
		this.personLogic.createOrUpdatePerson(command.getPerson());
		
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getId().intValue(), command.getPerson().getMainName().toString(), command.getPost().getResource().getInterHash(), command.getPost().getUser().getName(), command.getRequestedRole()));
	}

	/**
	 * Action called when a user adds an alternative name to a person
	 * @param command
	 */
	private View addNameAction(PersonPageCommand command) {
		command.getPerson().getAlternateNames().add(new PersonName(command.getFormFirstName(), command.getFormLastName()));
		this.personLogic.createOrUpdatePerson(command.getPerson());
		
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getId().intValue(), command.getPerson().getMainName().toString(), command.getPost().getResource().getInterHash(), command.getPost().getUser().getName(), command.getRequestedRole()));
	}

	/**
	 * Action called when a user removes an alternative name from a person
	 * @param command
	 * @return
	 */
	private View deleteNameAction(PersonPageCommand command) {
		
		for(PersonName name : command.getPerson().getAlternateNames()) {
			if(name.getFirstName().equals(command.getFormFirstName()) && name.getLastName().equals(command.getFormLastName())) {
				command.getPerson().getAlternateNames().remove(name);
				break;
			}
		}
		this.personLogic.createOrUpdatePerson(command.getPerson());
		
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getId().intValue(), command.getPerson().getMainName().toString(), command.getPost().getResource().getInterHash(), command.getPost().getUser().getName(), command.getRequestedRole()));
	}
	
	/**
	 * Default action called when a user url is called
	 * @param command
	 * @return
	 */
	private View showAction(PersonPageCommand command) {
		//TODO command.setAuthorOf
		command.setThesis(this.logic.getPosts(BibTex.class, GroupingEntity.PERSON_GRADUTED, null, null, null, command.getRequestedPersonId(), null, null, null, null, 0, 3));
		command.setAdvisedThesis(this.logic.getPosts(BibTex.class, GroupingEntity.PERSON_ADVISOR, null, null, null, command.getRequestedPersonId(), null, null, null, null, 0, 3));
		command.setAllPosts(this.logic.getPosts(BibTex.class, GroupingEntity.USER, command.getRequestedPersonId(), null, null, null, null, null, null, null, 0, 3));
		
		return Views.PERSON_SHOW;
	}

	@Override
	public PersonPageCommand instantiateCommand() {
		return new PersonPageCommand();
	}

	/**
	 * @return the personLogic
	 */
	public PersonLogic getPersonLogic() {
		return this.personLogic;
	}

	/**
	 * @param personLogic the personLogic to set
	 */
	public void setPersonLogic(PersonLogic personLogic) {
		this.personLogic = personLogic;
	}

	/**
	 * @return the dbSessionFactory
	 */
	public DBSessionFactory getDbSessionFactory() {
		return this.dbSessionFactory;
	}

	/**
	 * @param dbSessionFactory the dbSessionFactory to set
	 */
	public void setDbSessionFactory(DBSessionFactory dbSessionFactory) {
		this.dbSessionFactory = dbSessionFactory;
	}
}


