package org.bibsonomy.community.database.param;


public class BookmarkParam extends org.bibsonomy.database.params.BookmarkParam {
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
