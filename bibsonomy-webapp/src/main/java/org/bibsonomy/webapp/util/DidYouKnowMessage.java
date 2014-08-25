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
