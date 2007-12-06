/*
 * Created on 08.10.2007
 */
package org.bibsonomy.webapp.view;

import org.bibsonomy.webapp.util.View;

/**
 * some symbols for views in the application
 * 
 * @author Jens Illig
 */
public enum Views implements View {
	/**
	 * the first page you see when entering the application
	 */
	HOMEPAGE("home"),
	
	/**
	 * user page displaying the resources of a single user
	 */
	USERPAGE("user");
	
	private final String name;
	private Views(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
