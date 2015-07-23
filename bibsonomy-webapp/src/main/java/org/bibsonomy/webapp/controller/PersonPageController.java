package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang.StringUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.exception.LogicException;
import org.bibsonomy.model.util.BibTexUtils;
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
				case "addThesis": return this.addThesisAction(command);
				case "editRole": return this.editRoleAction(command);
				case "deleteRole": return this.deleteRoleAction(command);
				case "unlink": return this.unlinkAction(command);
				case "link": return this.linkAction(command);
				case "search": return this.searchAction(command);
				case "searchAuthor": return this.searchAuthorAction(command);
				case "searchPub": return this.searchPubAction(command);
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
	private View addThesisAction(PersonPageCommand command) {
		addRoleAction(command);
		return showAction(command);
	}

	/**
	 * @param command
	 * @return
	 */
	private View searchAuthorAction(PersonPageCommand command) { 
		final List<ResourcePersonRelation> suggestions = this.logic.getPersonSuggestion(command.getFormSelectedName()).withNonEntityPersons(true).withRelationType(PersonResourceRelationType.AUTHOR).preferUnlinked(true).doIt();
		
		JSONArray array = new JSONArray();
		for (ResourcePersonRelation rel : suggestions) {
			JSONObject jsonPersonName = new JSONObject();
			jsonPersonName.put("interhash", rel.getPost().getResource().getInterHash());
			jsonPersonName.put("personIndex", rel.getPersonIndex());
			//jsonPersonName.put("personNameId", personName.getPersonChangeId());
			final List<PersonName> authors = rel.getPost().getResource().getAuthor();
			jsonPersonName.put("personName", BibTexUtils.cleanBibTex(authors.get(rel.getPersonIndex()).toString()));
			jsonPersonName.put("extendedPublicationName", getExtendedPublicationName(rel.getPost()));
			
			array.add(jsonPersonName);
		}
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}
	
	private View searchPubAction(PersonPageCommand command) { 
		final List<Post<BibTex>> suggestions = this.logic.getPublicationSuggestion(command.getFormSelectedName());
		
		JSONArray array = new JSONArray();
		for (Post<BibTex> pub : suggestions) {
			JSONObject jsonPersonName = new JSONObject();
			jsonPersonName.put("interhash", pub.getResource().getInterHash());
			jsonPersonName.put("extendedName", getExtendedPublicationName(pub));
			array.add(jsonPersonName);
		}
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}

	/**
	 * @param pub
	 * @return
	 */
	private String getExtendedPublicationName(Post<? extends BibTex> pub) {
		final StringBuilder extendedNameBuilder = new StringBuilder();
		for (PersonName personName : pub.getResource().getAuthor()) {
			appendPersonName(personName, extendedNameBuilder);
			extendedNameBuilder.append(", ");
			appendDisambiguatingBibTexInfo(extendedNameBuilder, pub.getResource());
		}
		return extendedNameBuilder.toString();
	}

	/**
	 * @param command
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private View searchAction(PersonPageCommand command) {
		final List<ResourcePersonRelation> suggestions = this.logic.getPersonSuggestion(command.getFormSelectedName()).withEntityPersons(true).withRelationType(PersonResourceRelationType.values()).doIt();
		
		JSONArray array = new JSONArray();
		for (ResourcePersonRelation rel : suggestions) {
			JSONObject jsonPersonName = new JSONObject();
			jsonPersonName.put("personId", rel.getPerson().getPersonId());
			//jsonPersonName.put("personNameId", personName.getPersonChangeId());
			jsonPersonName.put("personName", BibTexUtils.cleanBibTex(rel.getPerson().getMainName().toString()));
			jsonPersonName.put("extendedPersonName", getExtendedPersonName(rel));
			
			array.add(jsonPersonName);
		}
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}

	private static String getExtendedPersonName(ResourcePersonRelation rel) {
		final Person person = rel.getPerson();
		final PersonName personName = person.getMainName();
		final StringBuilder extendedNameBuilder = new StringBuilder();
		appendPersonName(personName, extendedNameBuilder);
		if (present(person) && present(person.getAcademicDegree())) {
			extendedNameBuilder.append(", ").append(person.getAcademicDegree());
		}
		final BibTex res = rel.getPost().getResource();
		if (present(res)) {
			appendDisambiguatingBibTexInfo(extendedNameBuilder, res);
		}
		return extendedNameBuilder.toString();
	}
	
	private static void appendPersonName(PersonName personName, final StringBuilder extendedNameBuilder) {
		extendedNameBuilder.append(personName.getLastName());
		if (present(personName.getFirstName())) {
			extendedNameBuilder.append(", ").append(personName.getFirstName());
		}
	}

	private static void appendDisambiguatingBibTexInfo(final StringBuilder extendedNameBuilder, BibTex res) {
		String entryType = res.getEntrytype();
		if (entryType.toLowerCase().endsWith("thesis")) {
			if (present(res.getSchool())) {
				extendedNameBuilder.append(", ").append(BibTexUtils.cleanBibTex(res.getSchool()));
			}
		}
		if (present(res.getYear())) {
			extendedNameBuilder.append(", ").append(BibTexUtils.cleanBibTex(res.getYear()));
		}
		if (present(res.getTitle())) {
			extendedNameBuilder.append(", \"").append(BibTexUtils.cleanBibTex(res.getTitle())).append('"');
		}
	}

	@SuppressWarnings("static-method")
	private View indexAction(@SuppressWarnings("unused") PersonPageCommand command) {
		return Views.PERSON_SHOW;
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
		ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		Post<BibTex> post = new Post<>();
		post.setResource(new BibTex());
		post.getResource().setInterHash(command.getFormInterHash());
		resourcePersonRelation.setPost(post);
		resourcePersonRelation.setPerson(new Person());
		resourcePersonRelation.getPerson().setPersonId(command.getFormPersonId());
		resourcePersonRelation.setPersonIndex(command.getFormPersonIndex());
		resourcePersonRelation.setRelationType(command.getFormPersonRole());
		try {
			this.logic.addResourceRelation(resourcePersonRelation);
		} catch (LogicException e) {
			command.getLogicExceptions().add(e);
		}
		command.setResponseString(resourcePersonRelation.getPersonRelChangeId() + "");
		return Views.AJAX_TEXT;
	}

	/**
	 * Action called when a user wants to edit the role of a person in a thesis
	 * @param command
	 * @return
	 */
	private View editRoleAction(PersonPageCommand command) {
		//TODO not used? remove?
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
			try {
				this.logic.addResourceRelation(resourcePersonRelation);
			} catch (LogicException e) {
				command.getLogicExceptions().add(e);
			}
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
				command.setResponseString(otherName.getPersonNameChangeId()+ "");
				return Views.AJAX_TEXT;
			}
		}
		this.logic.createOrUpdatePersonName(personName);
		command.setResponseString(Integer.toString(personName.getPersonNameChangeId()));
		
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


