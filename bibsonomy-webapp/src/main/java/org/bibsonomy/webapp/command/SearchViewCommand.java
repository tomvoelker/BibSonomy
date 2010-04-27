package org.bibsonomy.webapp.command;

/**
 * 
 * Bean for providing the search string
 * 
 * @author Beate Krause
 * @version $Id$
 */
public class SearchViewCommand extends TagResourceViewCommand{
	
	/** String to search for */
	private String requestedSearch = "";

	/**
	 * sets the requested search string
	 * @param requestedSearch
	 */
	public void setRequestedSearch(String requestedSearch){
		this.requestedSearch=requestedSearch; 
	}
		
	/**
	 * @return the requested search string
	 */
	public String getRequestedSearch() {
		return requestedSearch;
	}
	
}
