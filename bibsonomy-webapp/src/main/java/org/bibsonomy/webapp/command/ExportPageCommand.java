package org.bibsonomy.webapp.command;

import java.util.Map;

import org.bibsonomy.layout.jabref.AbstractJabRefLayout;

/**
 * @author daill
 */
public class ExportPageCommand extends ResourceViewCommand{
	
	private Map<String, AbstractJabRefLayout> layoutMap;

	/**
	 * @return jabref layout map
	 */
	public Map<String, AbstractJabRefLayout> getLayoutMap() {
		return this.layoutMap;
	}

	/**
	 * @param layoutMap
	 */
	public void setLayoutMap(final Map<String, AbstractJabRefLayout> layoutMap) {
		this.layoutMap = layoutMap;
	}
}
