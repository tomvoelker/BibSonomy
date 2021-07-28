package org.bibsonomy.database.params.group;

/**
 * param to insert group parent relations
 *
 * @author ada
 */
public class InsertParentRelations {

	private int parentGroupId;
	private int childGroupId;

	/**
	 * default constructor
	 * @param parentGroupId
	 * @param childGroupId
	 */
	public InsertParentRelations(int parentGroupId, int childGroupId) {
		this.parentGroupId = parentGroupId;
		this.childGroupId = childGroupId;
	}

	/**
	 * @return the parentGroupId
	 */
	public int getParentGroupId() {
		return parentGroupId;
	}

	/**
	 * @param parentGroupId the parentGroupId to set
	 */
	public void setParentGroupId(int parentGroupId) {
		this.parentGroupId = parentGroupId;
	}

	/**
	 * @return the childGroupId
	 */
	public int getChildGroupId() {
		return childGroupId;
	}

	/**
	 * @param childGroupId the childGroupId to set
	 */
	public void setChildGroupId(int childGroupId) {
		this.childGroupId = childGroupId;
	}
}
