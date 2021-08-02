package org.bibsonomy.layout.citeproc.renderer;

import de.undercouch.citeproc.LocaleProvider;

/**
 * TODO: add documentation to this class
 *
 * @author ???
 */
public class LanguageFile implements LocaleProvider {

	private String locale;

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Override
	public String retrieveLocale(String lang) {
		return locale;
	}
}
