package org.bibsonomy.webapp.command.mock;

import java.util.List;

import org.bibsonomy.webapp.command.GroupingCommand;

/**
 * @author  dzo
 * @version  $Id$
 */
public class MockGroupingCommand implements GroupingCommand {
	private String abstractGrouping;
	private List<String> groups;
	
	/**
	 * @param abstractGrouping the abstractGrouping to set
	 */
	@Override
	public void setAbstractGrouping(String abstractGrouping) {
		this.abstractGrouping = abstractGrouping;
	}
	
	/**
	 * @return the abstractGrouping
	 */
	@Override
	public String getAbstractGrouping() {
		return abstractGrouping;
	}
	
	/**
	 * @param groups the groups to set
	 */
	@Override
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
	
	/**
	 * @return the groups
	 */
	@Override
	public List<String> getGroups() {
		return groups;
	}
}