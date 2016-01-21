/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util;

import org.bibsonomy.webapp.view.constants.BootstrapAlertStyle;

/**
 * 
 * @author Sebastian Böttger <boettger@cs.uni-kassel.de>
 */
public class DidYouKnowMessage {

	/**
	 * 
	 */
	private String messageKey;
	
	/**
	 * 
	 */
	private BootstrapAlertStyle alertType;

	public DidYouKnowMessage() {
	}
	
	public DidYouKnowMessage(String messageKey, BootstrapAlertStyle alertType) {
		this.messageKey = messageKey;
		this.alertType = alertType;
	}
	
	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public BootstrapAlertStyle getAlertType() {
		return alertType;
	}

	public void setAlertType(BootstrapAlertStyle alertType) {
		this.alertType = alertType;
	}
	
	
	
}
