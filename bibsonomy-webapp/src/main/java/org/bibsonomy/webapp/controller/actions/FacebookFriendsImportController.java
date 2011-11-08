package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.entity.UserRealnameResolver;
import org.bibsonomy.model.User;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.actions.FacebookAccessCommand;
import org.bibsonomy.webapp.command.actions.OAuthAccessCommand;
import org.bibsonomy.webapp.command.actions.OAuthAccessCommand.State;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.importer.FacebookFriendsImporter;
import org.bibsonomy.webapp.util.importer.AbstractFriendsImporter.UserAdapter;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.validation.Errors;

/**
 * controller for importing friends from facebook via facebook's graph api
 * 
 * TODO: implement error handling
 * TODO: store access tokens for later offline usage 
 * TODO: ckey?
 * 
 * @author fei
 * @version $Id$
 */
public class FacebookFriendsImportController implements ErrorAware, MinimalisticController<FacebookAccessCommand> {
	private final static Log log = LogFactory.getLog(FacebookFriendsImportController.class);
	
	private Errors errors = null;

	private final static String CALL_BACK = "import/facebook";
	private final static String FB_OAUTH_REQUEST_URL = "https://www.facebook.com/dialog/oauth";
	private final static String FB_OAUTH_ACCESSS_URL = "https://graph.facebook.com/oauth/access_token";
	private final static String FB_OAUTH_SCOPE= "user_about_me,user_birthday,user_website,user_hometown,user_interests,email,friends_interests,friends_about_me,friends_interests,friends_website,friends_birthday,friends_hometown";
	private static final String OAUTH_ACCESS_TOKEN = "access_token";
	private static final String OAUTH_ERROR_TYPE = "type";
	private static final String OAUTH_ERROR_MESSAGE = "message";
	private static final String OAUTH_ERROR_KEY = "error";

	/** facebook's api key */
	private String fbApiKey;
	/** facebook's api secret */
	private String fbSecret;

	/** project for call back requests */
	private String projectHome;
	
	/** resolves imported facebook user to BibSonomy users */
	private UserRealnameResolver friendsResolver;

	@Override
	public FacebookAccessCommand instantiateCommand() {
		return new FacebookAccessCommand();
	}

	@Override
	public View workOn(FacebookAccessCommand command) {
		final RequestWrapperContext context = command.getContext();
		if (!context.isUserLoggedIn()) {
			throw new AccessDeniedException();
		}

		//if (!context.isValidCkey()) {
		//errors.reject("error.field.valid.ckey");
		//}
		
		//
		// perform administrative tasks (if requested)
		//
		if (present(command.getAdminAction())) {
			return this.performAdminAction(command, context);
		}

		//
		// import friends 
		//
		switch (command.getState()) {
		case ACCESS:
			return this.oAuthAccessToken(command, context.getLoginUser());
		default: 
			return this.oAuthRequestToken(command, context.getLoginUser());
		}
	}

	/**
	 * obtain a (temporary) request token from facebook
	 * 
	 * @param command
	 * @param user
	 * @return
	 */
	private View oAuthRequestToken(OAuthAccessCommand command, User user) {
		log.debug("obtaining a request token for user '"+user.getName()+"' from facebook");

		// url to call when authorization dialog finished
		String callbackURI = this.getProjectHome()+CALL_BACK+"/"+UrlUtils.safeURIEncode(State.ACCESS.name());

		// url for obtaining the request token
		String redirectURI =  UrlUtils.setParam(FB_OAUTH_REQUEST_URL, "client_id", UrlUtils.safeURIEncode(this.getFbApiKey()));
		redirectURI =  UrlUtils.setParam(redirectURI, "redirect_uri", UrlUtils.safeURIEncode(callbackURI));
		redirectURI =  UrlUtils.setParam(redirectURI, "scope", UrlUtils.safeURIEncode(FB_OAUTH_SCOPE));

		// redirect
		return new ExtendedRedirectView(redirectURI);
	}

	/**
	 * obtain a authorized access token
	 * 
	 * @param command
	 * @param user the login user
	 * @return
	 */
	private View oAuthAccessToken(FacebookAccessCommand command, User user) {
		if (present(command.getError())) {
			return Views.FACEBOOK_IMPORT;
		}

		// url to call when authorization dialog finished
		String callbackURI = this.getProjectHome()+CALL_BACK+"/"+UrlUtils.safeURIEncode(State.ACCESS.name());

		// url for authorizing the request token
		String redirectURI =  UrlUtils.setParam(FB_OAUTH_ACCESSS_URL, "client_id", UrlUtils.safeURIEncode(this.getFbApiKey()));
		redirectURI =  UrlUtils.setParam(redirectURI, "redirect_uri", UrlUtils.safeURIEncode(callbackURI));
		redirectURI =  UrlUtils.setParam(redirectURI, "client_secret", UrlUtils.safeURIEncode(this.getFbSecret()));
		redirectURI =  UrlUtils.setParam(redirectURI, "code", UrlUtils.safeURIEncode(command.getCode()));
		
		String accessToken = this.getAccessToken(redirectURI, command);
		command.setAccessToken(accessToken);
		
		if (present(accessToken)) {
			Collection<User> facebookFriends = this.importFacebookFriends(command, user);
			Map<String, Collection<User>> userMapping = this.friendsResolver.resolveUsers(facebookFriends);
			command.setFriends(facebookFriends);
			command.setUserMapping(userMapping);
		}

		return Views.FACEBOOK_IMPORT;
	}

