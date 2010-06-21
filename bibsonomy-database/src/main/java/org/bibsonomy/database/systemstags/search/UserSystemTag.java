package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;

/**
 * @author sdo
 * @version $Id$
 */
public class UserSystemTag extends AbstractSystemTagImpl implements
		SearchSystemTag {

	public static final String NAME = "user";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public UserSystemTag newInstance() {
		return new UserSystemTag();
	}

	@Override
	public void handleParam(GenericParam param) {
		param.setGrouping(GroupingEntity.USER);
		param.setRequestedUserName(this.getArgument());
		log.debug("set grouping to 'user' and requestedUserName to " + this.getArgument() + " after matching for user system tag");
	}


/*	public boolean isUserSystemTag(final String tagName) {
		return SystemTagsUtil.
	}*/
}
