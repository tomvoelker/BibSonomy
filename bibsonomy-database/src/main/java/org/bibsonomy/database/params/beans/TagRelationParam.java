/*
 * Created on 07.06.2007
 */
package org.bibsonomy.database.params.beans;

import java.util.Date;

public class TagRelationParam {
	private Integer relationId;
	private String lowerTagName;
	private String upperTagName;
	private Date creationDate;
	private String ownerUserName;
	
	public Date getCreationDate() {
		return this.creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public String getLowerTagName() {
		return this.lowerTagName;
	}
	public void setLowerTagName(String lowerTagName) {
		this.lowerTagName = lowerTagName;
	}
	public String getOwnerUserName() {
		return this.ownerUserName;
	}
	public void setOwnerUserName(String ownerUserName) {
		this.ownerUserName = ownerUserName;
	}
	public Integer getRelationId() {
		return this.relationId;
	}
	public void setRelationId(Integer relationId) {
		this.relationId = relationId;
	}
	public String getUpperTagName() {
		return this.upperTagName;
	}
	public void setUpperTagName(String upperTagName) {
		this.upperTagName = upperTagName;
	}
}
