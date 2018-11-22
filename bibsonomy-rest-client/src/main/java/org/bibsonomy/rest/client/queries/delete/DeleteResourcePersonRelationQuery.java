package org.bibsonomy.rest.client.queries.delete;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.rest.client.AbstractDeleteQuery;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.exceptions.ErrorPerformingRequestException;

/**
 * delete query for a {@link org.bibsonomy.model.ResourcePersonRelation}
 *
 * @author dzo
 */
public class DeleteResourcePersonRelationQuery extends AbstractDeleteQuery {
	private final String personId;
	private final String interHash;
	private final int index;
	private final PersonResourceRelationType type;

	/**
	 * default constructor with all required fields
	 * @param personId
	 * @param interHash
	 * @param index
	 * @param type
	 */
	public DeleteResourcePersonRelationQuery(String personId, String interHash, int index, PersonResourceRelationType type) {
		if (!present(personId)) {
			throw new IllegalArgumentException("no person id given");
		}

		if (!present(interHash)) {
			throw new IllegalArgumentException("no interhash given");
		}

		if (!present(type)) {
			throw new IllegalArgumentException("no type given");
		}
		this.personId = personId;
		this.interHash = interHash;
		this.index = index;
		this.type = type;
	}

	@Override
	protected void doExecute() throws ErrorPerformingRequestException {
		final String personResourceRelationUrl = this.getUrlRenderer().createUrlBuilderForPersonResourceRelation(this.personId, this.interHash, this.index, this.type).asString();
		this.downloadedDocument = performRequest(HttpMethod.DELETE, personResourceRelationUrl, null);
	}
}
