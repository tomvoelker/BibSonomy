package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.Resource;

/**
 * System tag for representing a tagged user relation, e.g., sys:relation:football
 * 
 * @author fmi
 * @version $Id$
 */
public class UserRelationSystemTag extends AbstractSystemTagImpl implements SearchSystemTag {

	public static final String NAME = "relation";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isToHide() {
		return true;
	}

	@Override
	public boolean isInstance(final String tagName) {
		return SystemTagsUtil.hasPrefixTypeAndArgument(tagName) && SystemTagsUtil.extractType(tagName).equals(this.getName());
	}

	//------------------------------------------------------------------------
	// SearchSystemTag interface
	//------------------------------------------------------------------------
	@Override
	public UserRelationSystemTag newInstance() {
		return new UserRelationSystemTag();
	}
	
	@Override
	public boolean allowsResource(final Class<? extends Resource> resourceType) {
		return true;
	}

	@Override
	public void handleParam(final GenericParam param) {
		final String tagName = SystemTagsUtil.buildSystemTagString(this.getName(), this.getArgument());
		param.addRelationTag(tagName);
	}

}
