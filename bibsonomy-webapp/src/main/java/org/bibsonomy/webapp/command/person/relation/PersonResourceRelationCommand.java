package org.bibsonomy.webapp.command.person.relation;

import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * @author tok
 */
public class PersonResourceRelationCommand extends AjaxCommand<Void> {

	private String personId;

	private PersonResourceRelationType type;

	private String interHash;

	private int index;

	/**
	 * @return the personId
	 */
	public String getPersonId() {
		return personId;
	}

	/**
	 * @param personId the personId to set
	 */
	public void setPersonId(String personId) {
		this.personId = personId;
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
	 * @return the interHash
	 */
	public String getInterHash() {
		return interHash;
	}

	/**
	 * @param interHash the interHash to set
	 */
	public void setInterHash(String interHash) {
		this.interHash = interHash;
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
