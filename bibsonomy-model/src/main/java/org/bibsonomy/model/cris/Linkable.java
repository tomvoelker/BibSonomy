package org.bibsonomy.model.cris;

/**
 * an interface that all classes must implement that can be linked to a publication
 * @author dzo
 */
public interface Linkable {

	/**
	 * @return the linkable id of the resource
	 */
	public String getLinkableId();
}
