package org.bibsonomy.rest.strategy.groups;

import java.io.Reader;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class AddGroupStrategy extends Strategy {
	private final Reader doc;
	
	public AddGroupStrategy(final Context context) {
		super(context);
		this.doc = context.getDocument();
	}

	@Override
	public void perform(final Writer writer) throws InternServerException {
		final Group group = this.getRenderer().parseGroup(this.doc);
		this.getLogic().createGroup(group);
	}

	@Override
	public String getContentType() {
		// TODO no content-contenttype
		return null;
	}
}