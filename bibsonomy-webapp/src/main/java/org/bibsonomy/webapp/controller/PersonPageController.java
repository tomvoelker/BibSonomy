package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
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
				case "link": return this.linkAction(command);
				case "search": return this.searchAction(command);
				case "searchpub": return this.searchPubAction(command);
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
	private View searchPubAction(PersonPageCommand command) { 
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
			jsonPersonName.put("personNameId", personName.getPersonChangeId());
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
		if (command.getFormPersonIndex() == -1) {
			throw new IllegalArgumentException();
		}
		
		ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		Post<BibTex> post = new Post<>();
		post.setResource(new BibTex());
		post.getResource().setInterHash(command.getFormInterHash());
		resourcePersonRelation.setPost(post);
		resourcePersonRelation.setPerson(new Person());
		resourcePersonRelation.getPerson().setPersonId(command.getFormPersonId());
		resourcePersonRelation.setPersonIndex(command.getFormPersonIndex());
		this.logic.addResourceRelation(resourcePersonRelation);
		command.setResponseString(resourcePersonRelation.getPersonChangeId() + "");
		return Views.AJAX_TEXT;
	}

	/**
	 * Action called when a user wants to edit the role of a person in a thesis
	 * @param command
	 * @return
	 */
	private View editRoleAction(PersonPageCommand command) {
		//TODO add new role types to view
		for (String role : command.getFormPersonRoles()) {
			final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
			Post<BibTex> post = new Post<>();
			post.setResource(new BibTex());
			post.getResource().setInterHash(command.getFormInterHash());
			resourcePersonRelation.setPost(post);
			resourcePersonRelation.setPerson(new Person());
			resourcePersonRelation.getPerson().setPersonId(command.getFormPersonId());
			resourcePersonRelation.setPersonIndex(command.getFormPersonIndex());
			final PersonResourceRelationType relationType = PersonResourceRelationType.valueOf(StringUtils.upperCase(role)); 
			resourcePersonRelation.setRelationType(relationType);
			this.logic.addResourceRelation(resourcePersonRelation);
		}
		
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(command.getPerson().getPersonId()));	
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
		command.setPerson(this.logic.getPersonById(PersonIdType.BIBSONOMY_ID, command.getFormPersonId()));
		if (command.getPerson() == null) {
			// TODO: proper frontend responses in cases like this
			throw new NoSuchElementException();
		}
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
		final Person person = logic.getPersonById(PersonIdType.BIBSONOMY_ID, command.getFormPersonId());
		
		final PersonName personName = new PersonName(command.getFormLastName());
		personName.setFirstName(command.getFormFirstName());
		personName.setPersonId(command.getFormPersonId());
		
		for (PersonName otherName : person.getNames()) {
			if (personName.equals(otherName)) {
				command.setResponseString(otherName.getPersonChangeId()+ "");
				return Views.AJAX_TEXT;
			}
		}
		this.logic.createOrUpdatePersonName(personName);
		command.setResponseString(Integer.toString(personName.getPersonChangeId()));
		
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
		
		for(PersonResourceRelationType prr : PersonResourceRelationType.values()) {
			command.getAvailableRoles().add(prr);
		}
		
		command.setPerson(this.logic.getPersonById(PersonIdType.BIBSONOMY_ID, command.getRequestedPersonId()));
		
		List<ResourcePersonRelation> resourceRelations = this.logic.getResourceRelations(command.getPerson());
		List<Post<?>> authorPosts = new ArrayList<>();
		List<Post<?>> advisorPosts = new ArrayList<>();

		for(ResourcePersonRelation resourcePersonRelation : resourceRelations) {
			if(!resourcePersonRelation.getPost().getResource().getEntrytype().toLowerCase().endsWith("thesis")) {
				continue;	
			}
			
			resourcePersonRelation.getPost().setResourcePersonRelations(this.logic.getResourceRelations(resourcePersonRelation.getPost()));
			
			if (resourcePersonRelation.getRelationType().equals(PersonResourceRelationType.AUTHOR)) { 
				authorPosts.add(resourcePersonRelation.getPost());
			} else {
				advisorPosts.add(resourcePersonRelation.getPost());
			}
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


