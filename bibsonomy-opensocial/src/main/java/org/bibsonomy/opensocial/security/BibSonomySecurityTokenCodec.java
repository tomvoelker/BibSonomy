package org.bibsonomy.opensocial.security;

import java.util.Map;

import org.apache.shindig.auth.BibSonomyBlobCrypterSecurityTokenCodec;
import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.auth.SecurityTokenCodec;
import org.apache.shindig.auth.SecurityTokenException;
import org.apache.shindig.config.ContainerConfig;

import com.google.inject.Inject;

/**
 * TODO: remove BibSonomy from class name
 * @author fmi
 *
 */
public class BibSonomySecurityTokenCodec implements SecurityTokenCodec {
	private final SecurityTokenCodec codec;

	@Inject
	public BibSonomySecurityTokenCodec(final ContainerConfig config) {
		this.codec = new BibSonomyBlobCrypterSecurityTokenCodec(config);
	}

	@Override
	public SecurityToken createToken(final Map<String, String> tokenParameters) throws SecurityTokenException {
		return codec.createToken(tokenParameters);
	}


	@Override
	public String encodeToken(final SecurityToken token) throws SecurityTokenException {
		if (token == null) {
			return null;
		}
		return codec.encodeToken(token);
	}

}
