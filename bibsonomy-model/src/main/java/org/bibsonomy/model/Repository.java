package org.bibsonomy.model;

import java.util.Date;

/**
 * @author philipp
 * @version $Id$
 */
public class Repository {
	
	/**
	 * The name of the repository 
	 */
	private String id;
	
	/**
	 * The date where the attached post has been send to the repository
	 */
	private Date date;

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	

}
