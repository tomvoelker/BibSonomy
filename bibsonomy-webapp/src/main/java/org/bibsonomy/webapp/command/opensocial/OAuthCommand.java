package org.bibsonomy.webapp.command.opensocial;

import net.oauth.OAuthConsumer;

import org.apache.shindig.social.opensocial.oauth.OAuthEntry;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * @author fei
 * @version $Id$
 */
public class OAuthCommand extends BaseCommand {
	public enum AuthorizeAction { Authorize, Deny };

	private String responseString;
	
	private AuthorizeAction authorizeAction;
	
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

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	public String getResponseString() {
		return responseString;
	}

	public void setAuthorizeAction(AuthorizeAction authorizeAction) {
		this.authorizeAction = authorizeAction;
	}

	public AuthorizeAction getAuthorizeAction() {
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

}
