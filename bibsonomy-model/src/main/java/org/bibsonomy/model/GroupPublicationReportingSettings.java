/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.model;

/**
 * @author dzo
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
