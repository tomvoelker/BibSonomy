package org.bibsonomy.webapp.util.spring.security.saml.util;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.util.StringUtils;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.XSString;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.saml.SAMLCredential;

/**
 * utils class for {@link SAMLCredential}
 * @author dzo
 */
public final class SAMLCredentialUtils {
	private static final Log log = LogFactory.getLog(SAMLCredentialUtils.class);
	
	private SAMLCredentialUtils() {
		// noop
	}
	
	/**
	 * extracts a string array from the samlCredential's attribute
	 * @param attribute
	 * @return the string array
	 */
	public static List<String> getStringListFromAttribute(final Attribute attribute) {
		final List<XMLObject> values = attribute.getAttributeValues();
		final List<String> stringValues = new LinkedList<>();
		if (!present(values)) {
			return stringValues;
		}
		
		for (final XMLObject obj : values) {
			final String value = xmlObjToString(obj);
			stringValues.add(value);
		}
		
		return stringValues;
	}
	
	/**
	 * extracts a single string value attribute
	 * @param samlCred
	 * @param attrName
	 * @return the attribute as string
	 */
	public static String getSingleStringValueAttribute(SAMLCredential samlCred, String attrName) {
		final Attribute attr = samlCred.getAttributeByName(attrName);
		if (attr == null) {
			throw new AuthenticationServiceException("no '" + attrName + "' attribute in saml response");
		}
		return getSingleStringValueFromAttribute(attr);
	}
	
	public static String getSingleStringValueFromAttribute(Attribute attr) {
		final String attrName = attr.getName();
		
		final List<String> stringValues = getStringListFromAttribute(attr);
		if (!present(stringValues)) {
			throw new AuthenticationServiceException("no values for '" + attrName + "' attribute in saml response");
		}
		if (stringValues.size() > 1) {
			log.warn("more than one value for attribute '" + attrName + "' " + StringUtils.implodeStringCollection(stringValues, ","));
		}
		
		final String value = stringValues.get(0);
		if (value == null) {
			throw new AuthenticationServiceException("no usable values for '" + attrName + "' attribute in saml response");
		}
		
		return value;
	}

	public static StringBuilder appendAttributesAsArrayString(StringBuilder sb, Collection<XMLObject> values) {
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
	private static String xmlObjToString(XMLObject obj) {
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
}
