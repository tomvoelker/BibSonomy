package org.bibsonomy.database.params.person;

/**
 * @author dzo
 */
public class GetPersonByOrganizationParam {
	private final String organizationName;
	private final int limit;
	private final int offset;

	/**
	 * default constructor
	 * @param organizationName
	 * @param limit
	 * @param offset
	 */
	public GetPersonByOrganizationParam(String organizationName, int limit, int offset) {
		this.organizationName = organizationName;
		this.limit = limit;
		this.offset = offset;
	}

	/**
	 * @return the organizationName
	 */
	public String getOrganizationName() {
		return organizationName;
	}

	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}
}
