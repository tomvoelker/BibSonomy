package org.bibsonomy.webapp.controller;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonIdType;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.exception.LogicException;
import org.bibsonomy.model.logic.exception.ResourcePersonAlreadyAssignedException;
import org.bibsonomy.model.logic.querybuilder.PersonSuggestionQueryBuilder;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.services.person.PersonRoleRenderer;
import org.bibsonomy.webapp.command.DisambiguationPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Pfeiffer
 */
public class DisambiguationPageController extends SingleResourceListController implements MinimalisticController<DisambiguationPageCommand> {
	//private static final Log log = LogFactory.getLog(DisambiguationPageController.class);
	
	/**
	 * put into the session to tell the personPageController that the person has just been created
	 */
	public static final String ACTION_KEY_CREATE_AND_LINK_PERSON = "createAndLinkPerson";
	public static final String ACTION_KEY_LINK_PERSON = "linkPerson";
	
	protected RequestLogic requestLogic;
	private PersonRoleRenderer personRoleRenderer;
	
	@Override
	public DisambiguationPageCommand instantiateCommand() {
		final DisambiguationPageCommand command = new DisambiguationPageCommand();
		command.setPersonRoleRenderer(personRoleRenderer);
		return command;
	}
	
	@Override
	public View workOn(final DisambiguationPageCommand command) {
		// TODO: make sure that goldstandard posts are preferred here
		command.setPost(this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, command.getRequestedHash(), null, null, null, null, null, null, 0, 100).get(0));
		if ("newPerson".equals(command.getRequestedAction())) {
			return newAction(command);
		} else if ("linkPerson".equals(command.getRequestedAction())) {
			return linkAction(command);
		}
		
		return disambiguateAction(command);
	}

	private View disambiguateAction(final DisambiguationPageCommand command) {
		final List<ResourcePersonRelation> matchingRelations = this.logic.getResourceRelations().byInterhash(command.getPost().getResource().getInterHash()).byRelationType(command.getRequestedRole()).byAuthorIndex(command.getRequestedIndex()).getIt();		
		if (matchingRelations.size() > 0 ) {
			return new ExtendedRedirectView(new URLGenerator().getPersonUrl(matchingRelations.get(0).getPerson().getPersonId()));	
		}

		final PersonName requestedName = command.getPost().getResource().getAuthor().get(command.getRequestedIndex());
		command.setPersonName(requestedName);
		
		String name = requestedName.toString();
		PersonSuggestionQueryBuilder query = this.logic.getPersonSuggestion(name).withEntityPersons(true).withRelationType(PersonResourceRelationType.values());
		command.setPersonSuggestions(query.doIt());
		
		return Views.DISAMBIGUATION;
	}
	
	
/**
	 * creates a new person, links te resource and redirects to the new person page
	 * @param command
	 * @return
	 */
	private View newAction(DisambiguationPageCommand command) {
		final Person person = createPersonEntity(command);
		try {
			linkToPerson(command, person);
		} catch (LogicException e) {
			command.getLogicExceptions().add(e);
			return disambiguateAction(command);
		}
		
		
//		
//		JSONObject jsonPerson = new JSONObject();
//		jsonPerson.put("personId", person.getId());
//		jsonPerson.put("personName", person.getMainName().toString());
//		jsonPerson.put("personNameId", new Integer(person.getMainName().getId()));
//		jsonPerson.put("resourcePersonRelationId", new Integer(resourcePersonRelation.getId()));
//		
//		command.setResponseString(jsonPerson.toJSONString());
		
		this.requestLogic.setLastAction(ACTION_KEY_CREATE_AND_LINK_PERSON);
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(person.getPersonId()));
	}

	private Person createPersonEntity(DisambiguationPageCommand command) {
		final PersonName mainName = getMainPersonName(command);
		
		final Person person = new Person();
		mainName.setMain(true);
		person.setMainName(mainName);
		this.logic.createOrUpdatePerson(person);
		command.setPerson(person);
		return person;
	}

	private void linkToPerson(DisambiguationPageCommand command, final Person person) throws ResourcePersonAlreadyAssignedException {
		final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		resourcePersonRelation.setPerson(person);
		resourcePersonRelation.setPost(command.getPost());
		resourcePersonRelation.setRelationType(command.getRequestedRole());
		resourcePersonRelation.setPersonIndex(command.getRequestedIndex());
		this.logic.addResourceRelation(resourcePersonRelation);
	}

	private static PersonName getMainPersonName(DisambiguationPageCommand command) {
		final BibTex bibtex = command.getPost().getResource();
		final List<PersonName> publicationNames = bibtex.getPersonNamesByRole(command.getRequestedRole());
		final int i = command.getRequestedIndex();
		if ((publicationNames == null) || (publicationNames.size() <= i)) {
			throw new IllegalArgumentException();
		}
		final PersonName mainName = publicationNames.get(i);
		return mainName;
	}
	
	
	/**
	 * creates a new person, links the resource and redirects to the new person page
	 * @param command
	 * @return
	 */
	private View linkAction(DisambiguationPageCommand command) {
		final Person person = this.logic.getPersonById(PersonIdType.BIBSONOMY_ID, command.getRequestedPersonId());
		try {
			linkToPerson(command, person);
		} catch (LogicException e) {
			command.getLogicExceptions().add(e);
			return disambiguateAction(command);
		}
		this.requestLogic.setLastAction(ACTION_KEY_LINK_PERSON);
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(person.getPersonId()));
	}

	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	public void setPersonRoleRenderer(PersonRoleRenderer personRoleRenderer) {
		this.personRoleRenderer = personRoleRenderer;
	}
}


