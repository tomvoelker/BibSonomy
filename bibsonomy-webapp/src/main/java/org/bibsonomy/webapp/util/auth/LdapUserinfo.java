package org.bibsonomy.webapp.util.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;

import org.apache.commons.codec.binary.Base64;
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
	private final Log log = LogFactory.getLog(LdapUserinfo.class);

	private String userId = ""; 
	private String sureName = ""; 
	private String firstName = ""; 
	private String eMail = ""; 
	private String location = "";
	private String passwordPicaHash = "";


	/**
	 * generates a pica-"hashed" value from string
	 * this function will add prefix "{PICA}" to hash  
	 * @param s string to hash for pica
	 * @return Picahash
	 */

	public String generatePicaHash(String s) {
		return generatePicaHash(s, "{PICA}");
	}
	
	/**
	 * generates a pica-"hashed" value from string
	 * this function will add prefix "{PICA}" to hash
	 * and convert result with base64  
	 * @param s string to hash for pica
	 * @return Base64 Picahash
	 */
	public String generatePicaHashBase64(String s) {
		return new String(Base64.encodeBase64(generatePicaHash(s, "{PICA}").getBytes()));
	}

	/**
	 * generates a pica-"hashed" value from string
	 * hash-prefix can be set  
	 * @param s string to hash for pica
	 * @return pica hash
	 */
	public String generatePicaHash(String s, String prefix) {
		byte[] b = s.getBytes();

		Integer hash = 0;

		for (int i=0; i < b.length; i++) {
			hash += b[i]*(i+1);
		}
		
		return prefix + hash.toString();
	}
	
	
	public boolean checkPasswordPicaHash(String password) {
		boolean authOk = false;
		if ( this.getPasswordPicaHash().equals(generatePicaHash(password))) {
			authOk = true;
		} else {
			authOk = false;
		}
		return authOk;
	}
	
	
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
		this.setUserId(userIdS);
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
		this.setSureName(sureNameS);
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
		this.setFirstName(firstNameS);
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
		this.seteMail(eMailS);
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
		this.setLocation(locationS);
	} 

    public void setPasswordPicaHash (Attribute password) {
    	byte[] passwordB = null;

    	Object obj; 
    	try {
			obj = password.get();

			if (obj instanceof byte[]) {
				passwordB = ((byte[])obj);
			}
			else
			if (obj instanceof String) {
				passwordB = (((String)obj).getBytes());
			}

			// convert byte[] to String
			String decodedString = new String(passwordB); 
			
	    	this.passwordPicaHash = decodedString;
			
			
		} catch (NamingException ex) {
			log.error("NamingException in " + this.getClass().getName() + " (setPassword): " + ex.getMessage());
		}
    	
    }

    public String getPasswordPicaHash () {
    	return this.passwordPicaHash;
    }

    public String getPasswordPicaHashBase64 () {
    	return new String(Base64.encodeBase64(this.passwordPicaHash.getBytes()));
    }

    public String getPasswordPicaHashMd5 () {
    	MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			log.error("NoSuchAlgorithmException in " + this.getClass().getName() + " (getPasswordPicaHashMd5): " + ex.getMessage());
		}
        md5.reset();
        md5.update(this.passwordPicaHash.getBytes());
        byte[] result = md5.digest();
        
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<result.length; i++) {
        	if(result[i] <= 15 && result[i] >= 0){
        		hexString.append("0");
        	}
        	hexString.append(Integer.toHexString(0xFF & result[i]));
        }
       
    	return new String(hexString.toString());
    }
}
