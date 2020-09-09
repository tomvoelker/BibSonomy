/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonMatch;
import org.bibsonomy.model.PersonMergeFieldConflict;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationOrder;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.exception.LogicException;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.model.logic.query.PostQuery;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.model.util.PersonMatchUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.PersonUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.person.PersonRoleRenderer;
import org.bibsonomy.util.Sets;
import org.bibsonomy.webapp.command.PersonPageCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.picture.PictureHandlerFactory;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.validation.Errors;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * controller for a single person details page
 * paths:
 * - /person/PERSON_ID
 *
 * e.g.
 * /person/a.hotho
 *
 * @author Christian Pfeiffer
 */
public class PersonPageController extends SingleResourceListController implements MinimalisticController<PersonPageCommand>, ErrorAware {
	private static final Log log = LogFactory.getLog(PersonMatch.class);

	public static final Set<PersonResourceRelationType> PUBLICATION_RELATED_RELATION_TYPES = Sets.asSet(PersonResourceRelationType.AUTHOR, PersonResourceRelationType.EDITOR);

	private URLGenerator urlGenerator;
	private RequestLogic requestLogic;
	private PersonRoleRenderer personRoleRenderer;
	private Errors errors;
	private PictureHandlerFactory pictureHandlerFactory;
	/** the college that the cris system is configured for */
	private String crisCollege;

	private LogicInterface adminLogic;


	@Override
	public PersonPageCommand instantiateCommand() {
		return new PersonPageCommand();
	}
	
	@Override
	public View workOn(final PersonPageCommand command) {
		final RequestWrapperContext context = command.getContext();
		final String formAction = command.getFormAction();
		final boolean action = present(formAction);
		if (!action && !present(command.getRequestedPersonId())){
			throw new MalformedURLSchemeException("The person page was requested without a person in the request.");
		}

		if (present(formAction)) {
			if (!context.isValidCkey()) {
				errors.reject("error.field.valid.ckey");
			}

			switch(formAction) {
				case "update": return this.updateAction(command);
				case "addName": return this.addNameAction(command);
				case "deleteName": return this.deleteNameAction(command);
				case "setMainName": return this.setMainNameAction(command);
				case "addRole": return this.addRoleAction(command);
				case "addThesis": return this.addThesisAction(command);
				case "editRole": return this.editRoleAction(command);
				case "deleteRole": return this.deleteRoleAction(command);
				case "unlink": return this.unlinkAction(command);
				case "link": return this.linkAction(command);
				case "search": return this.searchAction(command);
				case "searchPub": return this.searchPubAction(command);
				case "searchPubAuthor": return this.searchPubAuthorAction(command);
				case "merge": return this.mergeAction(command);
				case "conflictMerge": return this.conflictMerge(command);
				case "getConflict": return this.getConflicts(command);

				default:
					return indexAction();
			}
		} else if (present(command.getRequestedPersonId())) {
			return this.showAction(command);
		}
		
		// the following statement cannot be reached, and seems useless anyway, since in this case no formAction was present and not PersonId. 
		// Remove when sure. 
		return indexAction();
	}

