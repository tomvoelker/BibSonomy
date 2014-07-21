package org.bibsonomy.webapp.command;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.bibsonomy.layout.jabref.JabrefLayout;
import org.bibsonomy.layout.standard.StandardLayout;
import org.bibsonomy.model.Layout;

/**
 * @author daill, lsc
 */
public class ExportPageCommand extends ResourceViewCommand{
	
	private Set<Layout> layoutSet;

	/**
	 * @return layout set
	 */
	public Set<Layout> getLayoutSet() {
		return this.layoutSet;
	}

	/**
	 * @param layoutSet
	 */
	public void setLayoutSet(final Set<Layout> layoutSet) {
		this.layoutSet = layoutSet;
	}
	
	/**
	 * @param layoutMap
	 */
	public void addJabrefLayoutMap(final Map<String, JabrefLayout> layoutMap) {
		for(Entry<String, JabrefLayout> layout : layoutMap.entrySet()){
			this.layoutSet.add(layout.getValue());
		}
	}
	
	/**
	 * @param layoutMap
	 */
	public void addStandardLayoutMap(final Map<String, StandardLayout> layoutMap) {
		for(Entry<String, StandardLayout> layout : layoutMap.entrySet()){
			this.layoutSet.add(layout.getValue());
		}
	}
	
}
