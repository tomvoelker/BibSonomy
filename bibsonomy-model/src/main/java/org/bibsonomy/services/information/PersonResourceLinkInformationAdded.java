package org.bibsonomy.services.information;

import org.bibsonomy.common.information.JobInformation;
import org.bibsonomy.model.ResourcePersonRelation;

/**
 * info object indicating that the job added a person resource information
 *
 * @author dzo
 */
public class PersonResourceLinkInformationAdded implements JobInformation {

	private final ResourcePersonRelation relation;

	/**
	 * default constructor with the created relation
	 * @param relation
	 */
	public PersonResourceLinkInformationAdded(ResourcePersonRelation relation) {
		this.relation = relation;
	}

	/**
	 * @return the relation
	 */
	public ResourcePersonRelation getRelation() {
		return relation;
	}
}
