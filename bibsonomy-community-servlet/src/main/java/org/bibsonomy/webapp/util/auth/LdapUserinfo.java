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
	private String email = ""; 
	private String location = "";
	private String passwordHash = "";


	/**
	 * generates a pica-"hashed" value from string
	 * this function will add prefix "{PICA}" to hash
	 * and convert result with base64  
	 * @param s string to hash for pica
	 * @return Base64 Picahash
	 */
	public String generatePicaHashBase64(String s) {
		return new String(Base64.encodeBase64(generatePicaHash(s).getBytes()));
	}

	/**
	 * generates a pica-"hashed" value from string
	 * this function will add prefix "{PICA}" to hash
	 * @param s string to hash for pica
	 * @return pica hash
	 */
	public String generatePicaHash(String s) {
		String prefix = "{PICA}";
		byte[] b = s.getBytes();

		Integer hash = 0;

		for (int i=0; i < b.length; i++) {
			hash += b[i]*(i+1);
		}
		
		return prefix + hash.toString();
	}
	

	public boolean checkPasswordHash(String password) {
		boolean authOk = false;
		
		//check hash method
		String hashMethod = this.getPasswordHash().substring(this.getPasswordHash().indexOf("{")+1, this.getPasswordHash().indexOf("}"));
		log.info("hash method ---> " + hashMethod);
		/*
		 * TODO: crypt auth is broken
		 * how to generate an equal crypt hash according to stored ldap crypt hash
		 */
/*		
		log.info("~~~~~~~~ password:"+password);
		log.info("~~~~~~~~ generateMd5(this.getPasswordHash(),1):"+generateMd5(this.getPasswordHash(),1));
		log.info("~~~~~~~~ this.getPasswordHash():"+this.getPasswordHash());
		log.info("~~~~~~~~ generatePicaHash(password):"+generatePicaHash(password));
		log.info("~~~~~~~~ this.getPasswordHashBase64():"+this.getPasswordHashBase64());
		log.info("~~~~~~~~ generateMd5(password,0):"+generateMd5(password,0));
*/
		
		// check plaintext password with ldap pica password hash 
		if ( hashMethod.equals("PICA") && this.getPasswordHash().equals(generatePicaHash(password))) {
			authOk = true;
		// check md5 pica password hash (from cookie) with ldap pica password hash 	
		} else if ( hashMethod.equals("PICA") && generateMd5(this.getPasswordHash(),1).equals(password)) {
			authOk = true;
		// check crypt/md5 password with ldap crypt password
		//} else if ( true || hashMethod.equals("crypt") && this.getPasswordHashBase64().equals(generateMd5(password,0))) {
		//	authOk = true;
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
	public String getEmail() {
		return this.email;
	}

	/**  
	 * Set eMail. If it is null, assign an empty String
	 * Converts Attribute to String
	 * @param eMail 
	 */
	public void setEmail(String email) {
		if (null==email) email="";
		this.email = email;
	}

	/**  
	 * Set eMail. If it is null, assign an empty String
	 * Converts Attribute to String
	 * @param eMail 
	 */
	public void setEmail(Attribute email) {
		String emailS = "";
		if (null!=email) try {
			emailS = email.get().toString();
		} catch (NamingException ex) {
			log.error("NamingException in " + this.getClass().getName() + " (seteMail): " + ex.getMessage());
		}
		this.setEmail(emailS);
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

    public void setPasswordHash (Attribute password) {
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
			
	    	this.passwordHash = decodedString;
			
			
		} catch (NamingException ex) {
			log.error("NamingException in " + this.getClass().getName() + " (setPassword): " + ex.getMessage());
		}
    	
    }

    public String getPasswordHash () {
    	return this.passwordHash;
    }

    public String getPasswordHashBase64 () {
    	return new String(Base64.encodeBase64(this.passwordHash.getBytes()));
    }

    public String getPasswordHashMd5Hex () {
    	return generateMd5(this.passwordHash,1);
    }

    public String getPasswordHashMd5Base64 () {
    	return generateMd5(this.passwordHash,0);
    }
    
    /**
     * generates md5 string <br />
     * if parameter format is 0 return base64 encoded md5 string<br />
     * otherwise (return != 0) return hex encoded md5 string <br />
     * 
     * @param s
     * @param format
     * @return encoded md5 string
     */
    public String generateMd5 (String s, int format) {
    	MessageDigest md5 = null;
    	String returnValue = null;
    	
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			log.error("NoSuchAlgorithmException in " + this.getClass().getName() + ": " + ex.getMessage());
		}
        md5.reset();
        md5.update(s.getBytes());
        byte[] result = md5.digest();
        
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i<result.length; i++) {
        	if(result[i] <= 15 && result[i] >= 0){
        		hexString.append("0");
        	}
        	hexString.append(Integer.toHexString(0xFF & result[i]));
        }
       
        if (format == 0) {
        	returnValue = new String(Base64.encodeBase64(result));
        } else {
        	returnValue = new String(hexString.toString());
        }
    	return returnValue;
    }
    

}
