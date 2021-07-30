package org.bibsonomy.webapp.command.cris;

import org.bibsonomy.model.Person;
import org.bibsonomy.webapp.command.EntitySearchAndFilterCommand;
import org.bibsonomy.webapp.command.ListCommand;

/**
 * command for the person overview page
 *
 * @author dzo
 */
public class PersonsPageCommand extends EntitySearchAndFilterCommand {

	private final ListCommand<Person> persons = new ListCommand<>(this);

	/** if true all persons are displayed (also persons that are not associated with the configured college */
	private boolean showAllPersons;

	/**
	 * @return the persons
	 */
	public ListCommand<Person> getPersons() {
		return persons;
	}

	/**
	 * @return the showAllPersons
	 */
	public boolean isShowAllPersons() {
		return showAllPersons;
	}

	/**
	 * @param showAllPersons the showAllPersons to set
	 */
	public void setShowAllPersons(boolean showAllPersons) {
		this.showAllPersons = showAllPersons;
	}

}
