package org.bibsonomy.opensocial.security;

import org.apache.shindig.auth.BlobCrypterSecurityToken;
import org.apache.shindig.common.crypto.BasicBlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypterException;
import org.apache.shindig.common.util.Utf8UrlCoder;
import org.bibsonomy.model.User;

/**
 * class for extracting credentials from the spring security framework 
 * 
 * @author fei
 */
public class SecurityTokenUtil {
	public static final String SECURITY_TOKEN_KEY_STRING = "gadgets.securityTokenKeyString";
	
	private final static String INSECURE_KEY = "Th!s Iz a kEy which is long EnOUgh";
	private final static String container = "default";
	private final static String domain = null;
	
	private final static BlobCrypter crypter = new BasicBlobCrypter(INSECURE_KEY.getBytes());
	  
	public static String getSecurityToken(User loginUser, String gadgetUrl) throws BlobCrypterException {
		
		BlobCrypterSecurityToken st = new BlobCrypterSecurityToken(crypter, container, domain);
		st.setViewerId(loginUser.getName());
		st.setOwnerId(loginUser.getName());
		st.setAppUrl(gadgetUrl);
		return Utf8UrlCoder.encode(st.encrypt());
	}

}
