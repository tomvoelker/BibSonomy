package org.bibsonomy.webapp.command;

import java.util.Map;

import org.bibsonomy.layout.jabref.JabrefLayout;

/**
 * @author daill
 * @version $Id$
 */
public class ExportPageCommand extends ResourceViewCommand{
	
	private Map<String, JabrefLayout> layoutMap;
	private String lang;

	/**
	 * @return language code
	 */
	public String getLang() {
		return this.lang;
	}

	/**
	 * @param lang
	 */
	public void setLang(final String lang) {
		this.lang = lang;
	}

	/**
	 * @return jabref layout map
	 */
	public Map<String, JabrefLayout> getLayoutMap() {
		return this.layoutMap;
	}

	/**
	 * @param layoutMap
	 */
	public void setLayoutMap(final Map<String, JabrefLayout> layoutMap) {
		this.layoutMap = layoutMap;
	}

	
}
