package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Resource;

/**
 * @author sdo
 * @version $Id$
 */
public class UserSystemTag extends AbstractSearchSystemTagImpl {

	/**
	 * the name of the user system tag
	 */
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
	public void handleParam(final GenericParam param) {
		param.setGrouping(GroupingEntity.USER);
		param.setRequestedUserName(this.getArgument());
		log.debug("set grouping to 'user' and requestedUserName to " + this.getArgument() + " after matching for user system tag");
	}

	@Override
	public boolean allowsResource(final Class<? extends Resource> resourceType) {
		return true;
	}

}
