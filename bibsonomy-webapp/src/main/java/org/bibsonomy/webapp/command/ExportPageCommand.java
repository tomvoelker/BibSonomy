package org.bibsonomy.webapp.command;

import java.util.Map;

import org.bibsonomy.layout.jabref.JabrefLayout;

/**
 * @author daill
 */
public class ExportPageCommand extends ResourceViewCommand{
	
	private Map<String, JabrefLayout> layoutMap;

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
