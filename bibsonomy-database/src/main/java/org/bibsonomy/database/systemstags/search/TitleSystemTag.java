package org.bibsonomy.database.systemstags.search;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.TagUtils;

/**
 * @author sdo
 * @version $Id$
 */
public class TitleSystemTag extends AbstractSearchSystemTagImpl {

	/**
	 * the name of the title system tag
	 */
	public static final String NAME = "title";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TitleSystemTag newInstance() {
		return new TitleSystemTag();
	}

	@Override
	public void handleParam(final GenericParam param) {
		if(!present(param.getTitle()) ) {
			param.setTitle(this.getArgument());
		} else {
			// we append the new title part
			param.setTitle( param.getTitle() + TagUtils.getDefaultListDelimiter() + this.getArgument() );
		}
		param.setGrouping(GroupingEntity.ALL);
		log.debug("set title to " + param.getTitle() + " after matching for title system tag");
	}
	
	@Override
	public boolean allowsResource(final Class<? extends Resource> resourceType) {
		return true;
	}
}
