package org.bibsonomy.webapp.command;

/**
 * @author fba
 * @version $Id$
 */
public class GetUrlAjaxCommand extends AjaxCommand {
	private String pageTitle; 
	
	private String pageURL;
	
	private String pageDescription;
	
	private String pageKeywords;

	public String getPageTitle() {
		return this.pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getPageURL() {
		return this.pageURL;
	}

	public void setPageURL(String pageURL) {
		this.pageURL = pageURL;
	}

	public String getPageDescription() {
		return this.pageDescription;
	}

	public void setPageDescription(String pageDescription) {
		this.pageDescription = pageDescription;
	}

	public void setPageKeywords(String pageKeywords) {
		this.pageKeywords = pageKeywords;
	}

	public String getPageKeywords() {
		return pageKeywords;
	}

}
