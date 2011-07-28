package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;

/**
 * @author sdo
 * @version $Id$
 */
public abstract class AbstractSearchSystemTagImpl extends AbstractSystemTagImpl implements SearchSystemTag {

	protected static boolean isPublicationClass(final Class<? extends Resource> clazz) {
		return clazz != null && BibTex.class.isAssignableFrom(clazz);
	}
	
	@Override
	public boolean isToHide() {
		return false;
	}

	@Override
	public boolean isInstance(final String tagName) {
		return SystemTagsUtil.hasPrefixTypeAndArgument(tagName) && SystemTagsUtil.extractType(tagName).equals(this.getName());
	}
}
