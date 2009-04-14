package org.bibsonomy.webapp.command;

import java.util.HashMap;

import org.bibsonomy.layout.jabref.JabrefLayout;

/**
 * @author daill
 * @version $Id$
 */
public class ExportPageCommand extends ResourceViewCommand{
	
	private HashMap<String, JabrefLayout> layoutMap;
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
	public HashMap<String, JabrefLayout> getLayoutMap() {
		return this.layoutMap;
	}

	/**
	 * @param layoutMap
	 */
	public void setLayoutMap(final HashMap<String, JabrefLayout> layoutMap) {
		this.layoutMap = layoutMap;
	}

	
}
