package org.bibsonomy.webapp.util.spring.security.userattributemapping;

import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.springframework.security.saml.SAMLCredential;

/**
 * Sets user attributes with values from a SAML (Shibboleth) SSO response
 * 
 * @author jensi
 * @version $Id$
 */
public class SamlUserAttributeMapping implements UserAttributeMapping<SAMLCredential, SamlRemoteUserId> {
	private Map<String, String> attributeMap;
	private String useridAttributeName;
	
	/**
	 * Sets user attributes with values from a SAML (Shibboleth) SSO response
	 * @param user object to be populated
	 * @param ctx saml credentials to read from
	 * @return remote user id extrected from src object
	 */
	@Override
	public SamlRemoteUserId populate(User user, SAMLCredential ctx) {
		SamlRemoteUserId remoteId = getRemoteUserId(ctx);
		user.setRemoteUserId(remoteId);
		
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
		user.setRealname("lalala hoho");
		return remoteId;
	}
	
	/**
	 * @param samlCred saml credentials to read from
	 * @return remote user id extracted from credentials
	 */
	public SamlRemoteUserId getRemoteUserId(SAMLCredential samlCred) {
		NameID nid = samlCred.getNameID();
		if (nid == null) {
			return null;
		}
		Assertion aa = samlCred.getAuthenticationAssertion();
		if (aa == null) {
			return null;
		}
		Issuer issuer = aa.getIssuer();
		if (issuer == null) {
			return null;
		}
		String idP = issuer.getValue();
		return new SamlRemoteUserId(idP, nid.getValue());
//		return "dummyUserId";
	}

	/**
	 * @return the attributeMap map with SAML attribute names as keys and {@link User}-property names as values
	 */
	public Map<String, String> getAttributeMap() {
		return this.attributeMap;
	}

	/**
	 * @return the useridAttributeName
	 */
	public String getUseridAttributeName() {
		return this.useridAttributeName;
	}

	/**
	 * @param useridAttributeName the useridAttributeName to set
	 */
	public void setUseridAttributeName(String useridAttributeName) {
		this.useridAttributeName = useridAttributeName;
	}

	/**
	 * @param attributeMap the attributeMap to set
	 */
	public void setAttributeMap(Map<String, String> attributeMap) {
		this.attributeMap = attributeMap;
	}

}
