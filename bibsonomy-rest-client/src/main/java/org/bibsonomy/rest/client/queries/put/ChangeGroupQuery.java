package org.bibsonomy.rest.client.queries.put;

import java.io.StringWriter;

import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to change details of an existing group in bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class ChangeGroupQuery extends AbstractQuery<String> {
	private final Group group;
	private final String groupName;

	/**
	 * Changes details of an existing group in bibsonomy.
	 * 
	 * @param groupName
	 *            name of the group to be changed
	 * @param group
	 *            new values
	 * @throws IllegalArgumentException
	 *             if groupname is null or empty, or if the group has no name
	 *             specified
	 */
	public ChangeGroupQuery(final String groupName, final Group group) throws IllegalArgumentException {
		if (groupName == null || groupName.length() == 0) throw new IllegalArgumentException("no groupName given");
		if (group == null) throw new IllegalArgumentException("no group specified");
		if (group.getName() == null || group.getName().length() == 0) throw new IllegalArgumentException("no groupname specified");

		this.groupName = groupName;
		this.group = group;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		RendererFactory.getRenderer(getRenderingFormat()).serializeGroup(sw, group, null);
		this.downloadedDocument = performRequest(HttpMethod.PUT, URL_GROUPS + "/" + this.groupName + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString());
		return null;
	}
	
	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess())
			return RendererFactory.getRenderer(getRenderingFormat()).parseGroupId(this.downloadedDocument); 
		return this.getError();
	}		
}