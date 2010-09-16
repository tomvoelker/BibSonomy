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
		
	/** logic to access request information */
	private RequestLogic requestLogic;
	/** preshared key for encryption */
	private String cryptKey;
	

	@Override
	public View workOn(RemoteAuthCommand command) {				
		String reqUrl = command.getReqUrl();
		if (!present(reqUrl)) {
			throw new MalformedURLSchemeException("error.remote_login_without_requrl");
		}
		
		/*
		 * present login form to user, if not logged in
		 */
		if (!command.getContext().isUserLoggedIn()) {
			String redirectURL = "/login";
			String referer = "/remoteAuth";
			referer = UrlUtils.setParam(referer, "reqUrl", reqUrl);
			if( present(command.getForwardPath()) ) {
				referer = UrlUtils.setParam(referer, "forwardPath", command.getForwardPath());
			}
			redirectURL = UrlUtils.setParam(redirectURL, "referer", UrlUtils.safeURIEncode(referer));
			return new ExtendedRedirectView(redirectURL/*"/login?referer=/remoteAuth?reqUrl=" + reqUrl*/);
		}
		
		/*
		 * build auth key + auth URL
		 */
		
		BasicTextEncryptor crypt = new BasicTextEncryptor();
		crypt.setPassword(this.generatePassword(command.getS()));
		final String authData = "USER:" + command.getContext().getLoginUser().getName() + 
							    " " + 
							    "APIKEY:" + command.getContext().getLoginUser().getApiKey() + 
							    " " + 
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
	 * generate a password to encrypt the authentication key.
	 * Currently, it is created by hashing some request headers - not too
	 * safe, but should suffice for now. 
	 * 
	 * @return - the generated password
	 */
	private String generatePassword(String secret) {
		LOGGER.debug("Generating password based on secret " + secret);
		String base = secret + this.getCryptKey();
		LOGGER.debug("Password is: " + StringUtils.getMD5Hash(base));
		return StringUtils.getMD5Hash(base);
	}
	
}
