package org.bibsonomy.database.systemstags.markup;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;

/**
 * @author sdo
 * @version $Id$
 */
public class UnfiledSystemTag  extends AbstractSystemTagImpl implements MarkUpSystemTag {

	public static final String NAME = "unfiled";
	private static boolean toHide = true;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isToHide() {
		return toHide;
	}

	@Override
	public MarkUpSystemTag newInstance() {
		return new UnfiledSystemTag();
	}

	@Override
	public boolean isInstance(final String tagName) {
		return SystemTagsUtil.hasPrefixAndType(tagName) && NAME.equals(SystemTagsUtil.extractType(tagName));
	}

}
