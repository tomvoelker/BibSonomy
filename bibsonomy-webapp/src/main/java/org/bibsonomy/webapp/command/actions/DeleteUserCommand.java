package org.bibsonomy.webapp.command.actions;

import java.io.Serializable;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author daill
 * @version $Id$
 */
public class DeleteUserCommand extends BaseCommand implements Serializable {

	private static final long serialVersionUID = 952301302153030500L;
	
	private String delete;

	/**
	 * @return String
	 */
	public String getDelete() {
		return this.delete;
	}

	/**
	 * @param delete
	 */
	public void setDelete(String delete) {
		this.delete = delete;
	}
	
	

}
