package org.bibsonomy.webapp.controller;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.DisambiguationPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;

/**
 * @author Christian Pfeiffer
 */
public class DisambiguationPageController extends SingleResourceListController implements MinimalisticController<DisambiguationPageCommand> {
	//private static final Log log = LogFactory.getLog(DisambiguationPageController.class);
	
	@Override
	public DisambiguationPageCommand instantiateCommand() {
		return new DisambiguationPageCommand();
	}
	
	@Override
	public View workOn(final DisambiguationPageCommand command) {
		// TODO: make sure that goldstandard posts are preferred here
		command.setPost(this.logic.getPosts(BibTex.class, GroupingEntity.ALL, null, null, command.getRequestedHash(), null, null, null, null, null, null, 0, 100).get(0));
		if ("newPerson".equals(command.getRequestedAction())) {
			return newAction(command);
		}
		
		return disambiguateAction(command);
	}

	private View disambiguateAction(final DisambiguationPageCommand command) {
		final List<ResourcePersonRelation> matchingRelations = this.logic.getResourceRelations(command.getPost().getResource().getInterHash(), command.getRequestedRole(), new Integer(command.getRequestedIndex()));		
		if (matchingRelations.size() > 0 ) {
			return new ExtendedRedirectView(new URLGenerator().getPersonUrl(matchingRelations.get(0).getPerson().getId()));	
		}

		final PersonName requestedName = command.getPost().getResource().getAuthor().get(command.getRequestedIndex());
		command.setPersonName(requestedName);
		command.setSuggestedPersonNames(this.logic.getPersonSuggestion(requestedName.getLastName(), requestedName.getFirstName()));
		
		return Views.DISAMBIGUATION;
	}
	
	/**
	 * creates a new person, links te resource and redirects to the new person page
	 * @param command
	 * @return
	 */
	private View newAction(DisambiguationPageCommand command) {
		final BibTex bibtex = command.getPost().getResource();
		final List<PersonName> publicationNames = bibtex.getPersonNamesByRole(command.getRequestedRole());
		final int i = command.getRequestedIndex();
		if ((publicationNames == null) || (publicationNames.size() <= i)) {
			throw new IllegalArgumentException();
		}
		final PersonName mainName = publicationNames.get(i);
		final Person person = new Person();
		mainName.setMain(true);
		person.setMainName(mainName);
		this.logic.createOrUpdatePerson(person);
		command.setPerson(person);
		
		final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
		resourcePersonRelation.setPost(command.getPost());
		resourcePersonRelation.setRelationType(command.getRequestedRole());
		resourcePersonRelation.setPersonIndex(i);
		this.logic.addResourceRelation(resourcePersonRelation);
		
//		
//		JSONObject jsonPerson = new JSONObject();
//		jsonPerson.put("personId", person.getId());
//		jsonPerson.put("personName", person.getMainName().toString());
//		jsonPerson.put("personNameId", new Integer(person.getMainName().getId()));
//		jsonPerson.put("resourcePersonRelationId", new Integer(resourcePersonRelation.getId()));
//		
//		command.setResponseString(jsonPerson.toJSONString());
		
		return new ExtendedRedirectView(new URLGenerator().getPersonUrl(person.getId()));
	}
}


