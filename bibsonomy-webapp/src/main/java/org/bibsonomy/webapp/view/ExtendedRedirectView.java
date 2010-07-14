package org.bibsonomy.webapp.view;

import org.bibsonomy.webapp.util.View;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author rja
 * @version $Id$
 */
public class ExtendedRedirectView extends RedirectView implements View {

	/** Constructs a new redirect view.
	 * 
	 * @param redirectURI
	 */
	public ExtendedRedirectView(final String redirectURI) {
		super(redirectURI);
		setExposeModelAttributes(false);
	}
	
	@Override
	public String getName() {
		return getUrl();
	}

}
