package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.systemstags.SystemTag;


/**
 * @author dzo
 * @version $Id$
 */
public interface SearchSystemTag extends SystemTag {

	/**
	 * Creates a new instance of this kind of SearchSystemTag
	 * @return a new instance of a {@link SearchSystemTag} tag
	 */
	public SearchSystemTag newInstance();

	/**
	 * Sets or changes fields in a param according to the systemTags function
	 * @param param 
	 */
	public void handleParam(GenericParam param);
}
