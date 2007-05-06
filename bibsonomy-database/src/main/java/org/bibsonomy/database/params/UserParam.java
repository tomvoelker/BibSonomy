package org.bibsonomy.database.params;

/**
 * Parameters that are specific to users.
 *
 * @author Miranda Grahl
 * @version $Id$
 */
public class UserParam extends GenericParam {

	private String groupingName;

	public String getGroupingName() {
		return this.groupingName;
	}

	public void setGroupingName(String groupingName) {
		this.groupingName = groupingName;
	}
}