/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

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