	/**
	 * get friends from facebook
	 * 
	 * @param command
	 * @param loginUser
	 * @return
	 */
	private Collection<User> importFacebookFriends(FacebookAccessCommand command, User loginUser) {
		
		FacebookFriendsImporter friendsImporter = new FacebookFriendsImporter();
		UserAdapter<com.restfb.types.User> userAdapter = friendsImporter.getUserAdapter();
		
		// retrieve list of friends from facebook
		loginUser.setApiKey(command.getAccessToken());
		Collection<com.restfb.types.User> facebookFriends = friendsImporter.getFriends(loginUser);
		
		// map facebook users to bibsonomy users
		Collection<User> bibsonomyFriends = new ArrayList<User>();
		if (present(facebookFriends)) {
			for (com.restfb.types.User facebookUser : facebookFriends ) {
				bibsonomyFriends.add(userAdapter.getUser(facebookUser));
			}
		}
		
		return bibsonomyFriends;
	}
	

	/**
	 * read json object from given url
	 * 
	 * @param url the url to fetch the response object from
	 * @param command the model object
	 * @return the obtained access token on success, null otherwise
	 */
    public String getAccessToken(String url, OAuthAccessCommand command) {
		HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());

		// Create a method instance.
		GetMethod method = new GetMethod(url);

		// Provide custom retry handler is necessary
		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
		
		// the raw response string
		String responseString = null;
		// the access token
		String accessToken = null;
		try {
			// Execute the method.
			int statusCode = client.executeMethod(method);

			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("Method failed: " + method.getStatusLine());
			}
			
			// the raw response string
			InputStreamReader input = new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8");
			
			responseString = convertStreamToString(input);
			
			if (present(responseString) && responseString.startsWith(OAUTH_ACCESS_TOKEN)) {
				// accessToken = responseString.split("=", 2)[1];
				Map<String,String> response = parseQueryString(responseString);
				accessToken = response.get(OAUTH_ACCESS_TOKEN);
				// TODO: also get "expires" and store both in the database
			} else {
				this.parseOAuthError(responseString, command);
			}
		} catch (IOException e) {
			log.error("Error reading OAuth response.", e);
		} finally {
			// Release the connection.
			method.releaseConnection();
		}  
		
		return accessToken;
    }

    /**
     * perform administrative tasks like building the user index
     * 
     * @param command
     * @param context
     * @return
     */
	private View performAdminAction(FacebookAccessCommand command, RequestWrapperContext context) {
		final User loginUser = context.getLoginUser();

		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("please log in as admin");
		}
		
		switch (command.getAdminAction()) {
		case BUILD_INDEX:
			if (!present(this.friendsResolver)) {
				throw new RuntimeException("No user index configured");
			}
			this.friendsResolver.buildIndex();
			return Views.FACEBOOK_IMPORT;
		default:
			throw new RuntimeException("Unsupported admin action: '"+command.getAdminAction()+"'");
		}
	}

	/**
	 * parse the error response string and set the corresponding properties 
	 * in the given command
	 * 
	 * @param response
	 * @param command
	 */
	private void parseOAuthError(String response, OAuthAccessCommand command) {
		// the parsed response object
		JSONObject jsonResponse;
		try {
			jsonResponse = new JSONObject(response);
			final JSONObject errorObject = jsonResponse.getJSONObject(OAUTH_ERROR_KEY);
			command.setErrorType(OAuthAccessCommand.ErrorType.valueOf(errorObject.getString(OAUTH_ERROR_TYPE)));
			command.setErrorMessage(errorObject.getString(OAUTH_ERROR_MESSAGE));
		} catch (JSONException e) {
			log.error("Error parsing json response string '"+response+"'", e);
		}
	}
	
	/**
	 * convert InputStream to String object
	 * 
	 * @param is
	 * @return
	 */
	private static String convertStreamToString(InputStreamReader is) {
        BufferedReader reader = new BufferedReader(is);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            log.error("Error reading OAuth response.", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                log.error("Error closing connection while reading OAuth response", e);
            }
        }

        return sb.toString();
    }
	
	
	/**
	 * parse a query string into a key value map
	 * 
	 * TODO: move to {@link UrlUtils}?
	 * 
	 * @param query the query string
	 * @return key value map 
	 */
	public static Map<String, String> parseQueryString(String query) {  
	    String[] params = query.split("&");  
	    Map<String, String> map = new HashMap<String, String>();  
	    for (String param : params) {  
	        String name = param.split("=")[0];  
	        String value = param.split("=")[1];  
	        map.put(name, value);  
	    }  
	    return map;  
	}  
	
	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @param fbApiKey
	 */
	public void setFbApiKey(String fbApiKey) {
		this.fbApiKey = fbApiKey;
	}

	/**
	 * @return the facebook api key
	 */
	public String getFbApiKey() {
		return fbApiKey;
	}

	/**
	 * @param fbSecret
	 */
	public void setFbSecret(String fbSecret) {
		this.fbSecret = fbSecret;
	}

	/**
	 * @return the facebook api secret key
	 */
	public String getFbSecret() {
		return fbSecret;
	}

	/**
	 * @param projectHome
	 */
	public void setProjectHome(String projectHome) {
		this.projectHome = projectHome;
	}

	/**
	 * @return project home
	 */
	public String getProjectHome() {
		return projectHome;
	}

	/**
	 * @param friendsResolver
	 */
	public void setFriendsResolver(UserRealnameResolver friendsResolver) {
		this.friendsResolver = friendsResolver;
	}

	/**
	 * @return the applied imported friends resolver
	 */
	public UserRealnameResolver getFriendsResolver() {
		return friendsResolver;
	}	

}
