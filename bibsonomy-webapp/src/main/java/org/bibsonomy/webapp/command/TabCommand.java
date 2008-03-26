package org.bibsonomy.webapp.command;


/**
 * Bean for a single tab in a multiple tab context
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class TabCommand {
	
	/** The id of the tab */
	private Integer id;
	
	/** The title of the tab */
	private String title;		
	
	/**
	 * Constructor
	 * @param id ID
	 * @param title Title of tab
	 */
	public TabCommand(Integer id, String title) {
		this.id = id;
		this.title = title;
	}
	
	/**
	 * @return tab id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets tab id
	 * @param id tab id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return tab title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * sets the title of the tab
	 * @param title tab title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}