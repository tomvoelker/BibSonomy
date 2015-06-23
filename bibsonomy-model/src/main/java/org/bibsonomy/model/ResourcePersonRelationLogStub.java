package org.bibsonomy.model;

/**
 * TODO: add documentation to this class
 * 
 * @author jensi
 */
public class ResourcePersonRelationLogStub extends ResourcePersonRelationBase {
	private String personId;
	private String postInterhash;
	private boolean deleted;

	public String getPersonId() {
		return this.personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getPostInterhash() {
		return this.postInterhash;
	}

	public void setPostInterhash(String postInterhash) {
		this.postInterhash = postInterhash;
	}

	public boolean isDeleted() {
		return this.deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
