package org.bibsonomy.model;

/**
 * @author dzo
 * @version $Id$
 */
public class GroupPublicationReportingSettings {
	
	private String reportingMailAddress;
	private String reportingMailTemplate;
	private String externalReportingUrl;

	/**
	 * @return the reportingMailAddress
	 */
	public String getReportingMailAddress() {
		return this.reportingMailAddress;
	}

	/**
	 * @param reportingMailAddress the reportingMailAddress to set
	 */
	public void setReportingMailAddress(String reportingMailAddress) {
		this.reportingMailAddress = reportingMailAddress;
	}
	
	/**
	 * @return the reportingMailTemplate
	 */
	public String getReportingMailTemplate() {
		return this.reportingMailTemplate;
	}

	/**
	 * @param reportingMailTemplate the reportingMailTemplate to set
	 */
	public void setReportingMailTemplate(String reportingMailTemplate) {
		this.reportingMailTemplate = reportingMailTemplate;
	}

	/**
	 * @return the externalReportingUrl
	 */
	public String getExternalReportingUrl() {
		return this.externalReportingUrl;
	}

	/**
	 * @param externalReportingUrl the externalReportingUrl to set
	 */
	public void setExternalReportingUrl(String externalReportingUrl) {
		this.externalReportingUrl = externalReportingUrl;
	}
}
