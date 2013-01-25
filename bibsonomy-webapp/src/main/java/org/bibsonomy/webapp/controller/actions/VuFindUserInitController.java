package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.common.enums.AuthMethod;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.VuFindUserInitCommand;
import org.bibsonomy.webapp.controller.opensocial.OAuthAuthorizeTokenController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.SpecialAuthMethodRequiredException;
import org.bibsonomy.webapp.util.spring.security.userattributemapping.SamlUserAttributeMapping;
import org.bibsonomy.webapp.validation.UserValidator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLCredential;


/**
 * This controller is called directly after the first vuFind login of a user.
 * It generates an oAuth-token request and ensures that there is a bibsonomy user for the remoteAuthentication (shibboleth) user. A new user is generated if required.
 * 
 * @author jensi
 *
 */
public class VuFindUserInitController implements MinimalisticController<VuFindUserInitCommand> {

	private OAuthAuthorizeTokenController oaAuthorizeController;
	
	private SamlUserAttributeMapping attributeExtractor;
	
	private LogicInterface adminLogic;
	
//	AuthenticationEntryPoint remoteAuthentication;
	
	@Override
	public VuFindUserInitCommand instantiateCommand() {
		return new VuFindUserInitCommand();
	}

	@Override
	public View workOn(VuFindUserInitCommand command) {
		// TODO: check if there's already a logged in user. If so, then we might like to ask the user whether he likes to directly connect his remote authentication to this user account.
		SAMLCredential samlCreds = getSamlCreds();
		SamlRemoteUserId remoteUserId = getRemoteUserId(samlCreds);
		if (remoteUserId == null) {
			throw new SpecialAuthMethodRequiredException(AuthMethod.SAML);
		}
		// TODO wohl falsch: zeug wie man user erzeugt steht in UserRegistrationController
		User user = new User();
		user.setRemoteUserId(remoteUserId);
		// Set additional Attributes
		attributeExtractor.populate(user, samlCreds);
		// user needs his Realname here
		if (user.getRealname() == null) {
			//Throw some exception
		}
		String userName = generateUserName(user, remoteUserId);
		if (userName == null) {
			//Throw some exception
		}
		user.setName(userName);
		user.setPassword("doof");
		this.adminLogic.createUser(user);

		// probably not needed (to be done in spring security filters):
		// remoteAuthentication.commence(command.getRe getRequest(), command.getResponse(), authException);
		
		
		// TODO:
		// - check if remoteuser maps to an existing bibsonomy user
		//   - if not: create one and add pair to the mapping
		// - login user but without any "stay logged in" cookie
		// automatically authorize via oauth (no extra button click)
		// If there is a logged-in user who is not already connected to the remoteuserid ignore him and create a new one
		return oaAuthorizeController.workOn(command);
	}

	private SamlRemoteUserId getRemoteUserId(SAMLCredential samlCreds) {
		if (samlCreds == null) {
			return null;
		}
		return attributeExtractor.getRemoteUserId(samlCreds);
	}

	private SAMLCredential getSamlCreds() {
		SAMLCredential samlCreds;
		SecurityContext ctx = SecurityContextHolder.getContext();
		Object creds = ctx.getAuthentication().getCredentials();
		if (creds instanceof SAMLCredential) {
			return (SAMLCredential) creds;
		}
		samlCreds = null;
		return samlCreds;
	}
	
	private String generateUserName(final User user, SamlRemoteUserId sruid) {
		/*
		 * Find user name which does not exist yet in the database.
		 * 
		 * check if username is already used and try another
		 */
		String newName = cleanUserName(user.getRealname());
		int tryCount = 0;
		//log.debug("try existence of username: " + newName);
		while ((newName.equalsIgnoreCase(this.adminLogic.getUserDetails(newName).getName())) && (tryCount < 101)) {
			try {
				if (tryCount == 0) {
					// try first character of forename concatenated with surname
					// bugs bunny => bbunny
					newName = cleanUserName(user.getRealname()).substring(0, 1).concat(newName);
				} else if (tryCount == 100) {
					// now use first character of fore- and first two characters of surename concatenated with user id 
					// bugs bunny => bbu01234567
					String remoteUserId = sruid.getUserId();
					newName = cleanUserName(newName.substring(0, 3).concat(remoteUserId));
				} else {
					// try first character of forename concatenated with surename concatenated with current number
					// bugs bunny => bbunnyX where X is between 1 and 9
					if (tryCount==1) {
						// add trycount to newName
						newName = cleanUserName(newName.concat(Integer.toString(tryCount)));
					} else { 
						// replace last two characters of string with trycount
						newName = cleanUserName(newName.substring(0, newName.length() - Integer.toString(tryCount-1).length()).concat(Integer.toString(tryCount)));
					}
				}
				//log.debug("try existence of username: " + newName + " (" + tryCount + ")");
				tryCount++;
			} catch (final IndexOutOfBoundsException ex) {
				/*
				 * if some substring values are out of range, catch exception and use surename
				 */
				newName = cleanUserName(user.getRealname());
				tryCount = 99;
			}
		}
		return newName;
	}
	
	private static String cleanUserName(final String name) {
		if (!ValidationUtils.present(name)) {
			return "";
		}
		return UserValidator.USERNAME_DISALLOWED_CHARACTERS_PATTERN.matcher(name).replaceAll("").toLowerCase();
	}
	

	/**
	 * @return the regular oAuth controller
	 */
	public OAuthAuthorizeTokenController getOaAuthorizeController() {
		return this.oaAuthorizeController;
	}

	/**
	 * @param oaReqTokenController the regular oAuth controller
	 */
	public void setOaAuthorizeController(OAuthAuthorizeTokenController oaReqTokenController) {
		this.oaAuthorizeController = oaReqTokenController;
	}

	/**
	 * @return the attributeExtractor
	 */
	public SamlUserAttributeMapping getAttributeExtractor() {
		return this.attributeExtractor;
	}

	/**
	 * @param attributeExtractor the attributeExtractor to set
	 */
	public void setAttributeExtractor(SamlUserAttributeMapping attributeExtractor) {
		this.attributeExtractor = attributeExtractor;
	}

	/**
	 * @return the adminLogic
	 */
	public LogicInterface getAdminLogic() {
		return this.adminLogic;
	}

	/**
	 * @param adminLogic the adminLogic to set
	 */
	public void setAdminLogic(LogicInterface adminLogic) {
		this.adminLogic = adminLogic;
	}
	
}
