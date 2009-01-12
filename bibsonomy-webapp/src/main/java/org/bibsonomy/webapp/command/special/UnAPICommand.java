package org.bibsonomy.webapp.command.special;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * 
 * The data of a UnAPI request. See <a href="http://unapi.info">UnAPI.info</a>.
 * 
 * Represents a post (by an id) in a certain format.
 * 
 * @author rja
 * @version $Id$
 */
public class UnAPICommand extends BaseCommand {

	private String id;
	private String format;
	
	/**
	 * @return The requested id of the post.
	 */
	public String getId() {
		return this.id;
	}
	/** 
	 * @param id - the id of the post.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return The requested format.
	 */
	public String getFormat() {
		return this.format;
	}
	/**
	 * @param format - the format the post should be returned.
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
}
