package org.bibsonomy.webapp.controller.cris;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonOrder;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.PersonQuery;
import org.bibsonomy.services.filesystem.ProfilePictureLogic;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.cris.PersonsPageCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * the controller for
 * - /persons
 *
 * if the system is configured to cris mode
 *
 * @author dzo
 */
public class PersonsPageController implements MinimalisticController<PersonsPageCommand> {

	private LogicInterface logicInterface;
	private String crisCollege;
	private ProfilePictureLogic profilePictureLogic;

	@Override
	public PersonsPageCommand instantiateCommand() {
		final PersonsPageCommand personsPageCommand = new PersonsPageCommand();
		personsPageCommand.getPersons().setEntriesPerPage(30);
		return personsPageCommand;
	}

	@Override
	public View workOn(final PersonsPageCommand command) {
		final boolean isUserLoggedin = command.getContext().isUserLoggedIn();

		final ListCommand<Person> personListCommand = command.getPersons();
		final String search = command.getSearch();
		final PersonQuery query = new PersonQuery(search);
		query.setUsePrefixMatch(true);
		query.setPrefix(command.getPrefix());
		final int personListStart = personListCommand.getStart();
		query.setStart(personListStart);
		query.setEnd(personListStart + personListCommand.getEntriesPerPage());
		query.setOrder(present(search) ? null : PersonOrder.MAIN_NAME_LAST_NAME);
		query.setUsePrefixMatch(true);
		if (!isUserLoggedin || !command.isShowAllPersons()) {
			query.setCollege(this.crisCollege);
		}

		final List<Person> persons = this.logicInterface.getPersons(query);
		personListCommand.setList(persons);

		List<String> personsWithProfilePicture = new ArrayList<>();
		for (Person person : persons) {
			String username = person.getUser();
			if (present(username) && profilePictureLogic.hasProfilePicture(username)) {
				personsWithProfilePicture.add(username);
			}
		}
		command.setPersonsWithProfilePicture(personsWithProfilePicture);

		return Views.PERSON_INTRO;
	}

	/**
	 * @param logicInterface the logicInterface to set
	 */
	public void setLogicInterface(LogicInterface logicInterface) {
		this.logicInterface = logicInterface;
	}

	/**
	 * @param crisCollege the crisCollege to set
	 */
	public void setCrisCollege(String crisCollege) {
		this.crisCollege = crisCollege;
	}

	public void setProfilePictureLogic(ProfilePictureLogic profilePictureLogic) {
		this.profilePictureLogic = profilePictureLogic;
	}

}