	public LogicInterface getAdminLogic() {
		return adminLogic;
	}

	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}

	/**
	 * @param command
	 * @return
	 */
	private View conflictMerge(PersonPageCommand command) {
		final JSONObject jsonResponse = new JSONObject();

		try {
			final Map<String, String> map = new HashMap<>();
			final Person person = command.getPerson();
			if (present(person)) {
				for (final String fieldName : Person.fieldsWithResolvableMergeConflicts){
					final PropertyDescriptor desc = new PropertyDescriptor(fieldName, Person.class);
					final Object value = desc.getReadMethod().invoke(person);

					if (value != null) {
						map.put(fieldName, value.toString());
					}
				}
			}
			final PersonName newName = command.getNewName();
			if (present(newName)) {
				map.put("mainName", PersonNameUtils.serializePersonName(newName));
			}

			jsonResponse.put("status", this.logic.conflictMerge(command.getFormMatchId(), map));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | IntrospectionException e) {
			log.error("error while building cpm", e);
			jsonResponse.put("status", false);
		}

		command.setResponseString(jsonResponse.toString());
		return Views.AJAX_JSON;
	}

	/**
	 * FIXME: we DO NOT use database ids in the webapp!!!!!!!
	 *
	 * @param command
	 * @return
	 */
	private View getConflicts(final PersonPageCommand command) {
		final int formMatchId = command.getFormMatchId();
		final PersonMatch personMatch = this.logic.getPersonMatch(formMatchId);

		final JSONArray array = new JSONArray();
		for (PersonMergeFieldConflict conflict : PersonMatchUtils.getPersonMergeConflicts(personMatch)) {
			final JSONObject jsonConflict = new JSONObject();
			jsonConflict.put("field", conflict.getFieldName());
			jsonConflict.put("person1Value", conflict.getPerson1Value());
			jsonConflict.put("person2Value", conflict.getPerson2Value());
			array.add(jsonConflict);
		}
		command.setResponseString(array.toJSONString());
		return Views.AJAX_JSON;
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
	 * This is a helper function that adds to an JSONarray from a list of resource-person-relations.
	 * @param suggestions
	 * @param array
	 * @return
	 */
	private void buildupAuthorResponseArray(final List<ResourcePersonRelation> suggestions, JSONArray array) {
			for (final ResourcePersonRelation rel : suggestions) {
				final JSONObject jsonPersonName = new JSONObject();
				jsonPersonName.put("interhash", rel.getPost().getResource().getInterHash());
				final int personIndex = rel.getPersonIndex();
				jsonPersonName.put("personIndex", personIndex);

				final BibTex pub = rel.getPost().getResource();
				final List<PersonName> authors = pub.getAuthor();
				jsonPersonName.put("personName", BibTexUtils.cleanBibTex(authors.get(personIndex).toString()));
				jsonPersonName.put("extendedPublicationName", this.personRoleRenderer.getExtendedPublicationName(pub, this.requestLogic.getLocale(), false));
				array.add(jsonPersonName);
			}
	}
	
	/** 
	 * This is a helper function adds to an JSONarray Publications form a sugesstions list.  
	 * @param posts
	 * @return
	 */	
	private JSONArray buildupPubResponseArray(final List<Post<GoldStandardPublication>> posts) {
		final JSONArray array = new JSONArray();
		for (final Post<GoldStandardPublication> post : posts) {
			final JSONObject jsonPersonName = new JSONObject();
			final BibTex publication = post.getResource();
			jsonPersonName.put("interhash", publication.getInterHash());
			jsonPersonName.put("extendedPublicationName", this.personRoleRenderer.getExtendedPublicationName(publication, this.requestLogic.getLocale(), false));
			array.add(jsonPersonName);
		}
		return array;
	}

	/**
	 * @param command
	 * @return
	 */
	private View searchPubAction(PersonPageCommand command) { 
		final List<Post<GoldStandardPublication>> suggestions = this.getSuggestionPub(command.getFormSelectedName());
		final JSONArray array = this.buildupPubResponseArray(suggestions);
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}

	private List<Post<GoldStandardPublication>> getSuggestionPub(final String search) {
		final PostQuery<GoldStandardPublication> postQuery = new PostQueryBuilder().setSearch(search).
						createPostQuery(GoldStandardPublication.class);
		// TODO limit searches to thesis
		return this.logic.getPosts(postQuery);
	}

	/**
	 * Combined publication and author search action. This search is in particular necessary 
	 * when someone want's to find unrelated (no role associated to authors) documents.  
	 * @param command
	 * @return
	 */
	private View searchPubAuthorAction(final PersonPageCommand command) {
		final List<Post<GoldStandardPublication>> suggestionsPub = this.getSuggestionPub(command.getFormSelectedName());
		
		final JSONArray array = new JSONArray();

		array.addAll(buildupPubResponseArray(suggestionsPub));  // Publications (not associated to Persons) oriented search return
		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}

	/**
	 * @param command
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private View searchAction(PersonPageCommand command) {
		final PersonQuery query = new PersonQuery(command.getFormSelectedName());
		if (command.isLimitResultsToCRISCollege() && present(this.crisCollege)) {
			query.setCollege(this.crisCollege);
		}

		/*
		 * query the persons and get the publication that should be displayed alongside the person
		 */
		final List<Person> persons = this.logic.getPersons(query);
		final JSONArray array = new JSONArray();
		for (final Person person : persons) {
			final JSONObject jsonPersonName = new JSONObject();
			jsonPersonName.put("personId", person.getPersonId());
			final String personName = BibTexUtils.cleanBibTex(person.getMainName().toString());
			jsonPersonName.put("personName", personName);
			jsonPersonName.put("extendedPersonName", personName); // FIXME: this.personRoleRenderer.getExtendedPersonName(rel, this.requestLogic.getLocale(), false));

			array.add(jsonPersonName);
		}

		command.setResponseString(array.toJSONString());
		
		return Views.AJAX_JSON;
	}

	private static View indexAction() {
		return Views.PERSON_SHOW;
	}

	/**
	 * action called when a user want to unlink an author from a publication
	 * @param command
	 * @return the ajax text view
	 */
	private View unlinkAction(PersonPageCommand command) {
		this.logic.unlinkUser(this.logic.getAuthenticatedUser().getName());
		return Views.AJAX_TEXT;
	}
	
	private View linkAction(final PersonPageCommand command) {
		final Person person = new Person();
		person.setPersonId(command.getFormPersonId());
		person.setUser(command.getContext().getLoginUser().getName());
		this.logic.updatePerson(person, PersonUpdateOperation.LINK_USER);
		return Views.AJAX_TEXT;
	}

	/**
	 * Action called when a user wants to add a person role to a thesis
	 * @param command
	 * @return
	 */
	private View addRoleAction(PersonPageCommand command) {
		final JSONObject jsonResponse = new JSONObject();
		final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		final Post<BibTex> post = new Post<>();
		post.setResource(new BibTex());
		post.getResource().setInterHash(command.getFormInterHash());
		resourcePersonRelation.setPost(post);
		
		try {
			final Person person = new Person();
			if (present(command.getFormPersonId())) {
				person.setPersonId(command.getFormPersonId());
			} else {
				final PersonName mainName = command.getNewName();
				mainName.setMain(true);
				person.setMainName(mainName);
				this.logic.createPerson(person);
			}
			resourcePersonRelation.setPerson(person);
			resourcePersonRelation.setPersonIndex(command.getFormPersonIndex());
			resourcePersonRelation.setRelationType(command.getFormPersonRole());

			this.logic.createResourceRelation(resourcePersonRelation);
		} catch (LogicException e) {
			command.getLogicExceptions().add(e);
			jsonResponse.put("exception", e.getClass().getSimpleName());
		}

		jsonResponse.put("personId", resourcePersonRelation.getPerson().getPersonId());
		jsonResponse.put("resourcePersonRelationid", resourcePersonRelation.getPersonRelChangeId() + "");
		jsonResponse.put("personUrl", this.urlGenerator.getPersonUrl(resourcePersonRelation.getPerson().getPersonId()));
		command.setResponseString(jsonResponse.toJSONString());
		
		return Views.AJAX_JSON;
	}

	/**
	 * Action called when a user wants to edit the role of a person in a thesis
	 * @param command
	 * @return
	 */
	private View editRoleAction(PersonPageCommand command) {
		// TODO not used? remove?
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
				this.logic.createResourceRelation(resourcePersonRelation);
			} catch (LogicException e) {
				command.getLogicExceptions().add(e);
			}
		}
		
		return new ExtendedRedirectView(this.urlGenerator.getPersonUrl(command.getPerson().getPersonId()));
	}
	
	private View deleteRoleAction(PersonPageCommand command) {
		this.logic.removeResourceRelation(null, null, -1, null); // FIXME: change
		
		return Views.AJAX_TEXT;
	}

	/*
	 * performs the merge action for the selected match
	 */
	private View mergeAction(PersonPageCommand command) {
		int id = command.getFormMatchId();
		JSONObject jsonResponse = new JSONObject();

		PersonMatch match = this.logic.getPersonMatch(id);
		boolean result = true;
		if (command.getUpdateOperation() == PersonUpdateOperation.MERGE_ACCEPT) {
			result = this.logic.acceptMerge(match);
		} else if (command.getUpdateOperation() == PersonUpdateOperation.MERGE_DENIED) {
			this.logic.denieMerge(match);
		}
		jsonResponse.put("status", result);
		command.setResponseString(jsonResponse.toString());
		return Views.AJAX_JSON;
	}
	
	/**
	 * action called when a user updates preferences of a person
	 * @param command
	 */
	private View updateAction(PersonPageCommand command) {
		final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getFormPersonId());

		// TODO: check if person present!

		final Person commandPerson = command.getPerson();
		if (!present(commandPerson)) {
			// FIXME: proper frontend responses in cases like this
			throw new NoSuchElementException();
		}
		
		final PersonUpdateOperation operation = command.getUpdateOperation();
		final JSONObject jsonResponse = new JSONObject();

		// FIXME: why do we have to copy all values from the command person to the person found in the logic?
		// set all attributes that might be updated
		person.setAcademicDegree(commandPerson.getAcademicDegree());
		person.setOrcid(commandPerson.getOrcid().replaceAll("-", ""));
		person.setResearcherid(commandPerson.getResearcherid().replaceAll("-", ""));
		person.setCollege(commandPerson.getCollege());
		
		// TODO only allow updates if the editor "is" this person
		person.setEmail(commandPerson.getEmail());
		person.setHomepage(commandPerson.getHomepage());
		
		// FIXME: write independent update method
		// FIXME: add its me action
		
		//command.getPerson().getMainName().setMain(false);
		//command.getPerson().setMainName(Integer.parseInt(command.getFormSelectedName()));

		try {
			this.logic.updatePerson(person, operation);
			jsonResponse.put("status", true);

		} catch (final Exception e) {
			log.error("error while updating person " + commandPerson.getPersonId(), e);
			jsonResponse.put("status", false);
			// TODO: set proper error message
			//jsonResponse.put("message", "Some error occured");
		}

		command.setResponseString(jsonResponse.toString());
		return Views.AJAX_JSON;
	}

	/**
	 * Action called when a user adds an alternative name to a person
	 * @param command
	 */
	private View addNameAction(PersonPageCommand command) {
		final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, command.getPerson().getPersonId());

		final JSONObject jsonResponse = new JSONObject();

		if (!present(person) || !present(command.getNewName())) {
			jsonResponse.put("status", false);
			// TODO: set proper error message
			//jsonResponse.put("message", "Person cannot be found.");
			command.setResponseString(jsonResponse.toString());
			return Views.AJAX_JSON;
		}

		final PersonName personName = command.getNewName();
		personName.setPersonId(command.getPerson().getPersonId());
		for (PersonName otherName : person.getNames()) {
			if (personName.equals(otherName)) {
				//command.setResponseString(otherName.getPersonNameChangeId()+ "");
				jsonResponse.put("status", true);
				jsonResponse.put("personNameChangeId", otherName.getPersonNameChangeId());
				command.setResponseString(jsonResponse.toString());
				return Views.AJAX_JSON;
			}
		}
		
		try {
			this.logic.createPersonName(personName);
		} catch (Exception e) {
			jsonResponse.put("status", false);
			// TODO: set proper error message
			//jsonResponse.put("message", "Some error occured");
			command.setResponseString(jsonResponse.toString());
			return Views.AJAX_JSON;
		}
		
		jsonResponse.put("status", true);
		jsonResponse.put("personNameChangeId", personName.getPersonNameChangeId());
		command.setResponseString(jsonResponse.toString());
		return Views.AJAX_JSON;		
	}

	/**
	 * Action called when a user removes an alternative name from a person
	 * @param command
	 * @return
	 */
	private View deleteNameAction(PersonPageCommand command) {
		final JSONObject jsonResponse = new JSONObject();
		try {			
			this.logic.removePersonName(new Integer(command.getFormPersonNameId()));
		} catch (Exception e) {
			jsonResponse.put("status", false);
			// TODO: set proper error message
			//jsonResponse.put("message", "Some error occured");
			command.setResponseString(jsonResponse.toString());
			return Views.AJAX_JSON;
		}
		
		jsonResponse.put("status", true);
		command.setResponseString(jsonResponse.toString());
		return Views.AJAX_JSON;	
	}
	
	
	private View setMainNameAction(PersonPageCommand command) {
		final Person person = logic.getPersonById(PersonIdType.PERSON_ID, command.getPerson().getPersonId());
		
		final JSONObject jsonResponse = new JSONObject();
		
		
		person.getMainName().setMain(false);
		person.setMainName(Integer.parseInt(command.getFormSelectedName()));
		
		// bind the new person
		command.setPerson(person);
		
		try {
			this.logic.updatePerson(person, PersonUpdateOperation.UPDATE_NAMES);
		} catch (final Exception e) {
			jsonResponse.put("status", false);
			// TODO: set proper error message
			//jsonResponse.put("message", "Some error occured");
			command.setResponseString(jsonResponse.toString());
			return Views.AJAX_JSON;
		}
		
		jsonResponse.put("status", true);
		command.setResponseString(jsonResponse.toString());
		
		return Views.AJAX_JSON;	
	}

	/**
	 * handles the person page
	 * @param command
	 * @return
	 */
	private View showAction(final PersonPageCommand command) {
		// TODO: remove initialization
		for (PersonResourceRelationType prr : PersonResourceRelationType.values()) {
			command.getAvailableRoles().add(prr);
		}
		// FIXME: remove? TODO_CRIS
		command.setShowProjects(true);

		final String requestedPersonId = command.getRequestedPersonId();
		/*
		 * get the person; if person with the requested id was merged with another person, this method
		 * throws a ObjectMovedException and the wrapper will render the redirect
		 */
		final Person person = this.logic.getPersonById(PersonIdType.PERSON_ID, requestedPersonId);
		if (!present(person)) {
			return Views.ERROR404;
		}

		command.setPerson(person);
		command.setPhdAdvisorRecForPerson(this.logic.getPhdAdvisorRecForPerson(person.getPersonId()));

		final User authenticatedUser = this.logic.getAuthenticatedUser();

		if (authenticatedUser != null && authenticatedUser.getSettings().getListItemcount() > 0) {
			command.setPersonPostsPerPage(authenticatedUser.getSettings().getListItemcount());
		} else {
			command.setPersonPostsPerPage(20);
		}

		// Get the linked user's person posts style settings
		final String linkedUser = person.getUser();
		if (present(linkedUser)) {
			final User user = this.adminLogic.getUserDetails(linkedUser);

			command.setPersonPostsStyleSettings(user.getSettings().getPersonPostsStyle());

			// Get 'myown' posts of the linked user
			PostQueryBuilder myOwnqueryBuilder = new PostQueryBuilder()
					.setStart(0)
					.setEnd(command.getPersonPostsPerPage())
					.setTags(new ArrayList<>(Collections.singletonList("myown")))
					.setGrouping(GroupingEntity.USER)
					.setGroupingName(person.getUser());
			final List<Post<BibTex>> myownPosts = this.logic.getPosts(myOwnqueryBuilder.createPostQuery(BibTex.class));
			command.setMyownPosts(myownPosts);

		} else {
			// default to gold standard publications, if no linked user found
			command.setPersonPostsStyleSettings(0);
		}



		// TODO: this needs to be removed/refactored as soon as the ResourcePersonRelationQuery.ResourcePersonRelationQueryBuilder accepts start/end
		ResourcePersonRelationQueryBuilder queryBuilder = new ResourcePersonRelationQueryBuilder()
				.byPersonId(person.getPersonId())
				.withPosts(true)
				.withPersonsOfPosts(true)
				.groupByInterhash(true)
				.orderBy(PersonResourceRelationOrder.PublicationYear)
				.fromTo(0, command.getPersonPostsPerPage());

		ResourcePersonRelationQuery.ResourcePersonRelationQueryBuilder builder = new ResourcePersonRelationQuery.ResourcePersonRelationQueryBuilder();

		builder.setAuthorIndex(queryBuilder.getAuthorIndex())
				.setEnd(queryBuilder.getEnd())
				.setGroupByInterhash(queryBuilder.isGroupByInterhash())
				.setInterhash(queryBuilder.getInterhash())
				.setOrder(queryBuilder.getOrder())
				.setPersonId(queryBuilder.getPersonId())
				.setRelationType(queryBuilder.getRelationType())
				.setStart(queryBuilder.getStart())
				.setWithPersons(queryBuilder.isWithPersons())
				.setWithPersonsOfPosts(queryBuilder.isWithPersonsOfPosts())
				.setWithPosts(queryBuilder.isWithPosts());

		ResourcePersonRelationQuery query = builder.build();

		// TODO: maybe this should be done in the view?
		final List<ResourcePersonRelation> resourceRelations = this.logic.getResourceRelations(query);
		final List<ResourcePersonRelation> authorRelations = new ArrayList<>();
		final List<ResourcePersonRelation> advisorRelations = new ArrayList<>();
		final List<ResourcePersonRelation> otherAuthorRelations = new ArrayList<>();
		final List<ResourcePersonRelation> otherAdvisorRelations = new ArrayList<>();

		command.setHasPicture(this.pictureHandlerFactory.hasVisibleProfilePicture(linkedUser, command.getContext().getLoginUser()));

		for (final ResourcePersonRelation resourcePersonRelation : resourceRelations) {
			final Post<? extends BibTex> post = resourcePersonRelation.getPost();
			final BibTex publication = post.getResource();
			final boolean isThesis = publication.getEntrytype().toLowerCase().endsWith("thesis");
			final boolean isAuthorEditorRelation = PUBLICATION_RELATED_RELATION_TYPES.contains(resourcePersonRelation.getRelationType());

			if (isAuthorEditorRelation) {
				if (isThesis) {
					authorRelations.add(resourcePersonRelation);
				} else {
					otherAuthorRelations.add(resourcePersonRelation);
				}
			} else {
				if (isThesis) {
					advisorRelations.add(resourcePersonRelation);
				} else {
					otherAdvisorRelations.add(resourcePersonRelation);
				}
			}
			
			// we explicitly do not want ratings on the person pages because this might cause users of the genealogy feature to hesitate putting in their dissertations
			publication.setRating(null);
			publication.setNumberOfRatings(null);
		}
		
		command.setThesis(authorRelations);
		command.setOtherPubs(otherAuthorRelations);
		command.setAdvisedThesis(advisorRelations);
		// FIXME: not used in the view!!
		command.setOtherAdvisedPubs(otherAdvisorRelations);

		final List<PersonMatch> personMatches = this.logic.getPersonMatches(person.getPersonId());
		command.setPersonMatchList(personMatches);
		command.setMergeConflicts(PersonMatchUtils.getMergeConflicts(personMatches));

		/*
		 * get a list of post that could be also be written by the requested person
		 */
		final List<ResourcePersonRelation> similarAuthorRelations = new ArrayList<>();
		final List<Post<GoldStandardPublication>> similarAuthorPubs = this.getPublicationsOfSimilarAuthor(person);
		for (final Post<GoldStandardPublication> post : similarAuthorPubs) {
			final ResourcePersonRelation relation = new ResourcePersonRelation();
			relation.setPost(post);
			relation.setPersonIndex(PersonUtils.findIndexOfPerson(person, post.getResource()));
			relation.setRelationType(PersonUtils.getRelationType(person, post.getResource()));
			similarAuthorRelations.add(relation);
		}

		command.setSimilarAuthorPubs(similarAuthorRelations);

		return Views.PERSON_SHOW;
	}

	private List<Post<GoldStandardPublication>> getPublicationsOfSimilarAuthor(Person person) {
		final PostQuery<GoldStandardPublication> personNameQuery = new PostQueryBuilder().
						setPersonNames(person.getNames()).
						setOnlyIncludeAuthorsWithoutPersonId(true).
						setEnd(20) // get 20 "recommendations"
						.createPostQuery(GoldStandardPublication.class);
		return this.logic.getPosts(personNameQuery);
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
	}

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	public void setPersonRoleRenderer(PersonRoleRenderer personRoleRenderer) {
		this.personRoleRenderer = personRoleRenderer;
	}

	/**
	 * @param urlGenerator the urlGenerator to set
	 */
	public void setUrlGenerator(URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	/**
	 * Sets this controller's {@link PictureHandlerFactory} instance.
	 *
	 * @param factory
	 */
	public void setPictureHandlerFactory(final PictureHandlerFactory factory) {
		this.pictureHandlerFactory = factory;
	}

	/**
	 * @param crisCollege the crisCollege to set
	 */
	public void setCrisCollege(String crisCollege) {
		this.crisCollege = crisCollege;
	}
}


