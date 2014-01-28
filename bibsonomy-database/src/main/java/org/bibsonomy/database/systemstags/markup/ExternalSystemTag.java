package org.bibsonomy.database.systemstags.markup;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;

/**
 * The External System Tag marks a post relevant for some external system. 
 * For example the tag sys:external:vufind would marks posts that are created through the system VuFind.
 * @author sdo
 */
public class ExternalSystemTag extends AbstractSystemTagImpl implements MarkUpSystemTag {

	public static final String NAME = "external";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isToHide() {
		return true;
	}


	@Override
	public ExternalSystemTag newInstance() {
		return new ExternalSystemTag();
	}

	@Override
	public boolean isInstance(final String tagName) {
		return SystemTagsUtil.hasPrefixTypeAndArgument(tagName) && NAME.equals(SystemTagsUtil.extractType(tagName));
	}

}
