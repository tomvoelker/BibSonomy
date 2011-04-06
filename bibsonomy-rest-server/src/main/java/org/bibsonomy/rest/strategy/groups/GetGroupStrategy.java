package org.bibsonomy.rest.strategy.groups;


import java.io.ByteArrayOutputStream;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetGroupStrategy extends Strategy {

	private final String groupName;

	/**
	 * @param context
	 * @param groupName
	 */
	public GetGroupStrategy(final Context context, final String groupName) {
		super(context);
		this.groupName = groupName;
	}

	@Override
	public void perform(final ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException {
		// delegate to the renderer
		final Group group = this.getLogic().getGroupDetails(this.groupName);
		if (group == null) {
			throw new NoSuchResourceException("The requested group '" + this.groupName + "' does not exist.");
		}
		this.getRenderer().serializeGroup(writer, group, new ViewModel());
	}

	@Override
	public String getContentType() {
		return "group";
	}
}