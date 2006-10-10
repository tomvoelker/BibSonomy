/*
 * Created on 19.05.2006
 */
package org.bibsonomy.db.model;

public interface Tag {
	public String getName();
	public Integer getOverallUsageCount();
	public Integer getTagId();
	public Integer getUserUsageCount();
}
