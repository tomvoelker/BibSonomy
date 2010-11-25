package org.bibsonomy.webapp.util.spring.security.exceptionmapper;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.model.User;
import org.bibsonomy.webapp.util.spring.security.exceptions.LdapUsernameNotFoundException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author rja
 * @version $Id$
 */
public class LdapUsernameNotFoundExceptionMapper extends UsernameNotFoundExceptionMapper {

	@Override
	public boolean supports(final UsernameNotFoundException e) {
		return present(e) && LdapUsernameNotFoundException.class.isAssignableFrom(e.getClass());
	}
	
	@Override
	public User mapToUser(final UsernameNotFoundException e) {
		final User user = new User();
		if (e instanceof LdapUsernameNotFoundException) {
			final DirContextOperations dco = ((LdapUsernameNotFoundException) e).getDirContextOperations();

			try {
				/*
				 * copy user attributes
				 */
				user.setRealname(dco.getStringAttribute("givenname") + " " + dco.getStringAttribute("sn"));
				user.setEmail(dco.getStringAttribute("mail"));
				user.getSettings().setDefaultLanguage(dco.getStringAttribute("preferredlanguage"));
				user.setPlace(dco.getStringAttribute("l")); // location
				user.setLdapId(dco.getStringAttribute("uid"));
				user.setPassword(dco.getStringAttribute("userpassword"));
			} catch (Exception ee) {
				System.out.println(ee);
			}
			
//			ldapSearchDomain = (String) envContext.lookup("ldap/" + currentConfig + "/config/searchDomain");
//			ldapUserIdField = (String) envContext.lookup("ldap/" + currentConfig + "/config/userId");
//			ldapSureNameField = (String) envContext.lookup("ldap/" + currentConfig + "/config/sureName");
//			ldapGivenNameField = (String) envContext.lookup("ldap/" + currentConfig + "/config/givenName");
//			ldapMailField = (String) envContext.lookup("ldap/" + currentConfig + "/config/mail");
//			ldapLocationField = (String) envContext.lookup("ldap/" + currentConfig + "/config/location");
//			dirContextOperations.g
//			/*
//			 * TODO: fill user
//			 */
//			user.setEmail(ldapUserinfo.getEmail());
//			command.getRegisterUser().setRealname(ldapUserinfo.getFirstName() + " " + ldapUserinfo.getSureName());
//			// command.getRegisterUser().setGender(ldapUserinfo.);
//			command.getRegisterUser().setPlace(ldapUserinfo.getLocation());
//			command.getRegisterUser().setLdapId(ldapUserinfo.getUserId());
//			command.getRegisterUser().setPassword(ldapUserinfo.getPasswordHashMd5Hex());
//			
		}
		
		return user;
	}
	
}
