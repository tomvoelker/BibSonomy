package org.bibsonomy.database.systemstags.markup;
import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;

/**
 * @author sdo
 * @version $Id$
 */
public class JabrefSystemTag extends AbstractSystemTagImpl implements MarkUpSystemTag {

	public static final String NAME = "jabref";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isToHide() {
		/*
		 * TODO: store this String elsewhere (comes form our jabref-plugin)
		 */
		if (present(this.getArgument()) && "noKeywordAssigned".equals(this.getArgument())) {
			return true;
		}
		return false;
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