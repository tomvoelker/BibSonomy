package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.Resource;

/**
 * System tag for representing a tagged user relation, e.g., sys:relation:football
 * 
 * @author fmi
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
	public SearchSystemTag newInstance() {
		return new UserRelationSystemTag();
	}
	
	@Override
	public <T extends Resource> boolean allowsResource(Class<T> resourceType) {
		return true;
	}

	@Override
	public void handleParam(GenericParam param) {
		String tagName = SystemTagsUtil.buildSystemTagString(this.getName(), this.getArgument());
		param.addRelationTag(tagName);
	}

}
