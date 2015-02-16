package org.bibsonomy.webapp.command;

import java.util.Map;
import java.util.Map.Entry;
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
	 * adds all maps o the layout map
	 * @param map
	 */
	public void addLayoutMap(final Map<String, ? extends Layout> map) {
		for (Entry<String, ? extends Layout> entry : map.entrySet()){
			this.layoutMap.put(entry.getValue().getDisplayName(), entry.getValue());
		}
		//this.layoutMap.putAll(map);
	}
	
	/**
	 * adds a layout to the layout map
	 * @param l 
	 */
	public void addLayout(Layout l) {
		this.layoutMap.put(l.getDisplayName(), l);
	}
}
