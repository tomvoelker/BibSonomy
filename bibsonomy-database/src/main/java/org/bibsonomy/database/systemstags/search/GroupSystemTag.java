package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Resource;

/**
 * @author sdo
 * @version $Id$
 */
public class GroupSystemTag extends AbstractSearchSystemTagImpl {

	/**
	 * the name of the group system tag
	 */
	public static final String NAME = "group";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public GroupSystemTag newInstance() {
		return new GroupSystemTag();
	}

	@Override
	public void handleParam(final GenericParam param) {
		param.setGrouping(GroupingEntity.GROUP);
		param.setRequestedGroupName(this.getArgument());
		log.debug("set grouping to 'group' and requestedGroupName to " + this.getArgument() + " after matching for group system tag");
	}

	@Override
	public boolean allowsResource(final Class<? extends Resource> resourceType) {
		return true;
	}

}
