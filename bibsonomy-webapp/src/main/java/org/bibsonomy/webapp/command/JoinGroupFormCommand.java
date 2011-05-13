package org.bibsonomy.webapp.command;


/**
 * @author schwass
 * @version $Id$
 */
public  class JoinGroupFormCommand extends BaseCommand {

	private String group;

	/**
	 * @param group
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return name of group user want join
	 */
	public String getGroup() {
		return group;
	}
}
