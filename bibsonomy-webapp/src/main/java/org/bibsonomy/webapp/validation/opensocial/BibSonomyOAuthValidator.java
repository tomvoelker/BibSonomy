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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shindig.gadgets.oauth.BasicOAuthStoreConsumerKeyAndSecret.KeyType;
import org.bibsonomy.webapp.command.opensocial.OAuthAdminCommand;
import org.bibsonomy.webapp.util.Validator;
import org.springframework.validation.Errors;

/**
 * @author fei
 * @version $Id$
 */
public class BibSonomyOAuthValidator implements  Validator<OAuthAdminCommand>{
	private static final Log log = LogFactory.getLog(BibSonomyOAuthValidator.class);

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
	}

	//------------------------------------------------------------------------
	// private helpers
	//------------------------------------------------------------------------
	private PublicKey getPublicKeyFromPem(String pem) throws GeneralSecurityException, IOException {
		InputStream stream = new ByteArrayInputStream(pem.getBytes("UTF-8"));

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
