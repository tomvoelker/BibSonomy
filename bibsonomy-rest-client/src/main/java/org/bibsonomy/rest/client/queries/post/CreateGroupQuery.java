package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.model.Group;
import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.client.exception.ErrorPerformingRequestException;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;

/**
 * Use this Class to create a new group in bibsonomy.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public final class CreateGroupQuery extends AbstractQuery<String> {
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
	protected String doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		RendererFactory.getRenderer(getRenderingFormat()).serializeGroup(sw, this.group, null);
		this.downloadedDocument = performRequest(HttpMethod.POST, URL_GROUPS + "?format=" + getRenderingFormat().toString().toLowerCase(), sw.toString());
		return null;
	}
	
	@Override
	public String getResult() throws BadRequestOrResponseException, IllegalStateException {
		if (this.isSuccess())
			return RendererFactory.getRenderer(getRenderingFormat()).parseGroupId(this.downloadedDocument); 
		return this.getError();
	}		
}