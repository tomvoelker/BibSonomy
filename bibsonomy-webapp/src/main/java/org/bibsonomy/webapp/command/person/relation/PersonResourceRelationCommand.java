package org.bibsonomy.webapp.command.person.relation;

import org.bibsonomy.model.Person;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * @author tok
 */
public class PersonResourceRelationCommand extends AjaxCommand<Void> {

	private Person person;

	private PersonResourceRelationType type;

	private String interhash;

	private int index = -1;

	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person the personId to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return the type
	 */
	public PersonResourceRelationType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(PersonResourceRelationType type) {
		this.type = type;
	}

	/**
	 * @return the interhash
	 */
	public String getInterhash() {
		return interhash;
	}

	/**
	 * @param interhash the interHash to set
	 */
	public void setInterhash(String interhash) {
		this.interhash = interhash;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}
}
