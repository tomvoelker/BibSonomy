package org.bibsonomy.model.logic.query;

/**
 * Specifies a group query.
 * <p>
 * Depending on the settings, the query will be handled differently.
 * @author ada, pda
 */
public class GroupQuery implements Query {

	private final boolean pending;
	private final String userName;
	private final int start;
	private final int end;
	private final String externalId;

	/**
	 * Creates a group query.
	 *  @param pending    if set to <code>true</code> this query will retrieve pending groups, otherwise only activated groups will be retrieved.
	 * @param userName   if set the query is restricted to groups created by the user (applies only to pending groups).
	 * @param externalId if a valid non-empty string is supplied, the query will lookup the group with the supplied external id.
	 * @param start      start index of the retrieved result set.
	 * @param end        end index of the retrieved result set.
	 */
	public GroupQuery(final boolean pending, final String userName, final String externalId, int start, int end) {
		this.pending = pending;
		this.userName = userName;
		this.start = start;
		this.end = end;
		this.externalId = externalId;
	}

	/**
	 * @return the pending
	 */
	public boolean isPending() {
		return pending;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}
}
