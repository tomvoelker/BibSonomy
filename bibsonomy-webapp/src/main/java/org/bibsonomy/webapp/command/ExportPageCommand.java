package org.bibsonomy.webapp.command;

import java.util.Map;
import java.util.TreeMap;

import org.bibsonomy.model.Layout;

/**
 * @author daill, lsc
 */
public class ExportPageCommand extends ResourceViewCommand {

	private Map<String, Layout> layoutMap;
	
	public ExportPageCommand() {
		this.layoutMap = new TreeMap<>();
	}

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

	public void addLayoutMap(final Map<String, ? extends Layout> map) {
		for (Layout layout: map.values()) {
			// TODO: Check by which keys the values are saved in map, maybe a simple
			// putAll is sufficient?
			this.layoutMap.put(layout.getDisplayName(), layout);
		}
	}
}
