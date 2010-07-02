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

	/**
	 * @return creationDate
	 */
	public Date getCreationDate() {
		return this.creationDate;
	}

	/**
	 * @param creationDate
	 */
	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return lowerTagName
	 */
	public String getLowerTagName() {
		return this.lowerTagName;
	}

	/**
	 * @param lowerTagName
	 */
	public void setLowerTagName(final String lowerTagName) {
		this.lowerTagName = lowerTagName;
	}

	/**
	 * @return ownerUserName
	 */
	public String getOwnerUserName() {
		return this.ownerUserName;
	}

	/**
	 * @param ownerUserName
	 */
	public void setOwnerUserName(final String ownerUserName) {
		this.ownerUserName = ownerUserName;
	}

	/**
	 * @return relationId
	 */
	public Integer getRelationId() {
		return this.relationId;
	}

	/**
	 * @param relationId
	 */
	public void setRelationId(final Integer relationId) {
		this.relationId = relationId;
	}

	/**
	 * @return upperTagName
	 */
	public String getUpperTagName() {
		return this.upperTagName;
	}

	/**
	 * @param upperTagName
	 */
	public void setUpperTagName(final String upperTagName) {
		this.upperTagName = upperTagName;
	}

	/**
	 * @return conceptStatus
	 */
	public ConceptStatus getConceptStatus() {
		return this.conceptStatus;
	}

	/**
	 * @param conceptStatus
	 */
	public void setConceptStatus(final ConceptStatus conceptStatus) {
		this.conceptStatus = conceptStatus;
	}
}