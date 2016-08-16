package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.SimpleResourceViewCommand;

/**
 * publication autocomplete
 *
 * @author dzo
 */
public class PublicationAutocompleteCommand extends SimpleResourceViewCommand {
	
	private String search;

	/**
	 * @return the search
	 */
	public String getSearch() {
		return this.search;
	}

	/**
	 * @param search the search to set
	 */
	public void setSearch(String search) {
		this.search = search;
	}
}
