package org.bibsonomy.webapp.command.reporting;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.webapp.command.EntitySearchAndFilterCommand;

/**
 * base command for reporting pages
 *
 * @author pda
 */
public abstract class ReportingCommand extends EntitySearchAndFilterCommand {
	private String downloadFormat;

	private Person person;
	private Group organization;

	public String getDownloadFormat() {
		return downloadFormat;
	}

	public void setDownloadFormat(String downloadFormat) {
		this.downloadFormat = downloadFormat;
	}

	public abstract String getFilename();

	/**
	 * @return the person
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * @return the organization
	 */
	public Group getOrganization() {
		return organization;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(Group organization) {
		this.organization = organization;
	}
}
