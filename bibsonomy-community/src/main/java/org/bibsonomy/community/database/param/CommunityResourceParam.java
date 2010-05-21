package org.bibsonomy.community.database.param;

import org.bibsonomy.community.enums.Ordering;
import org.bibsonomy.database.params.ResourceParam;
import org.bibsonomy.model.Resource;

public class CommunityResourceParam<R extends Resource> extends ResourceParam<R> {
	private Integer runId;
	private Integer communityId;
	private Ordering ordering;
	private String communityDBName;

	public void setRunId(Integer runId) {
		this.runId = runId;
	}

	public Integer getRunId() {
		return runId;
	}

	public void setCommunityId(Integer communityId) {
		this.communityId = communityId;
	}

	public Integer getCommunityId() {
		return communityId;
	}

	public void setOrdering(Ordering ordering) {
		this.ordering = ordering;
	}

	public Ordering getOrdering() {
		return ordering;
	}

	public void setCommunityDBName(String communityDBName) {
		this.communityDBName = communityDBName;
	}

	public String getCommunityDBName() {
		return communityDBName;
	}
}
