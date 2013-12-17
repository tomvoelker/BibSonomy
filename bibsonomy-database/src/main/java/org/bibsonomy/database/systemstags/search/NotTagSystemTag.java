package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Resource;

/**
 * @author mba
 */
public class NotTagSystemTag extends AbstractSearchSystemTagImpl {
	
	public static final String NAME = "not";
	
	private String tagName;

	@Override
	public SearchSystemTag newInstance() {
		return new NotTagSystemTag();
	}

	@Override
	public void handleParam(GenericParam param) {
		param.addToSystemTags(this);
		tagName = this.getArgument();
	}

	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return this.tagName;
	}

	// Allow all resource types
	@Override
	public boolean allowsResource(Class<? extends Resource> resourceClass) {
		return true;
	}

	@Override
	public String getName() {
		return NAME;
	}

}
