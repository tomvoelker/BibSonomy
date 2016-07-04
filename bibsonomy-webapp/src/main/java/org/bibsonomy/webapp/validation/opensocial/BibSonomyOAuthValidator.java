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
package org.bibsonomy.webapp.validation.opensocial;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

import net.oauth.signature.pem.PEMReader;

import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;
import org.bibsonomy.opensocial.oauth.database.OAuthLogic;
import org.bibsonomy.opensocial.oauth.database.beans.OAuthConsumerInfo;
import org.bibsonomy.util.StringUtils;
import org.bibsonomy.webapp.command.opensocial.OAuthAdminCommand;
import org.bibsonomy.webapp.command.opensocial.OAuthAdminCommand.AdminAction;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

/**
 * @author fei
 */
public class BibSonomyOAuthValidator implements  Validator<OAuthAdminCommand>{
	
	private final OAuthLogic logic;
	
	/**
	 * @param logic
	 */
	public BibSonomyOAuthValidator (OAuthLogic logic) {
		this.logic = logic;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return OAuthAdminCommand.class.equals(clazz);
	}

	@Override
	public void validate(Object oAuthObject, Errors errors) {
		OAuthAdminCommand command = (OAuthAdminCommand)oAuthObject;


		if (KeyType.RSA_PRIVATE.equals(command.getConsumerInfo().getKeyType())) {
			// check wheter consumer secret is a valid (pem) encoded certificate
			try {
				this.getPublicKeyFromPem(command.getConsumerInfo().getConsumerSecret());
			} catch (Exception e) {
				errors.rejectValue("consumerInfo.consumerSecret", "error.oauth.rsa.pubKey");
			}
		}
		
		if (AdminAction.Register.equals(command.getAdminAction_())) {
			// Check whether required fields are empty
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "consumerInfo.consumerKey", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "consumerInfo.consumerSecret", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "consumerInfo.serviceName", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "consumerInfo.keyType", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "consumerInfo.title", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "consumerInfo.summary", "error.field.required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "consumerInfo.description", "error.field.required");
			
			// Check whether consumer-key already exists
			OAuthConsumerInfo consumer = this.logic.readConsumer(command.getConsumerInfo().getConsumerKey());
			if (consumer != null) {
				errors.reject("consumerInfo.consumerKey", "error.field.duplicate.oauth.consumerKey");
			}
		}
		else if (AdminAction.Remove.equals(command.getAdminAction_()) &&
				!org.bibsonomy.util.ValidationUtils.present(command.getConsumerInfo().getConsumerKey())) {
			errors.reject("error.field.required");
		}
	}

	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	private PublicKey getPublicKeyFromPem(String pem) throws GeneralSecurityException, IOException {
		InputStream stream = new ByteArrayInputStream(pem.getBytes(StringUtils.CHARSET_UTF_8));

		PEMReader reader = new PEMReader(stream);
		byte[] bytes = reader.getDerBytes(); 	
		PublicKey pubKey;

		if (PEMReader.PUBLIC_X509_MARKER.equals(reader.getBeginMarker())) {
			KeySpec keySpec = new X509EncodedKeySpec(bytes);
			KeyFactory fac = KeyFactory.getInstance("RSA");
			pubKey = fac.generatePublic(keySpec);
		} else if (PEMReader.CERTIFICATE_X509_MARKER.equals(reader.getBeginMarker())) {
			pubKey = getPublicKeyFromDerCert(bytes);
		} else {
			throw new IOException("Invalid PEM fileL: Unknown marker for " + 
					" public key or cert " + reader.getBeginMarker());
		}

		return pubKey;
	}

	private PublicKey getPublicKeyFromDerCert(byte[] certObject)
	throws GeneralSecurityException {
		CertificateFactory fac = CertificateFactory.getInstance("X509");
		ByteArrayInputStream in = new ByteArrayInputStream(certObject);
		X509Certificate cert = (X509Certificate)fac.generateCertificate(in);
		return cert.getPublicKey();
	}	
}
