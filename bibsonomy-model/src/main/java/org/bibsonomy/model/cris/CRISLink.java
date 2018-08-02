package org.bibsonomy.model.cris;

import java.util.Date;

/**
 * this class represents the link of a publication to an Linkable object of the CRIS system
 *
 * @author dzo
 */
public class CRISLink {

	private Linkable source;

	private Linkable target;

	private Date startDate;

	private Date endDate;

	private CRISLinkType linkType;

	/**
	 * @return the source
	 */
	public Linkable getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Linkable source) {
		this.source = source;
	}

	/**
	 * @return the target
	 */
	public Linkable getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Linkable target) {
		this.target = target;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the linkType
	 */
	public CRISLinkType getLinkType() {
		return linkType;
	}

	/**
	 * @param linkType the linkType to set
	 */
	public void setLinkType(CRISLinkType linkType) {
		this.linkType = linkType;
	}
}
