package org.bibsonomy.webapp.util.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Sven Stefani
 * @version $Id$
 */
public class LdapTest {
	
	@Ignore // test only, if ldap connection is available
	@Test
	public void connection() {
		String ldapUserId = "02004160";
        String ldapCredentials = "170874";

        Ldap ldap = new Ldap();
		LdapUserinfo ldapUserinfo = ldap.checkauth(ldapUserId, ldapCredentials);
		assertNotNull("ldapUserinfo is null. Something with LDAP is wrong, maybe authorization failure." , ldapUserinfo);
		assertFalse("userID is not set in ldapUserinfo, some strange thing happens!", ldapUserinfo.getUserId()=="");
	}

}
