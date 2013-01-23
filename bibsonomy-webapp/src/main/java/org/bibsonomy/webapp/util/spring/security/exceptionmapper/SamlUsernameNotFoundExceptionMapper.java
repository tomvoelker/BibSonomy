package org.bibsonomy.webapp.util.spring.security.exceptionmapper;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.webapp.util.spring.security.exceptions.SamlUsernameNotFoundException;
import org.opensaml.saml2.core.Attribute;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

/**
 * @author jensi
 * @version $Id$
 */
public class SamlUsernameNotFoundExceptionMapper extends UsernameNotFoundExceptionMapper {
	private static final Logger log = Logger.getLogger(SamlUsernameNotFoundExceptionMapper.class);
	
	private final Map<String, String> attributeMap = new HashMap<String, String>();
	
	@Override
	public boolean supports(final UsernameNotFoundException e) {
		return present(e) && SamlUsernameNotFoundException.class.isAssignableFrom(e.getClass());
	}

	@Override
	public User mapToUser(final UsernameNotFoundException e) {
		final User user = new User();
		if (e instanceof SamlUsernameNotFoundException) {
			final SAMLCredential ctx = ((SamlUsernameNotFoundException) e).getSamlCreds();

			if (log.isDebugEnabled() == true) {
				for (Attribute a : ctx.getAttributes()) {
					log.debug("" + a.getName() + "=" + a.getFriendlyName());
				}
			}
			populateUserFromSaml(user, ctx);
		}

		return user;
	}

	/**
	 * Sets user attributes with values from a SAML (Shibboleth) SSO response
	 * @param user
	 * @param ctx
	 */
	public void populateUserFromSaml(final User user, final SAMLCredential ctx) {
		String remoteUserId = ctx.getNameID().getValue();
		String idP = ctx.getAuthenticationAssertion().getIssuer().getValue();
		user.setRemoteUserId(new SamlRemoteUserId(idP, remoteUserId));
		
		// copy user attributes
		for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
			Attribute attrValue = ctx.getAttributeByName(entry.getKey());
			Object value = (attrValue == null) ? null : attrValue.getFriendlyName();
			try {
				BeanUtils.setProperty(user, entry.getValue(), value);
			} catch (Exception ex) {
				throw new RuntimeException("exception while setting property '" + entry.getValue() + "'", ex);
			}
		}
	}

	/**
	 * @return the attributeMap
	 */
	public Map<String, String> getAttributeMap() {
		return this.attributeMap;
	}

}
