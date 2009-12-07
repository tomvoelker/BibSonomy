package org.bibsonomy.webapp.util.auth;

import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class providing features for LDAP authentication
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class Ldap implements Serializable {

	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 437358812087062133L;

	/**
	 * Logger
	 */
	private final Log log = LogFactory.getLog(Ldap.class);


	/** name of the property file which configures ldap */
	private final String LDAPPropertyFilename = "ldap.properties";
	
	
	public LdapUserinfo checkauth(String ldapUserId, String ldapCredentials) {

		// exit, if one parameter is null
		if (null==ldapUserId || null==ldapCredentials) return null;
		
		log.info("check LDAP authentication");
		Properties props = new Properties();

		LdapUserinfo ldapUserinfo = new	LdapUserinfo();	
		
		try {
			// read database properties
			props.load(Ldap.class.getClassLoader().getResourceAsStream(this.LDAPPropertyFilename));
		} catch (IOException ex) {
			log.error("There is no LDAP properties file. It is named: " + this.LDAPPropertyFilename + " and normally located in src/main/resources");
			throw new RuntimeException(ex);
		}
		
        String query = "("+props.getProperty("ldap.principal.useridentifier")+"="+ldapUserId+")";
        
        String ldapPrincipal = props.getProperty("ldap.principal.useridentifier")+"="+ldapUserId;
        if (!props.getProperty("ldap.principal.trailing").isEmpty()){
        	ldapPrincipal = ldapPrincipal + ","+props.getProperty("ldap.principal.trailing");
        }
        
        Hashtable<String, String> env = new Hashtable<String,String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, props.getProperty("ldap.url"));
		env.put(Context.SECURITY_AUTHENTICATION, props.getProperty("ldap.authentication"));
		env.put(Context.SECURITY_PRINCIPAL, ldapPrincipal);
		env.put(Context.SECURITY_CREDENTIALS, ldapCredentials);

		DirContext context=null;
		try {
			context = new InitialDirContext(env);
		} catch (AuthenticationException ex) {
			// wrong user authentication
			ldapUserinfo = null;			
		} catch (NamingException ex) {
			log.warn("NamingException in " + this.getClass().getName() + " (checkauth/1): " + ex.getMessage());
		}
		
		if (context != null) {
			try {
	            SearchControls ctrl = new SearchControls();
	            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
	            NamingEnumeration<SearchResult> enumeration;
					enumeration = context.search("", query, ctrl);
	            while (enumeration.hasMore()) {
	                SearchResult result = enumeration.next();
	                Attributes attribs = result.getAttributes();
	
                	ldapUserinfo.setUserId(attribs.get(props.getProperty("ldap.principal.useridentifier")));
                	ldapUserinfo.setSureName(attribs.get(props.getProperty("ldap.surename")));
                	ldapUserinfo.setFirstName(attribs.get(props.getProperty("ldap.firstname")));
                	ldapUserinfo.seteMail(attribs.get(props.getProperty("ldap.email")));
                	ldapUserinfo.setLocation(attribs.get(props.getProperty("ldap.location")));
	                
            		log.info("LDAP-Authentication successful for user " + ldapUserinfo.getUserId() + " (" + ldapUserinfo.getSureName() + ", " + ldapUserinfo.getFirstName() + ", " + ldapUserinfo.geteMail() + ", " + ldapUserinfo.getLocation() + ")");
	            }
			} catch (NamingException ex) {
				log.warn("NamingException in " + this.getClass().getName() + " (checkauth/2): " + ex.getMessage());
			}

        }
		else
		{ // can't get context
			log.warn("LDAP context not available. Wrong authentication !?");
		}

		return ldapUserinfo;
		
	}
	
}