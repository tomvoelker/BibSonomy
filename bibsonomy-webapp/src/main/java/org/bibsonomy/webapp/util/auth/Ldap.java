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
	private static final Log log = LogFactory.getLog(Ldap.class);


	/** name of the property file which configures ldap */
	//private final String LDAPPropertyFilename = "ldap.properties";

    @Override
	public Object getObjectInstance( Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws NamingException {

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
	 * returns ldapuserinfo object with retrieved userdata if user exists otherwise null 
	 * @param ldapUserId
	 * @param ldapCredentials
	 * @return LdapUserinfo
	 */
	public LdapUserinfo checkauth(String ldapUserId, String ldapCredentials) {

		LdapUserinfo ldapUserinfo = new	LdapUserinfo();	

		// exit, if one parameter is null
		if (null==ldapUserId || ldapUserId.equals("")) {
			log.info("LdapUserinfo.checkauth: ldapUserId is NULL or empty!");
			return null;
		} else if (null==ldapCredentials || ldapCredentials.equals("")) {
			log.info("LdapUserinfo.checkauth: ldapCredentials is NULL or empty!");
			return null;
		}
		
		// get some ldap parameter from environment ldap resource configuration
		String currentConfig = "";
		String ldapSearchDomain = "";
		String ldapUserIdField = "";
		String ldapSureNameField = "";
		String ldapGivenNameField = "";
		String ldapMailField = "";
		String ldapLocationField = "";
		String ldapUserPasswordField = "userPassword";
		Context initContext = null;
		Context envContext = null;
		try {
			initContext = new InitialContext();
			envContext = (Context) initContext.lookup("java:/comp/env");
		} catch (NamingException ex) {
			log.error("Error when trying create initContext lookup for java:/comp/env via JNDI.", ex);
		}
		try {
			currentConfig =  (String) envContext.lookup("ldap/currentConfig");
		} catch (NamingException ex) {
			log.error("Error when trying to read environment variable ldap/currentConfig via JNDI.", ex);
		}
		try {
			ldapSearchDomain = (String) envContext.lookup("ldap/"+currentConfig+"/config/searchDomain");
			ldapUserIdField = (String) envContext.lookup("ldap/"+currentConfig+"/config/userId");
			ldapSureNameField = (String) envContext.lookup("ldap/"+currentConfig+"/config/sureName");
			ldapGivenNameField = (String) envContext.lookup("ldap/"+currentConfig+"/config/givenName");
			ldapMailField = (String) envContext.lookup("ldap/"+currentConfig+"/config/mail");
			ldapLocationField = (String) envContext.lookup("ldap/"+currentConfig+"/config/location");
		} catch (NamingException ex) {
			log.error("Error when trying to read environment variables 'ldap/"+currentConfig+"/config/*' via JNDI.", ex);
		}

		log.info("using ldap configuration: "+currentConfig);
		
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
			ctx = (DirContext) envCtx.lookup("ldap/"+currentConfig+"/dir");
		} catch (NamingException ex) {
			log.error("Error when trying to create LDAP connection to 'ldap/"+currentConfig+"/dir' via JNDI.", ex);
		}

		// now we have got a InitialDirContext from LDAP Resource
		if (ctx != null) {
			try {
	            SearchControls ctrl = new SearchControls();
	            ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
	            NamingEnumeration<SearchResult> enumeration;
	            enumeration = ctx.search(ldapSearchDomain, query, ctrl);
				if (enumeration.hasMore()) {
		            while (enumeration.hasMore()) {
		                SearchResult result = enumeration.next();
		                Attributes attribs = result.getAttributes();
		
	                	ldapUserinfo.setUserId(attribs.get(ldapUserIdField));
	                	if ((null != ldapSureNameField) && ldapSureNameField != "") ldapUserinfo.setSureName(attribs.get(ldapSureNameField));
	                	if ((null != ldapGivenNameField) && ldapGivenNameField != "") ldapUserinfo.setFirstName(attribs.get(ldapGivenNameField));
	                	if ((null != ldapMailField) && ldapMailField != "") ldapUserinfo.setEmail(attribs.get(ldapMailField));
	                	if ((null != ldapLocationField) && ldapLocationField != "") ldapUserinfo.setLocation(attribs.get(ldapLocationField));
	                	ldapUserinfo.setPasswordHash(attribs.get(ldapUserPasswordField));
	                }
		            
					// check password
					if (ldapUserinfo.checkPasswordHash(ldapCredentials)) {
						// password is correct
						log.info("Password is CORRECT");
					} else {
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
				log.warn("Check LDAP server or configuration in context.xml");
				ldapUserinfo = null;
			}

		} else { // can't get context
			log.warn("LDAP context not available. Wrong authentication within JNDI-LDAP-Resource!?");
			ldapUserinfo = null;
		}

		return ldapUserinfo;
		
	}
	
}