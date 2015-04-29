package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.util.spring.security.AuthenticationUtils;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
		List<PersonName> personNames;
			List<PersonName> personQuery = null;
			try {
				personQuery = PersonNameUtils.discoverPersonNames(command.getFormSelectedName());
			} catch (PersonListParserException e) {
				// ok
			}
			if (!CollectionUtils.isEmpty(personQuery)) {
				personNames = this.logic.getPersonSuggestion(personQuery.get(0));
				String firstName = personQuery.get(0).getFirstName();
				personQuery.get(0).setFirstName(personQuery.get(0).getLastName());
				personQuery.get(0).setLastName(firstName);
				personNames.addAll(this.logic.getPersonSuggestion(personQuery.get(0)));
			} else {
				personNames = this.logic.getPersonSuggestion(command.getFormSelectedName(), null);
			}
		
		
		JSONArray array = new JSONArray();
		for(PersonName personName : personNames) {
			JSONObject jsonPersonName = new JSONObject();
			jsonPersonName.put("personId", personName.getPersonId());
			jsonPersonName.put("personNameId", personName.getId());
			jsonPersonName.put("personName", BibTexUtils.cleanBibTex(personName.toString()));
			// FIXME: this is only a quick hack and must be replaced!
			jsonPersonName.put("extendedPersonName", BibTexUtils.cleanBibTex(getExtendedPersonName(personName)));
			
			array.add(jsonPersonName);
		}
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}
	
	/**
	 * @param personName
	 * @return
	 */
	private String getExtendedPersonName(PersonName personName) {
		final StringBuilder extendedNameBuilder = new StringBuilder(personName.getLastName());
		if (present(personName.getFirstName())) {
			extendedNameBuilder.append(", ").append(personName.getFirstName());
		}
		final Person person = personName.getPerson();
		if (present(person) && present(person.getAcademicDegree())) {
			extendedNameBuilder.append(", ").append(person.getAcademicDegree());
		}
		BibTex res = null;
		for (ResourcePersonRelation resourcePersonRelation : person.getResourcePersonRelations()) {
			String entryType;
			try {
				entryType = resourcePersonRelation.getPost().getResource().getEntrytype();
				if (!present(entryType)) {
					continue;
				}
			} catch (Exception e) {
				continue;
			}
			if (entryType.toLowerCase().endsWith("thesis")) {
				res = resourcePersonRelation.getPost().getResource();
				break;
			}
			res = resourcePersonRelation.getPost().getResource();
		}
		if (present(res)) {
			String entryType = res.getEntrytype();
			if (entryType.toLowerCase().endsWith("thesis")) {
				if (present(res.getSchool())) {
					extendedNameBuilder.append(", ").append(res.getSchool());
				}
			}
			if (present(res.getYear())) {
				extendedNameBuilder.append(", ").append(res.getYear());
			}
			if (present(res.getTitle())) {
				extendedNameBuilder.append(", \"").append(res.getTitle()).append('"');
			}
		}
		return extendedNameBuilder.toString();
		
		// Nachname, Vorname, Akad. Grad, sowie Ort, Jahr und Titel 
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
		ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation()
		.withSimhash1(command.getFormInterHash())
		.withSimhash2(command.getFormIntraHash())
		.withRelatorCode(role)
		.withPersonId(person.getId())
		.withPubOwner(command.getFormUser())
		.withAuthorIndex(new Integer(command.getFormAuthorIndex()));
		this.logic.addResourceRelation(resourcePersonRelation);
		
		JSONObject jsonPerson = new JSONObject();
		jsonPerson.put("personId", person.getId());
		jsonPerson.put("personName", person.getMainName().toString());
		jsonPerson.put("personNameId", new Integer(person.getMainName().getId()));
		jsonPerson.put("resourcePersonRelationid", new Integer(resourcePersonRelation.getId()));
		
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
		this.logic.linkUser(command.getFormPersonId());
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
		
		ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation()
			.withSimhash1(command.getFormInterHash())
			.withSimhash2(command.getFormIntraHash())
			.withRelatorCode(role)
			.withPersonId(command.getFormPersonId())
			.withPubOwner(command.getFormUser());
		this.logic.addResourceRelation(resourcePersonRelation);
		command.setResponseString(resourcePersonRelation.getId() + "");
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
			ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation()
			.withSimhash1(command.getFormInterHash())
			.withSimhash2(command.getFormIntraHash())
			.withRelatorCode(PersonResourceRelation.valueOf(role).getRelatorCode())
			.withPersonId(command.getFormPersonId())
			.withPubOwner(command.getRequestedUser());
			this.logic.addResourceRelation(resourcePersonRelation);
		}
				
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getId(), command.getPerson().getMainName().toString(), command.getPost().getResource().getInterHash(), command.getPost().getUser().getName(), command.getRequestedRole(), new Integer(command.getRequestedIndex())));	
	}
	
	@SuppressWarnings("boxing")
	private View deleteRoleAction(PersonPageCommand command) {

		this.logic.removeResourceRelation(Integer.valueOf(command.getFormResourcePersonRelationId()));
				
		return Views.AJAX_TEXT;	
	}

	/**
	 * Action called when a user updates preferences of a person
	 * @param command
	 */
	private View updateAction(PersonPageCommand command) {
		command.setPerson(this.logic.getPersonById(command.getFormPersonId()));
		command.getPerson().setAcademicDegree(command.getFormAcademicDegree());
		command.getPerson().getMainName().setMain(false);
		command.getPerson().setMainName(Integer.parseInt(command.getFormSelectedName()));
		command.getPerson().setOrcid(command.getFormOrcid());
		command.getPerson().setUser(command.isFormThatsMe() ? AuthenticationUtils.getUser().getName() : null);
		this.logic.createOrUpdatePerson(command.getPerson());
		
		return Views.AJAX_TEXT;
	}

	/**
	 * Action called when a user adds an alternative name to a person
	 * @param command
	 */
	private View addNameAction(PersonPageCommand command) {
		Person person = logic.getPersonById(command.getFormPersonId());
		PersonName personName = new PersonName(command.getFormLastName()).withFirstName(command.getFormFirstName()).withPersonId(command.getFormPersonId());
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
		
		command.setPerson(this.logic.getPersonById(command.getRequestedPersonId()));
		
		List<ResourcePersonRelation> resourceRelations = this.logic.getResourceRelations(command.getPerson());
		List<Post<?>> authorPosts = new ArrayList<>();
		List<Post<?>> advisorPosts = new ArrayList<>();

		for(ResourcePersonRelation resourcePersonRelation : resourceRelations) {
			if(!resourcePersonRelation.getPost().getResource().getEntrytype().toLowerCase().endsWith("thesis")) {
				continue;	
			}
			
			if(resourcePersonRelation.getRelatorCode().equals(PersonResourceRelation.AUTHOR.getRelatorCode())) 
				authorPosts.add(resourcePersonRelation.getPost());
			else
				advisorPosts.add(resourcePersonRelation.getPost());
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


