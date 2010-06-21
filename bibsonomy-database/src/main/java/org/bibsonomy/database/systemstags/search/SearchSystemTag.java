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
	 * @return
	 */
	public SearchSystemTag newInstance();

	/**
	 * Sets or changes fields in a Param according to the systemTags function
	 */
	public void handleParam (GenericParam param);
}
