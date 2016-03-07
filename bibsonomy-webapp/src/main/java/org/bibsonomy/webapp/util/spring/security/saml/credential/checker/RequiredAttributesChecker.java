package org.bibsonomy.webapp.util.spring.security.saml.credential.checker;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bibsonomy.webapp.util.spring.security.exceptions.UseNotAllowedException;
import org.bibsonomy.webapp.util.spring.security.saml.util.SAMLCredentialUtils;
import org.opensaml.saml2.core.Attribute;
import org.springframework.security.saml.SAMLCredential;

/**
 * checks for required attributes
 *
 * @author dzo
 */
public class RequiredAttributesChecker implements SAMLCredentialChecker {
	
	private Map<String, String> requiredAttributes = Collections.emptyMap();
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.spring.security.saml.credential.checker.SAMLCredentialChecker#checkCredential(org.springframework.security.saml.SAMLCredential)
	 */
	@Override
	public void checkCredential(final SAMLCredential credential) {
		for (final Entry<String, String> requiredAttributeEntry : this.requiredAttributes.entrySet()) {
			final Attribute requiredAttribute = credential.getAttributeByName(requiredAttributeEntry.getKey());
			if (requiredAttribute == null) {
				throw new UseNotAllowedException("user can't use this app");
			}
			final List<String> values = SAMLCredentialUtils.getStringListFromAttribute(requiredAttribute);
			if (!values.contains(requiredAttributeEntry.getValue())) {
				throw new UseNotAllowedException("user can't use this app");
			}
		}
	}

	/**
	 * @param requiredAttributes the requiredAttributes to set
	 */
	public void setRequiredAttributes(Map<String, String> requiredAttributes) {
		this.requiredAttributes = requiredAttributes;
	}

}
