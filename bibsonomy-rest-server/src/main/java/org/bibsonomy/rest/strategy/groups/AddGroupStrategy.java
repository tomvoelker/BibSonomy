package org.bibsonomy.rest.strategy.groups;

import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class AddGroupStrategy extends AbstractCreateStrategy {
	
	/**
	 * @param context
	 */
	public AddGroupStrategy(final Context context) {
		super(context);
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}

	@Override
	protected String create() throws InternServerException {
		final Group group = this.getRenderer().parseGroup(this.doc);
		return this.getLogic().createGroup(group);
	}

	@Override
	protected void render(Writer writer, String groupID) {
		this.getRenderer().serializeGroupId(writer, groupID);		
	}
}