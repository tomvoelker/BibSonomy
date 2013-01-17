package org.bibsonomy.webapp.controller.actions;

import org.bibsonomy.common.enums.AuthMethod;
import org.bibsonomy.webapp.command.VuFindUserInitCommand;
import org.bibsonomy.webapp.controller.opensocial.OAuthRequestTokenController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.spring.security.exceptions.SpecialAuthMethodRequiredException;


/**
 * This controller is called directly after the first vuFind login of a user.
 * It generates an oAuth-token request and ensures that there is a bibsonomy user for the remoteAuthentication (shibboleth) user. A new user is generated if required.
 * 
 * @author jensi
 *
 */
public class VuFindUserInitController implements MinimalisticController<VuFindUserInitCommand> {

	private OAuthRequestTokenController oaReqTokenController;
//	AuthenticationEntryPoint remoteAuthentication;
	
	@Override
	public VuFindUserInitCommand instantiateCommand() {
		return new VuFindUserInitCommand();
	}

	@Override
	public View workOn(VuFindUserInitCommand command) {
		// TODO: check if there's already a logged in user. If so, then we might like to ask the user whether he likes to directly connect his remote authentication to this user account.
		String remoteUserId = getRemoteUserId();
		if (remoteUserId == null) {
			throw new SpecialAuthMethodRequiredException(AuthMethod.SAML);
		}
		
		// probably not needed (to be done in spring security filters):
		// remoteAuthentication.commence(command.getRe getRequest(), command.getResponse(), authException);
		
		
		// TODO:
		// - check if remoteuser maps to an existing bibsonomy user
		//   - if not: create one and add pair to the mapping
		// - login user but without any "stay logged in" cookie
		return oaReqTokenController.workOn(command);
	}

	protected String getRemoteUserId() {
//		Object creds = SecurityContextHolder.getContext().getAuthentication().getCredentials();
//		if (creds instanceof SAMLCredential == false) {
//			return null;
//		}
//		SAMLCredential samlCred = (SAMLCredential) creds;
//		NameID nid = samlCred.getNameID();
//		if (nid == null) {
//			return null;
//		}
//		return nid.getValue();
		return "dummyUserId";
	}

	/**
	 * @return the regular oAuth controller
	 */
	public OAuthRequestTokenController getOaReqTokenController() {
		return this.oaReqTokenController;
	}

	/**
	 * @param oaReqTokenController the regular oAuth controller
	 */
	public void setOaReqTokenController(OAuthRequestTokenController oaReqTokenController) {
		this.oaReqTokenController = oaReqTokenController;
	}
	
}
