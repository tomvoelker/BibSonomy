package org.bibsonomy.rest.client.queries.post;

import java.io.StringWriter;

import org.bibsonomy.rest.client.AbstractQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;
import org.bibsonomy.util.StringUtils;

/**
 * @author wla
 * @version $Id$
 */
public class CreateReferenceQuery extends AbstractQuery<String> {

	private final static String URL_PREFIX = "/posts/community/";
	private final static String URL_POSTFIX = "/references";

	private final String hash;
	private final String referenceHash;

	public CreateReferenceQuery(final String hash, final String referenceHash) {
		this.hash = hash;
		this.referenceHash = referenceHash;
	}

	@Override
	protected String doExecute() throws ErrorPerformingRequestException {
		final StringWriter sw = new StringWriter(100);
		this.getRenderer().serializeReference(sw, this.referenceHash);

		this.downloadedDocument = performRequest(HttpMethod.POST, URL_PREFIX + hash + URL_POSTFIX, StringUtils.toDefaultCharset(sw.toString()));

		return null;
	}

}
