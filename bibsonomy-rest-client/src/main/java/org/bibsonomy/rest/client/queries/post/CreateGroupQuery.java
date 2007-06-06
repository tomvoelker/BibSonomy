package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to create a new group in bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class CreateGroupQuery extends AbstractQuery<String> {

	private boolean executed = false;
	private String result;
	private final Group group;

	/**
	 * Creates a new group account in bibsonomy.
	 * 
	 * @param group
	 *            the group to be created
	 * @throws IllegalArgumentException
	 *             if the group has no name is defined
	 */
	public CreateGroupQuery(final Group group) throws IllegalArgumentException {
		if (group == null) throw new IllegalArgumentException("no group specified");
		if (group.getName() == null || group.getName().length() == 0) throw new IllegalArgumentException("no groupname specified");

		this.group = group;
	}

	@Override
	public String getResult() {
		if (!this.executed) throw new IllegalStateException("Execute the query first.");
		return this.result;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		this.executed = true;
		final StringWriter sw = new StringWriter(100);
		RendererFactory.getRenderer(getRenderingFormat()).serializeGroup(sw, this.group, null);
		this.result = performRequest(HttpMethod.POST, URL_GROUPS + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString());
	}
}