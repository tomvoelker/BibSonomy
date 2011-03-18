package org.bibsonomy.database.systemstags.markup;

import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.search.SearchSystemTag;

/**
 * @author sdo
 * @version $Id$
 */
public interface MarkUpSystemTag extends SystemTag {
	/**
	 * Creates a new instance of this kind of MarkUpSystemTag
	 * @return a new instance of a {@link SearchSystemTag} tag
	 */
	public MarkUpSystemTag newInstance();

}
