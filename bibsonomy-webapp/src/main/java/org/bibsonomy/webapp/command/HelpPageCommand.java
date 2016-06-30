package org.bibsonomy.webapp.command;

import org.bibsonomy.webapp.command.actions.DownloadFileCommand;

/**
 * The command for the help pages and images.
 *
 * @author Johannes Blum
 */
public class HelpPageCommand extends DownloadFileCommand {

	private static final long serialVersionUID = -1480991183960187327L;

	/** The requested help page. */
	private String helpPage;
	
	/** The main content of the help page. */
	private String content;
	
	/** The content of the sidebar. */
	private String sidebar;
	
	/** <code>true</code> if the requested help page could not be found. */
	private boolean pageNotFound = false;

	/**
	 * @return the helpPage
	 */
	public String getHelpPage() {
		return this.helpPage;
	}

	/**
	 * @param helpPage the helpPage to set
	 */
	public void setHelpPage(String helpPage) {
		this.helpPage = helpPage;
	}
	
	/**
	 * @return the content of the requested helpPage
	 */
	public String getContent() {
		return content;
	}
	
	/**
	 * @param content The new content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the content of the sidebar
	 */
	public String getSidebar() {
		return this.sidebar;
	}
	
	/**
	 * @param sidebar The new content of the sidebar
	 */
	public void setSidebar(String sidebar) {
		this.sidebar = sidebar;
	}

	/**
	 * @return <code>true</code> if the requested file could not be found.
	 */
	public boolean isPageNotFound() {
		return this.pageNotFound;
	}

	/**
	 * @param pageNotFound the new value for pageNotFound
	 */
	public void setPageNotFound(boolean pageNotFound) {
		this.pageNotFound = pageNotFound;
	}

}
