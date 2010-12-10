package org.bibsonomy.webapp.command.actions;

import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * @author philipp
 * @version $Id$
 */
public class PublicationClassificationCommand extends AjaxCommand {

	private String classificationName = "";
	
	private String id = "";
	
	/**
	 * @param name
	 */
	public void setClassificationName(String name) {
		this.classificationName = name;
	}
	
	/**
	 * @return  classification name
	 */
	public String getClassificationName() {
		return this.classificationName;
	}
	
	/**
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return id
	 */
	public String getId() {
		return this.id;
	}
	
	
	
}
