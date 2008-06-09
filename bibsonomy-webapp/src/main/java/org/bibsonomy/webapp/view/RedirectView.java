package org.bibsonomy.webapp.view;

import org.bibsonomy.webapp.util.View;

/**
 * @author rja
 * @version $Id$
 */
public class RedirectView implements View {

	private String redirectURI;
	
	public RedirectView(final String redirectURI) {
		this.redirectURI = redirectURI;
	}
	
	public String getName() {
		return "redirect:" + redirectURI;
	}

}
