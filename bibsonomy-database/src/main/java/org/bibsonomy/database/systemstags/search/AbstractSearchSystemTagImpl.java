package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;

/**
 * @author sdo
 * @version $Id$
 */
public abstract class AbstractSearchSystemTagImpl extends AbstractSystemTagImpl implements SearchSystemTag{

	@Override
	public boolean isToHide() {
		return false;
	}

	@Override
	public boolean isInstance(String tagName) {
		return SystemTagsUtil.hasPrefixTypeAndArgument(tagName) && SystemTagsUtil.extractType(tagName).equals(this.getName());
	}

}
