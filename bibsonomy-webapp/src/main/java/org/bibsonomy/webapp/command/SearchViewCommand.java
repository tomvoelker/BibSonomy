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
	
	private String searchmode = "all";
	
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

	/**
	 * @return the searchmode
	 */
	public String getSearchmode() {
		return this.searchmode;
	}

	/**
	 * @param searchmode the searchmode to set
	 */
	public void setSearchmode(String searchmode) {
		this.searchmode = searchmode;
	}
	
}
