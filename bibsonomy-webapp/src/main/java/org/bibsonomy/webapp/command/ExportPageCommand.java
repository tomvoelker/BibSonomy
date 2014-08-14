package org.bibsonomy.webapp.command;

import java.util.Map;
import java.util.TreeMap;

import org.bibsonomy.model.Layout;

/**
 * @author daill, lsc
 */
public class ExportPageCommand extends ResourceViewCommand {

	private Map<String, Layout> layoutMap;
	
	/**
	 * default constructor
	 */
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
	
	/**
	 * adds all maps the the layout map
	 * @param map
	 */
	public void addLayoutMap(final Map<String, ? extends Layout> map) {
		this.layoutMap.putAll(map);
	}
}
