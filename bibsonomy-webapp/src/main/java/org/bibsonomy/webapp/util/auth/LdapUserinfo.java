package org.bibsonomy.webapp.util.auth;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Container for LDAP user data.
 * 
 * @author stefani
 *
 */
public class LdapUserinfo {

	/**
	 * Logger
	 */
	private final Log log = LogFactory.getLog(Ldap.class);

	private String userId = ""; 
	private String sureName = ""; 
	private String firstName = ""; 
	private String eMail = ""; 
	private String location = "";

	
	
	/** 
	 * @return String UserID
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 *  Set userID. If it is null, assign an empty String 
	 * 
	 * @param userId
	 */
	public void setUserId(String userId) {
		if (null==userId) userId="";
		this.userId = userId;
	}

	/**  
	 *  Set userID. If it is null, assign an empty String
	 *  Converts Attribute to String
	 *  
	 * @param userId
	 */
	public void setUserId(Attribute userId) {
		String userIdS = "";
		if (null!=userId) try {
			userIdS = userId.get().toString();
		} catch (NamingException ex) {
			log.error("NamingException in " + this.getClass().getName() + " (setUserId): " + ex.getMessage());
		}
		this.userId = userIdS;
	} 

	/**
	 * @return sureName
	 */
	public String getSureName() {
		return this.sureName;
	}
	
	/**
	 * Set sureName. If it is null, assign an empty String
	 *  
	 * @param sureName
	 */
	public void setSureName(String sureName) {
		if (null==sureName) sureName="";
		this.sureName = sureName;
	}
	
	/**  
	 * Set setSureName. If it is null, assign an empty String
	 * Converts Attribute to String
	 * @param sureName 
	 */
	public void setSureName(Attribute sureName) {
		String sureNameS = "";
		if (null!=sureName) try {
			sureNameS = sureName.get().toString();
		} catch (NamingException ex) {
			log.error("NamingException in " + this.getClass().getName() + " (setSureName): " + ex.getMessage());
		}
		this.sureName = sureNameS;
	} 

	/**
	 * @return firstName
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**  
	 * Set firstName. If it is null, assign an empty String
	 * Converts Attribute to String
	 * @param firstName 
	 */
	public void setFirstName(String firstName) {
		if (null==firstName) firstName="";
		this.firstName = firstName;
	}
	
	/**  
	 *  Set firstName. If it is null, assign an empty String
	 *  Converts Attribute to String
	 * @param firstName 
	 */
	public void setFirstName(Attribute firstName) {
		String firstNameS = "";
		if (null!=firstName) try {
			firstNameS = firstName.get().toString();
		} catch (NamingException ex) {
			log.error("NamingException in " + this.getClass().getName() + " (setFirstName): " + ex.getMessage());
		}
		this.firstName = firstNameS;
	} 

	/**
	 * @return eMail
	 */
	public String geteMail() {
		return this.eMail;
	}

	/**  
	 * Set eMail. If it is null, assign an empty String
	 * Converts Attribute to String
	 * @param eMail 
	 */
	public void seteMail(String eMail) {
		if (null==eMail) eMail="";
		this.eMail = eMail;
	}

	/**  
	 * Set eMail. If it is null, assign an empty String
	 * Converts Attribute to String
	 * @param eMail 
	 */
	public void seteMail(Attribute eMail) {
		String eMailS = "";
		if (null!=eMail) try {
			eMailS = eMail.get().toString();
		} catch (NamingException ex) {
			log.error("NamingException in " + this.getClass().getName() + " (seteMail): " + ex.getMessage());
		}
		this.eMail = eMailS;
	} 
	
	/**
	 * @return location
	 */
	public String getLocation() {
		return this.location;
	}

	/**  
	 *  Set location. If it is null, assign an empty String
	 *  Converts Attribute to String
	 * @param location 
	 */
	public void setLocation(String location) {
		if (null==location) location="";
		this.location = location;
	} 

	/**  
	 * Set location. If it is null, assign an empty String
	 * Converts Attribute to String
	 * @param location 
	 */
	public void setLocation(Attribute location) {
		String locationS = "";
		if (null!=location) try {
			locationS = location.get().toString();
		} catch (NamingException ex) {
			log.error("NamingException in " + this.getClass().getName() + " (setLocation): " + ex.getMessage());
		}
		this.location = locationS;
	} 



}
