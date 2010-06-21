package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;

/**
 * @author sdo
 * @version $Id$
 */
public class DaysSystemTag extends AbstractSystemTagImpl implements SearchSystemTag {

	public static final String NAME = "days";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public DaysSystemTag newInstance() {
		return new DaysSystemTag();
	}

	@Override
	public void handleParam(GenericParam param) {
		/*
		 * FIXME: What do we clear the TagIndex for, What is the TagIndes
		 */
		param.getTagIndex().clear();
		/*
		 * FIXME: How do we handle NumberFormatExceptions from parseInt
		 */
		param.setDays(Integer.parseInt(this.getArgument()));
		log.debug("set days to " + this.getArgument() + " after matching for days system tag");
	}

}
