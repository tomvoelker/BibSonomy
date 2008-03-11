package org.bibsonomy.database.params;

import java.util.Date;

import org.bibsonomy.common.enums.ConceptStatus;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class TagRelationParam extends GenericParam {

	private Integer relationId;
	private String lowerTagName;
	private String upperTagName;
	private Date creationDate;
	private String ownerUserName;
	private ConceptStatus conceptStatus;

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

	public ConceptStatus getConceptStatus() {
		return this.conceptStatus;
	}

	public void setConceptStatus(ConceptStatus conceptStatus) {
		this.conceptStatus = conceptStatus;
	}	
}