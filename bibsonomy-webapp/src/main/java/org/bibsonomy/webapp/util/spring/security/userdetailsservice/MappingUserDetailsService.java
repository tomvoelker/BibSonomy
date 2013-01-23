package org.bibsonomy.webapp.util.spring.security.userdetailsservice;

import org.bibsonomy.util.spring.security.RemoteOnlyUserDetails;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

/**
 * @author jensi
 * @version $Id$
 */
public class MappingUserDetailsService implements SAMLUserDetailsService {

	// TODO: add mapper property that knows how to map userids
	
	/**
	 * {@link UserDetailsService} property that knows how to load the {@link UserDetails}
	 */
	private UserDetailsService userDetailsService;
	private NameSpacedNameMapping userNameMapping;
	
	@Override
	public Object loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
		// TODO:
		// map credentils to bibsonomy userid
		// if no mapping present:
		//   create anonymous user or throw exception? - probably a special anonymous user with saml data and special contextrepository handling in
		//   org.bibsonomy.webapp.util.spring.security.UsernameSecurityContextRepository.setLoginUser(HttpServletRequest, Authentication)
		//   such that it can be checked later
		String idPId = credential.getAuthenticationAssertion().getIssuer().getValue();
		String remoteUserId = credential.getNameID().getValue();
		final String systemName = userNameMapping.mapName(idPId, remoteUserId);
		if (systemName == null) {
			return new RemoteOnlyUserDetails(credential);
			// TODO: raise exception somewhere later to allow regular registration:
			// throw new SamlUsernameNotFoundException("SAML userid not found in database", credential);
		}
		
		final UserDetails loadedUser = this.userDetailsService.loadUserByUsername(systemName);
		
		// TODO: are we missing something else?
		if (!loadedUser.isEnabled()) {
			throw new DisabledException("user was deleted");
		}
		
		return loadedUser;
	}

	/**
	 * @return {@link UserDetailsService} property that knows how to load the {@link UserDetails}
	 */
	public UserDetailsService getUserDetailsService() {
		return this.userDetailsService;
	}

	/**
	 * @param userDetailsService {@link UserDetailsService} property that knows how to load the {@link UserDetails}
	 */
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	/**
	 * @return the userNameMapping
	 */
	public NameSpacedNameMapping getUserNameMapping() {
		return this.userNameMapping;
	}

	/**
	 * @param userNameMapping the userNameMapping to set
	 */
	public void setUserNameMapping(NameSpacedNameMapping userNameMapping) {
		this.userNameMapping = userNameMapping;
	}

}
