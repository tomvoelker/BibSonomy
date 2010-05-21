package org.bibsonomy.community.database.param;

public class BibTexParam extends org.bibsonomy.database.params.BibTexParam {
	private Integer communityId;
	private Integer runId;

	public void setCommunityId(Integer communityId) {
		this.communityId = communityId;
	}

	public Integer getCommunityId() {
		return communityId;
	}

	public void setRunId(Integer runId) {
		this.runId = runId;
	}

	public Integer getRunId() {
		return runId;
	}
}
