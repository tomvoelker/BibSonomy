package org.bibsonomy.rest.strategy.groups;

import java.io.Writer;

import org.bibsonomy.common.enums.GroupUpdateOperation;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id: UpdateGroupDetailsStrategy.java,v 1.5 2007/04/15 11:05:07 mbork
 *          Exp $
 */
public class UpdateGroupDetailsStrategy extends AbstractUpdateStrategy {
	private final String groupName;

	public UpdateGroupDetailsStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}

	@Override
	protected void render(Writer writer, String groupID) {
		this.getRenderer().serializeGroupId(writer, groupID);	
	}

	@Override
	protected String update() throws InternServerException {
		// ensure right groupname
		final Group group = this.getRenderer().parseGroup(this.doc);
		group.setName(this.groupName);
		return this.getLogic().updateGroup(group, GroupUpdateOperation.UPDATE_ALL);
	}
}