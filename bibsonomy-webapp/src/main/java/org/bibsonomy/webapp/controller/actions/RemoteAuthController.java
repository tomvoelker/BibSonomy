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
	
	/** logic to access request information */
	private RequestLogic requestLogic;
	/** preshared key for encryption */
	private String cryptKey;
	

	@Override
	public View workOn(RemoteAuthCommand command) {				
		/*
		 * works only if we have a referer
		 */
		final String reqUrl = command.getReqUrl();
		if (!present(reqUrl)) {
			throw new MalformedURLSchemeException("error.remote_login_without_requrl");
		}
		
		/*
		 * present login form to user, if not logged in
		 */
		if (!command.getContext().isUserLoggedIn()) {
			return new ExtendedRedirectView("/login?referer=/remoteAuth?reqUrl=" + reqUrl);
		}
		
		/*
		 * build auth key + auth URL
		 */
		
		BasicTextEncryptor crypt = new BasicTextEncryptor();
		crypt.setPassword(this.generatePassword());
		final String authData = "USER:" + command.getContext().getLoginUser().getName() + 
							    " " + 
							    "PWDHASH:" + command.getContext().getLoginUser().getPassword();
		final String authKey = UrlUtils.safeURIEncode(crypt.encrypt(authData));
		final String authUrl = UrlUtils.setParam(reqUrl, "authKey", authKey);
		command.setAuthUrl(authUrl);
		
		/*
		 * set remaining parameters
		 */
		command.setPageTitle("Remote Authentication");
		
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
	private String generatePassword() {
		LOGGER.debug("Creating password based on IP " + requestLogic.getHostInetAddress() + ", user agent " + requestLogic.getUserAgent());
		String base = requestLogic.getHostInetAddress() +
					  requestLogic.getUserAgent() + 
					  this.getCryptKey();
		LOGGER.debug("Password is: " + StringUtils.getMD5Hash(base));
		return StringUtils.getMD5Hash(base);
	}

}
