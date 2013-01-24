package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.common.enums.AuthMethod;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.webapp.command.VuFindUserInitCommand;
import org.bibsonomy.webapp.controller.opensocial.OAuthAuthorizeTokenController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.SpecialAuthMethodRequiredException;
import org.bibsonomy.webapp.util.spring.security.userattributemapping.SamlUserAttributeExtraction;
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
	
	private SamlUserAttributeExtraction attributeExtractor;
	
//	AuthenticationEntryPoint remoteAuthentication;
	
	@Override
	public VuFindUserInitCommand instantiateCommand() {
		return new VuFindUserInitCommand();
	}

	@Override
	public View workOn(VuFindUserInitCommand command) {
		// TODO: check if there's already a logged in user. If so, then we might like to ask the user whether he likes to directly connect his remote authentication to this user account.
		SamlRemoteUserId remoteUserId = getRemoteUserId();
		if (remoteUserId == null) {
			throw new SpecialAuthMethodRequiredException(AuthMethod.SAML);
		}
		
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

	private SamlRemoteUserId getRemoteUserId() {
		SecurityContext ctx = SecurityContextHolder.getContext();
		Object creds = ctx.getAuthentication().getCredentials();
		if (creds instanceof SAMLCredential == false) {
			return null;
		}
		return attributeExtractor.getRemoteUserId((SAMLCredential) creds);
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
	public SamlUserAttributeExtraction getAttributeExtractor() {
		return this.attributeExtractor;
	}

	/**
	 * @param attributeExtractor the attributeExtractor to set
	 */
	public void setAttributeExtractor(SamlUserAttributeExtraction attributeExtractor) {
		this.attributeExtractor = attributeExtractor;
	}
	
}
