/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.webapp.util.spring.security.userdetailsservice;

import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.util.spring.security.RemoteOnlyUserDetails;
import org.bibsonomy.webapp.util.spring.security.userattributemapping.SamlUserAttributeMapping;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;
import org.springframework.security.saml.userdetails.SAMLUserDetailsService;

/**
 * @author jensi
 */
public class MappingUserDetailsService implements SAMLUserDetailsService {
	
	/**
	 * {@link UserDetailsService} property that knows how to load the {@link UserDetails}
	 */
	private UserDetailsService userDetailsService;
	private SamlUserAttributeMapping attributeExtractor;
	private NameSpacedNameMapping<SamlRemoteUserId> userNameMapping;
	
	@Override
	public UserDetails loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
		
		// TODO:
		// map credentials to bibsonomy userid
		// if no mapping present:
		//   create anonymous user or throw exception? - probably a special anonymous user with saml data and special contextrepository handling in
		//   org.bibsonomy.webapp.util.spring.security.UsernameSecurityContextRepository.setLoginUser(HttpServletRequest, Authentication)
		//   such that it can be checked later
		final SamlRemoteUserId remoteId = this.attributeExtractor.getRemoteUserId(credential);
		final String systemName = this.userNameMapping.map(remoteId);
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
	public NameSpacedNameMapping<SamlRemoteUserId> getUserNameMapping() {
		return this.userNameMapping;
	}

	/**
	 * @param userNameMapping the userNameMapping to set
	 */
	public void setUserNameMapping(NameSpacedNameMapping<SamlRemoteUserId> userNameMapping) {
		this.userNameMapping = userNameMapping;
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

}
