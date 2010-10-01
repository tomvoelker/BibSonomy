package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.RemoteAuthCommand;
import org.bibsonomy.webapp.exceptions.MalformedURLSchemeException;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.jasypt.util.text.BasicTextEncryptor;


/**
 * Controller for remote Authentication. This controller basically creates 
 * a backlink to the requesting page with a encrypted version of username + hashed password.
 * 
 * @author Dominik Benz
 * @version $Id$
 */          
public class RemoteAuthController implements MinimalisticController<RemoteAuthCommand>, RequestAware {

	private static final Log LOGGER = LogFactory.getLog(RemoteAuthController.class);
	
	/** nr. of minutes the generated key is valid */
	private static final int KEY_VALIDITY = 20;
	
	/** the URL to conferator (a URL we trust) */
	private static final String CONFERATOR_URL = "^http\\:\\/\\/conferator\\.org\\/.*$";
		
	/** logic to access request information */
	private RequestLogic requestLogic;
	/** preshared key for encryption */
	private String cryptKey;
	

	@Override
	public View workOn(RemoteAuthCommand command) {				
		final String reqUrl = command.getReqUrl();
		if (!present(reqUrl)) {
			throw new MalformedURLSchemeException("error.remote_login_without_requrl");
		}
		
		/*
		 * present login form to user, if not logged in
		 */
		if (!command.getContext().isUserLoggedIn()) {
			/*
			 * Generate referer URL
			 */
			String referer = UrlUtils.setParam("/remoteAuth", "reqUrl", reqUrl);
			if (present(command.getForwardPath())) {
				referer = UrlUtils.setParam(referer, "forwardPath", command.getForwardPath());
			}
			/*
			 * send redirect
			 */
			return new ExtendedRedirectView(UrlUtils.setParam("/login", "referer", UrlUtils.safeURIEncode(UrlUtils.setParam(referer, "s", command.getS() ) ) ) );
		}
		
		/*
		 * build auth key + auth URL
		 */
		final BasicTextEncryptor crypt = new BasicTextEncryptor();
		crypt.setPassword(this.generatePassword(command.getS()));
		
		final String authData = "USER:" + command.getContext().getLoginUser().getName() + " " + 
							    "APIKEY:" + command.getContext().getLoginUser().getApiKey() + " " + 
							    "TIME:" + System.currentTimeMillis();
		
		final String authKey = UrlUtils.safeURIEncode(crypt.encrypt(authData));
		String authUrl = UrlUtils.setParam(reqUrl, "authKey", authKey);
		if( present(command.getForwardPath()) ) {
			authUrl = UrlUtils.setParam(authUrl, "forwardPath", command.getForwardPath());
		}
		command.setAuthUrl(authUrl);
		
		/*
		 * set remaining parameters
		 */
		command.setPageTitle("Remote Authentication");
		command.setIp(requestLogic.getHostInetAddress());
		command.setValidPeriod(KEY_VALIDITY);
		
		/*
		 * if the auth URL comes from conferator, redirect directly
		 */
		if (authUrl.matches(CONFERATOR_URL)) {
			return new ExtendedRedirectView(authUrl);
		}
		
		/*
		 * return view
		 */		
		return Views.REMOTE_AUTH;
	}

	@Override
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;	
	}
	
	public String getCryptKey() {
		return this.cryptKey;
	}

	public void setCryptKey(String cryptKey) {
		this.cryptKey = cryptKey;
	}

	@Override
	public RemoteAuthCommand instantiateCommand() {
		return new RemoteAuthCommand();
	}	
	
	/**
	 * Generate a password to encrypt the authentication key. 
	 * 
	 * 
	 * @return - the generated password
	 */
	private String generatePassword(String secret) {
		return StringUtils.getMD5Hash(secret + this.getCryptKey());
	}
	
}
