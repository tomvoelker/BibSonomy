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
package org.bibsonomy.webapp.command.opensocial;

import net.oauth.OAuthConsumer;

import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;
import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author fei
 */
public class OAuthCommand extends BaseCommand {
	public enum AuthorizeAction { Authorize, Deny };

	private String responseString;
	
	private String authorizeAction;
	
	private OAuthConsumer consumer;
	
	/** information about OAuth token and authorization */
	private OAuthEntry entry;
	
	/** consumer meta information */
	private String appTitle;
	/** consumer meta information */
	private String appDescription;
	/** consumer meta information */
	private String appIcon;
	/** consumer meta information */
	private String appThumbnail;
	/** call back URL */
	private String callBackUrl;
	
	private OAuthConsumerInfo consumerInfo;
	
	public KeyType[] getKeyTypes() {
		return KeyType.values();
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	public String getResponseString() {
		return responseString;
	}

	public void setAuthorizeAction(String authorizeAction) {
		this.authorizeAction = authorizeAction;
	}

	public String getAuthorizeAction() {
		return authorizeAction;
	}

	public void setConsumer(OAuthConsumer consumer) {
		this.consumer = consumer;
	}

	public OAuthConsumer getConsumer() {
		return consumer;
	}

	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
	}

	public String getAppTitle() {
		return appTitle;
	}

	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}

	public String getAppDescription() {
		return appDescription;
	}

	public void setAppIcon(String appIcon) {
		this.appIcon = appIcon;
	}

	public String getAppIcon() {
		return appIcon;
	}

	public void setAppThumbnail(String appThumbnail) {
		this.appThumbnail = appThumbnail;
	}

	public String getAppThumbnail() {
		return appThumbnail;
	}

	public void setEntry(OAuthEntry entry) {
		this.entry = entry;
	}

	public OAuthEntry getEntry() {
		return entry;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setConsumerInfo(OAuthConsumerInfo consumerInfo) {
		this.consumerInfo = consumerInfo;
	}

	public OAuthConsumerInfo getConsumerInfo() {
		return consumerInfo;
	}

}
