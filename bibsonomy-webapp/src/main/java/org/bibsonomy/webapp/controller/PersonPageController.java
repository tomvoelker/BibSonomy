package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSessionFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.PersonLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.eclipse.jetty.util.ajax.JSON;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 * @author Christian Pfeiffer
 */
public class PersonPageController extends SingleResourceListController implements MinimalisticController<PersonPageCommand> {
	
	@Override
	public View workOn(final PersonPageCommand command) {		
		
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
				case "link": return this.linkAction(command);
				case "search": return this.searchAction(command);
				case "searchpub": return this.searchpubAction(command);
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
	private View searchpubAction(PersonPageCommand command) { 
		JSONArray array = new JSONArray();
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}

	/**
	 * @param command
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private View searchAction(PersonPageCommand command) {
		List<PersonName> personNames = this.logic.getPersonSuggestion(command.getFormSelectedName(), command.getFormSelectedName());
		JSONArray array = new JSONArray();
		for(PersonName personName : personNames) {
			JSONObject jsonPersonName = new JSONObject();
			jsonPersonName.put("personId", new Integer(personName.getPersonId()));
			jsonPersonName.put("personNameId", new Integer(personName.getId()));
			jsonPersonName.put("personName", personName.toString());
			
			array.add(jsonPersonName);
		}
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}
	
	@SuppressWarnings("static-method")
	private View indexAction(@SuppressWarnings("unused") PersonPageCommand command) {
		return Views.PERSON;
	}


	/**
	 * Action which is called a new person button somewhere in an modal dialog
	 * @param command
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private View newAction(PersonPageCommand command) {
		
		Person person = new Person().withMainName(new PersonName(command.getFormLastName()).withFirstName(command.getFormFirstName()).withMain(true)).withAcademicDegree(command.getFormAcademicDegree());
		this.logic.createOrUpdatePerson(person);
		command.setPerson(person);
		String role = command.getFormPersonRole();
		if(role.length() != 4) {
			role = PersonResourceRelation.valueOf(command.getFormPersonRole()).getRelatorCode();
		}
		ResourcePersonRelation rpr = new ResourcePersonRelation()
		.withSimhash1(command.getFormInterHash())
		.withSimhash2(command.getFormIntraHash())
		.withRelatorCode(role)
		.withPersonNameId(person.getMainName().getId())
		.withPubOwner(command.getFormUser());
		this.logic.addResourceRelation(rpr);
		
		JSONObject jsonPerson = new JSONObject();
		jsonPerson.put("personId", new Integer(person.getId()));
		jsonPerson.put("personName", person.getMainName().toString());
		jsonPerson.put("personNameId", new Integer(person.getMainName().getId()));
		jsonPerson.put("rprid", new Integer(rpr.getId()));
		
		command.setResponseString(jsonPerson.toJSONString());
		
		return Views.AJAX_JSON;
	}


	/**
	 * Action called when a user want to unlink an author from a publication
	 * @param command
	 * @return
	 */
	private View unlinkAction(PersonPageCommand command) {
		this.logic.unlinkUser(this.logic.getAuthenticatedUser().getName());
		return Views.AJAX_TEXT;
	}
	
	private View linkAction(PersonPageCommand command) {
		this.logic.linkUser(new Integer(command.getFormPersonId()));
		return Views.AJAX_TEXT;
	}
	
	/**
	 * Action called when a user wants to add a person role to a thesis
	 * @param command
	 * @return
	 */
	private View addRoleAction(PersonPageCommand command) {
		String role;
		if(command.getFormPersonRole().length() == 4)
			role = command.getFormPersonRole();
		else
			role = PersonResourceRelation.valueOf(command.getFormPersonRole()).getRelatorCode();
		
		ResourcePersonRelation rpr = new ResourcePersonRelation()
			.withSimhash1(command.getFormInterHash())
			.withSimhash2(command.getFormIntraHash())
			.withRelatorCode(role)
			.withPersonNameId(Integer.valueOf(command.getFormPersonNameId()).intValue())
			.withPubOwner(command.getFormUser());
		this.logic.addResourceRelation(rpr);
		command.setResponseString(rpr.getId() + "");
		return Views.AJAX_TEXT;
	}

	/**
	 * Action called when a user wants to edit the role of a person in a thesis
	 * @param command
	 * @return
	 */
	private View editRoleAction(PersonPageCommand command) {
		//TODO add new role types to view
		for(String role : command.getFormPersonRoles()) {
			ResourcePersonRelation rpr = new ResourcePersonRelation()
			.withSimhash1(command.getFormInterHash())
			.withSimhash2(command.getFormIntraHash())
			.withRelatorCode(PersonResourceRelation.valueOf(role).getRelatorCode())
			.withPersonNameId(Integer.valueOf(command.getFormPersonNameId()).intValue())
			.withPubOwner(command.getRequestedUser());
			this.logic.addResourceRelation(rpr);
		}
				
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getId(), command.getPerson().getMainName().toString(), command.getPost().getResource().getInterHash(), command.getPost().getUser().getName(), command.getRequestedRole()));	
	}
	
	@SuppressWarnings("boxing")
	private View deleteRoleAction(PersonPageCommand command) {

		this.logic.removeResourceRelation(Integer.valueOf(command.getFormRPRId()));
				
		return Views.AJAX_TEXT;	
	}

	/**
	 * Action called when a user updates preferences of a person
	 * @param command
	 */
	private View updateAction(PersonPageCommand command) {
		command.setPerson(this.logic.getPersonById(Integer.parseInt(command.getFormPersonId())));
		command.getPerson().setAcademicDegree(command.getFormAcademicDegree());
		command.getPerson().getMainName().setMain(false);
		command.getPerson().setMainName(Integer.parseInt(command.getFormSelectedName()));
		command.getPerson().setOrcid(command.getFormOrcid());
		this.logic.createOrUpdatePerson(command.getPerson());
		
		return Views.AJAX_TEXT;
	}

	/**
	 * Action called when a user adds an alternative name to a person
	 * @param command
	 */
	private View addNameAction(PersonPageCommand command) {
		Person person = logic.getPersonById(Integer.valueOf(command.getFormPersonId()).intValue());
		PersonName personName = new PersonName(command.getFormLastName()).withFirstName(command.getFormFirstName()).withPersonId(Integer.valueOf(command.getFormPersonId()).intValue());
		for( PersonName otherName : person.getNames()) {
			if(personName.equals(otherName)) {
				command.setResponseString(otherName.getId()+ "");
				return Views.AJAX_TEXT;
			}
		}
		
		this.logic.createOrUpdatePersonName(personName);
		command.setResponseString(personName.getId() + "");
		return Views.AJAX_TEXT;
	}

	/**
	 * Action called when a user removes an alternative name from a person
	 * @param command
	 * @return
	 */
	private View deleteNameAction(PersonPageCommand command) {
		this.logic.removePersonName(new Integer(command.getFormPersonNameId()));
		return Views.AJAX_TEXT;
	}
	
	/**
	 * Default action called when a user url is called
	 * @param command
	 * @return
	 */
	private View showAction(PersonPageCommand command) {
		
		for(PersonResourceRelation prr : PersonResourceRelation.values()) {
			command.getAvailableRoles().add(prr.getRelatorCode());
		}
		
		command.setPerson(this.logic.getPersonById(Integer.parseInt(command.getRequestedPersonId())));
		
		List<ResourcePersonRelation> resourceRelations = this.logic.getResourceRelations(command.getPerson());
		List<Post<?>> authorPosts = new ArrayList<>();
		List<Post<?>> advisorPosts = new ArrayList<>();

		for(ResourcePersonRelation rpr : resourceRelations) {
			if(rpr.getRelatorCode().equals(PersonResourceRelation.AUTHOR.getRelatorCode())) 
				authorPosts.add(rpr.getPost());
			else
				advisorPosts.add(rpr.getPost());
		}
		
		command.setThesis(authorPosts);
		command.setAdvisedThesis(advisorPosts);
		
		return Views.PERSON_SHOW;
	}

	@Override
	public PersonPageCommand instantiateCommand() {
		return new PersonPageCommand();
	}
}


