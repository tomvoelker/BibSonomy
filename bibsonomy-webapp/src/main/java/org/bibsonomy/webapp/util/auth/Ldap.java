package org.bibsonomy.webapp.util.auth;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Class providing features for LDAP authentication
 * 
 * @author Sven Stefani
 * @version $Id$
 */
public class Ldap implements ObjectFactory, InitialContextFactory, Serializable {

	/**
	 * serial uid
	 */
	private static final long serialVersionUID = 437358812087062133L;

	/**
	 * Logger
	 */
	private final Log log = LogFactory.getLog(Ldap.class);


	/** name of the property file which configures ldap */
	//private final String LDAPPropertyFilename = "ldap.properties";

    public Object getObjectInstance( Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
    		    throws NamingException {

    		        Hashtable<String, String> env = new Hashtable<String, String>();
    		        Reference ref = (Reference) obj;
    		        Enumeration<?> addrs = ref.getAll();

    		        while (addrs.hasMoreElements()) {
    		            RefAddr addr = (RefAddr) addrs.nextElement();
		            	//log.info("-----------------------------------------------------------------           LDAP-JNDI: " + addr.getType().toString() + " = " + addr.getContent().toString());
    		            if (addr.getType().equals("java.naming.factory.initial")) {
    		                env.put(addr.getType(), addr.getContent().toString());
    		            } else if (addr.getType().equals("java.naming.provider.url")){
    		                env.put(addr.getType(), addr.getContent().toString());
    		            } else if (addr.getType().equals("java.naming.security.authentication")) {
    		                env.put(addr.getType(), addr.getContent().toString());
    		            } else if (addr.getType().equals("java.naming.security.principal")) {
    		                env.put(addr.getType(), addr.getContent().toString());
    		            } else if (addr.getType().equals("java.naming.security.credentials")) {
    		                env.put(addr.getType(), addr.getContent().toString());
    		            } else if (addr.getType().equals("com.sun.jndi.ldap.connect.pool")) {
    		                env.put(addr.getType(), addr.getContent().toString());

    		            } else if (addr.getType().equals("userIdField")) {
    		            	env.put(addr.getType(), addr.getContent().toString());
    		            	//this.userIdField = addr.getContent().toString();
    		            } else {
    		            	//log.info(" - - - LDAP-JNDI: " + addr.getType().toString() + " = " + addr.getContent().toString());
    		            }
    		            	
    		        }

    		        return this.getInitialContext(env);
    		    }


    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
    	return new InitialDirContext(environment);
    }
	
	
	
	
	/** check authorization of user
	 * returns ldapuserinfo object with retireved userdata if user exists otherwise null 
	 * @param ldapUserId
	 * @param ldapCredentials
	 * @return LdapUserinfo
	 */
	public LdapUserinfo checkauth(String ldapUserId, String ldapCredentials) {

		LdapUserinfo ldapUserinfo = new	LdapUserinfo();	

		// exit, if one parameter is null
		if (null==ldapUserId || null==ldapCredentials) return null;
		
		// get some ldap parameter from environment ldap resource configuration
		String ldapUserIdField = "";
		String ldapSurenameField = "";
		String ldapGivennameField = "";
		String ldapMailField = "";
		String ldapLocationField = "";
		String ldapUserPasswordField = "userPassword";
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			ldapUserIdField = (String) envContext.lookup("ldap/config/userId");
			ldapSurenameField = (String) envContext.lookup("ldap/config/surename");
			ldapGivennameField = (String) envContext.lookup("ldap/config/givenname");
			ldapMailField = (String) envContext.lookup("ldap/config/mail");
			ldapLocationField = (String) envContext.lookup("ldap/config/location");
		} catch (NamingException ex) {
			log.error("Error when trying to read environment variables 'ldap/config/*' via JNDI.", ex);
		}
		
		String query = "";
//		query +=  "&(";
		query +=  "("+ ldapUserIdField +"="+ldapUserId+")";
//		query +=  "("+ ldapUserPasswordField +"="+ldapUserinfo.generatePicaHashBase64(ldapCredentials)+")";
//		query +=  ")";

		log.info("check LDAP authentication");
		log.info("LDAP query: "+query);


		// get ldap resource (see tomcat's context.xml)
		Context newCtx = null;
		Context envCtx = null;
		DirContext ctx = null;
		try {
			newCtx = new InitialContext();
			envCtx = (Context) newCtx.lookup("java:comp/env");
			ctx = (DirContext) envCtx.lookup("ldap/DirContext");
		} catch (NamingException ex) {
			log.error("Error when trying to create LDAP connection with configuration provided by JNDI.", ex);
		}

		// now we have got a InitialDirContext from LDAP Resource
		if (ctx != null) {
			try {
	            SearchControls ctrl = new SearchControls();
	            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
	            NamingEnumeration<SearchResult> enumeration;
	            enumeration = ctx.search("ou=pica-users,dc=ub,dc=uni-kassel,dc=de", query, ctrl);
				if (enumeration.hasMore()) {
		            while (enumeration.hasMore()) {
		                SearchResult result = enumeration.next();
		                Attributes attribs = result.getAttributes();
		
	                	ldapUserinfo.setUserId(attribs.get(ldapUserIdField));
	                	ldapUserinfo.setSureName(attribs.get(ldapSurenameField));
	                	ldapUserinfo.setFirstName(attribs.get(ldapGivennameField));
	                	ldapUserinfo.setEmail(attribs.get(ldapMailField));
	                	ldapUserinfo.setLocation(attribs.get(ldapLocationField));
	                	ldapUserinfo.setPasswordPicaHash(attribs.get(ldapUserPasswordField));
/*
	                	log.info("LDAP-Properties: ############> " + attribs.toString());
	                	log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ LDAP-Password: "+ldapUserinfo.getPasswordPicaHash());
	                	log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ LDAP-PasswordB64: "+ldapUserinfo.getPasswordPicaHashBase64());
	                	log.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ LDAP-PasswordMD5: "+ldapUserinfo.getPasswordPicaHashMd5());
	                	log.info("LDAP-Authentication successful for user " + ldapUserinfo.getUserId() + " (" + ldapUserinfo.getSureName() + ", " + ldapUserinfo.getFirstName() + ", " + ldapUserinfo.geteMail() + ", " + ldapUserinfo.getLocation() + ")");
*/
	                }
		            
					// check password
					if (ldapUserinfo.checkPasswordPicaHash(ldapCredentials)) {
						// password is correct
						log.info("Password is CORRECT");
						
					}
					else {
						// password is not correct
						log.info("Password is NOT correct");
						ldapUserinfo = null;
					}
		            
				} else {
					log.info("no record retrieved from ldap. User "+ ldapUserId +" does not exist.");
					ldapUserinfo = null;
				}
					
			} catch (NamingException ex) {
				log.warn("NamingException in " + this.getClass().getName() + " (checkauth/2): " + ex.getMessage());
			}

		}
		else
		{ // can't get context
			log.warn("LDAP context not available. Wrong authentication within JNDI-LDAP-Resource!?");
			ldapUserinfo = null;
		}

		return ldapUserinfo;
		
	}
	
}