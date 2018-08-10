package org.bibsonomy.database.params;

import org.bibsonomy.database.common.enums.CRISEntityType;
import org.bibsonomy.model.cris.CRISLink;

import java.util.Date;

/**
 * @author dzo
 */
public class CRISLinkParam extends GenericParam {

	private CRISLink link;

	private int linkableId;

	private CRISEntityType sourceType;

	private CRISEntityType targetType;

	private int sourceId;

	private int targetId;

	private String updatedBy;

	private Date updatedAt;

	/**
	 * @return the link
	 */
	public CRISLink getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(CRISLink link) {
		this.link = link;
	}

	/**
	 * @return the linkableId
	 */
	public int getLinkableId() {
		return linkableId;
	}

	/**
	 * @param linkableId the linkableId to set
	 */
	public void setLinkableId(int linkableId) {
		this.linkableId = linkableId;
	}

	/**
	 * @return the sourceType
	 */
	public CRISEntityType getSourceType() {
		return sourceType;
	}

	/**
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(CRISEntityType sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * @return the targetType
	 */
	public CRISEntityType getTargetType() {
		return targetType;
	}

	/**
	 * @param targetType the targetType to set
	 */
	public void setTargetType(CRISEntityType targetType) {
		this.targetType = targetType;
	}

	/**
	 * @return the sourceId
	 */
	public int getSourceId() {
		return sourceId;
	}

	/**
	 * @param sourceId the sourceId to set
	 */
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	/**
	 * @return the targetId
	 */
	public int getTargetId() {
		return targetId;
	}

	/**
	 * @param targetId the targetId to set
	 */
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the updatedAt
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
}
