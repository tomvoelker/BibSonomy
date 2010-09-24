package org.bibsonomy.rest.strategy.groups;

import java.io.ByteArrayOutputStream;
import java.io.Writer;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;
import org.bibsonomy.rest.util.EscapingPrintWriter;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetGroupStrategy extends Strategy {

	private final String groupName;
	private Writer writer;

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
		writer = new EscapingPrintWriter(outStream);
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