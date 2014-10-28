package org.bibsonomy.webapp.util.spring.security.userattributemapping;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.bibsonomy.model.User;
import org.bibsonomy.model.user.remote.SamlRemoteUserId;
import org.bibsonomy.util.ValidationUtils;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.saml.SAMLCredential;

/**
 * Sets user attributes with values from a SAML (Shibboleth) SSO response
 * 
 * @author jensi
 */
public class SamlUserAttributeMapping implements UserAttributeMapping<SAMLCredential, SamlRemoteUserId> {
	private static final Logger log = Logger.getLogger(SamlUserAttributeMapping.class);
	private Map<String, String> samlToUserPropertiesMap;
	private String useridAttributeName;
	
	/**
	 * Sets user attributes with values from a SAML (Shibboleth) SSO response
	 * @param user object to be populated
	 * @param samlCred saml credentials to read from
	 * @return remote user id extrected from src object
	 */
	@Override
	public SamlRemoteUserId populate(User user, SAMLCredential samlCred) {
		SamlRemoteUserId remoteId = getRemoteUserId(samlCred);
		user.setRemoteUserId(remoteId);
		
		writeAttributeDebugLogs(samlCred);
		
		// copy user attributes
		for (Map.Entry<String, String> entry : samlToUserPropertiesMap.entrySet()) {
			Attribute samlAttr = samlCred.getAttributeByName(entry.getKey());
			Object samlValue = (samlAttr == null) ? null : getSingleStringValueFromAttribute(entry.getKey(), samlAttr);
			try {
				BeanUtils.setProperty(user, entry.getValue(), samlValue);
			} catch (Exception ex) {
				throw new RuntimeException("exception while setting property '" + entry.getValue() + "'", ex);
			}
		}
		return remoteId;
	}

	protected void writeAttributeDebugLogs(SAMLCredential samlCred) {
		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			for (Attribute a : samlCred.getAttributes()) {
				sb.append(a.getName()).append(" or simply ").append(a.getFriendlyName()).append(" = ");
				appendAttributesAsArrayString(sb, a.getAttributeValues());
				log.debug(sb.toString());
				sb.setLength(0);
			}
		}
	}
	
	/**
	 * @param samlCred saml credentials to read from
	 * @return remote user id extracted from credentials
	 */
	public SamlRemoteUserId getRemoteUserId(SAMLCredential samlCred) {
		final String userId = getUserId(samlCred);
		Assertion aa = samlCred.getAuthenticationAssertion();
		if (aa == null) {
			return null;
		}
		Issuer issuer = aa.getIssuer();
		if (issuer == null) {
			return null;
		}
		String idP = issuer.getValue();
		return new SamlRemoteUserId(idP, userId);
//		return "dummyUserId";
	}

	/**
	 * @param samlCred response from the remote system
	 * @return local userid of the remote system
	 */
	public String getUserId(SAMLCredential samlCred) {
		writeAttributeDebugLogs(samlCred);
		if (useridAttributeName != null) {
			return getSingleStringValueAttribute(samlCred, useridAttributeName);
		}
		// does this work at all?
		NameID nid = samlCred.getNameID();
		if (nid == null) {
			return null;
		}
		return nid.getValue();
	}

	protected String getSingleStringValueAttribute(SAMLCredential samlCred, String attrName) {
		Attribute attr = samlCred.getAttributeByName(attrName);
		if (attr == null) {
			throw new AuthenticationServiceException("no '" + attrName + "' attribute in saml response");
		}
		return getSingleStringValueFromAttribute(attrName, attr);
	}

	protected String getSingleStringValueFromAttribute(String attrName, Attribute attr) {
		List<XMLObject> values = attr.getAttributeValues();
		if (ValidationUtils.present(values) == false) {
			throw new AuthenticationServiceException("no values for '" + attrName + "' attribute in saml response");
		}
		if (values.size() > 1) {
			log.warn("more than one value for attribute '" + attrName + "' " + appendAttributesAsArrayString(new StringBuilder(), values));
		}
		String value = null;
		for (XMLObject obj : values) {
			value = xmlObjToString(obj);
			break;
		}
		if (value == null) {
			throw new AuthenticationServiceException("no usable values for '" + attrName + "' attribute in saml response");
		}
		
		return value;
	}

	protected StringBuilder appendAttributesAsArrayString(StringBuilder sb, Collection<XMLObject> values) {
		sb.append('[');
		for (XMLObject obj : values) {
			sb.append(xmlObjToString(obj)).append(", ");
		}
		if (values.size() > 0) {
			sb.setLength(sb.length() - 2);
		}
		return sb.append("]");
	}

	/**
	 * @param obj
	 * @return a string version of the obj argument
	 */
	public static String xmlObjToString(XMLObject obj) {
		String value;
		if (obj instanceof XSString) {
			value = ((XSString) obj).getValue();
		} else if (obj instanceof XSAny) {
			value = ((XSAny) obj).getTextContent();
		} else {
			value = obj.toString();
		}
		return value;
	}

	/**
	 * @return the samlToUserPropertiesMap map with SAML attribute names as keys and {@link User}-property names as values
	 */
	public Map<String, String> getSamlToUserPropertiesMap() {
		return this.samlToUserPropertiesMap;
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
	 * @param samlToUserPropertiesMap the samlToUserPropertiesMap to set
	 */
	public void setSamlToUserPropertiesMap(Map<String, String> samlToUserPropertiesMap) {
		this.samlToUserPropertiesMap = samlToUserPropertiesMap;
	}

}
