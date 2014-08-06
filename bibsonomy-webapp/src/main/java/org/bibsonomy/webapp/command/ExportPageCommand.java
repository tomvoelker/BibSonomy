package org.bibsonomy.webapp.command;

import java.util.Map;

import org.bibsonomy.layout.jabref.AbstractJabRefLayout;
import org.bibsonomy.layout.standard.StandardLayout;
import org.bibsonomy.model.Layout;

/**
 * @author daill, lsc
 */
public class ExportPageCommand extends ResourceViewCommand {

	private Map<String, Layout> layoutMap;

	/**
	 * @return layout map
	 */
	public Map<String, Layout> getLayoutMap() {
		return this.layoutMap;
	}

	/**
	 * @param layoutMap
	 */
	public void setLayoutMap(final Map<String, Layout> layoutMap) {
		this.layoutMap = layoutMap;
	}

	/**
	 * @param jabrefMap
	 */
	public void addJabrefLayoutMap(
			final Map<String, AbstractJabRefLayout> jabrefMap) {
		for (AbstractJabRefLayout layout : jabrefMap.values()) {
			this.layoutMap.put(layout.getDisplayName(), layout);
		}
	}

	/**
	 * @param standardMap
	 */
	public void addStandardLayoutMap(
			final Map<String, StandardLayout> standardMap) {
		for (StandardLayout layout : standardMap.values()) {
			this.layoutMap.put(layout.getDisplayName(), layout);
		}
	}

}
