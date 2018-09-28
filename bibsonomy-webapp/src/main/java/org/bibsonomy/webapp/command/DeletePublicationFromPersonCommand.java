package org.bibsonomy.webapp.command;


import org.bibsonomy.model.Person;

public class DeletePublicationFromPersonCommand extends BaseCommand {

	private Person person;

	private String typeToDelete;

	private String interhashToDelete;

	private String indexToDelete;

	/**
	 * @return
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return
	 */
	public String getTypeToDelete() {
		return typeToDelete;
	}

	/**
	 * @param typeToDelete
	 */
	public void setTypeToDelete(String typeToDelete) {
		this.typeToDelete = typeToDelete;
	}

	/**
	 * @return
	 */
	public String getInterhashToDelete() {
		return interhashToDelete;
	}

	/**
	 * @param interhashToDelete
	 */
	public void setInterhashToDelete(String interhashToDelete) {
		this.interhashToDelete = interhashToDelete;
	}

	/**
	 * @return
	 */
	public String getIndexToDelete() {
		return indexToDelete;
	}

	/**
	 * @param indexToDelete
	 */
	public void setIndexToDelete(String indexToDelete) {
		this.indexToDelete = indexToDelete;
	}
}
