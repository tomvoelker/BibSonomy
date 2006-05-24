/*
 * Created on 19.05.2006
 */
package org.bibsonomy.logic.model.entities;

import org.bibsonomy.db.model.Tag;

public class TagBean implements Tag {
	private String name;
	private Integer tagId;
	private Integer overallUsageCount;
	private Integer userUsageCount;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getOverallUsageCount() {
		return overallUsageCount;
	}
	public void setOverallUsageCount(Integer overallUsageCount) {
		this.overallUsageCount = overallUsageCount;
	}
	public Integer getTagId() {
		return tagId;
	}
	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}
	public Integer getUserUsageCount() {
		return userUsageCount;
	}
	public void setUserUsageCount(Integer userUsageCount) {
		this.userUsageCount = userUsageCount;
	}
}
