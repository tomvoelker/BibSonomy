/*
 * Created on 08.10.2007
 */
package org.bibsonomy.webapp.view;

import org.bibsonomy.webapp.util.View;

public enum Views implements View {
	HOMEPAGE("home");
	
	private final String name;
	private Views(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
