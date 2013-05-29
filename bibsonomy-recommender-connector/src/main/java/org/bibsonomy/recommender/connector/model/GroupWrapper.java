package org.bibsonomy.recommender.connector.model;

import org.bibsonomy.model.Group;

public class GroupWrapper implements recommender.core.interfaces.model.RecommendationGroup {

	/**
	 * for persistence
	 */
	private static final long serialVersionUID = -720572125586893099L;

	private Group group;
	
	public GroupWrapper(Group group) {

		this.group = group;
		
	}
	
	public Group getGroup() {
		return group;
	}
	
	public void setGroup(Group group) {
		this.group = group;
	}
}
