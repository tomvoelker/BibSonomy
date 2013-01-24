package org.bibsonomy.webapp.util.spring.security.exceptionmapper;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.log4j.Logger;
import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.exceptions.SamlUsernameNotFoundException;
import org.bibsonomy.webapp.util.spring.security.userattributemapping.UserAttributeMapping;
import org.opensaml.saml2.core.Attribute;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.saml.SAMLCredential;

/**
 * @author jensi
 * @version $Id$
 */
public class SamlUsernameNotFoundExceptionMapper extends UsernameNotFoundExceptionMapper {
	private static final Logger log = Logger.getLogger(SamlUsernameNotFoundExceptionMapper.class);
	
	private UserAttributeMapping<SAMLCredential, ?> attributeExtractor;
	
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
			attributeExtractor.populate(user, ctx);
		}

		return user;
	}

	/**
	 * @return the attributeExtractor
	 */
	public UserAttributeMapping<SAMLCredential,?> getAttributeExtractor() {
		return this.attributeExtractor;
	}

	/**
	 * @param attributeExtractor the attributeExtractor to set
	 */
	public void setAttributeExtractor(UserAttributeMapping<SAMLCredential, ?> attributeExtractor) {
		this.attributeExtractor = attributeExtractor;
	}

}
