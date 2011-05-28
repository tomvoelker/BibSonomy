package org.bibsonomy.webapp.command;

import java.util.List;

/**
 * @author dzo
 * @version $Id$
 */
public interface GroupingCommand {

	/**
	 * @return the abstractGrouping
	 */
	public String getAbstractGrouping();

	/**
	 * @param abstractGrouping the abstractGrouping to set
	 */
	public void setAbstractGrouping(String abstractGrouping);

	/**
	 * @return the groups
	 */
	public List<String> getGroups();

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<String> groups);

}