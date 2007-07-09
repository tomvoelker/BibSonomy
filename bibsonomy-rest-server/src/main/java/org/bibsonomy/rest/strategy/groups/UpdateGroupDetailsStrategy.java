package org.bibsonomy.rest.strategy.groups;

import java.io.Reader;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id: UpdateGroupDetailsStrategy.java,v 1.5 2007/04/15 11:05:07 mbork
 *          Exp $
 */
public class UpdateGroupDetailsStrategy extends Strategy {
	private final Reader doc;
	private final String groupName;

	public UpdateGroupDetailsStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
		this.doc = context.getDocument();
	}

	@Override
	public void perform(final Writer writer) throws InternServerException {
		// ensure right groupname
		final Group group = this.getRenderer().parseGroup(this.doc);
		group.setName(this.groupName);
		this.getLogic().updateGroup(group);
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}
}