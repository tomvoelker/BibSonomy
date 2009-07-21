/*
 * This class is used to check the integrity 
 * and validate the entries on various sites
 */

package beans;
import helpers.database.DBUserManager;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;


public class SettingsBean extends UserBean implements Serializable {
	
	private static final long serialVersionUID = 3835150662295433527L;
	
	public static final String CREDENTIAL_KEY="ckey";
	
	// user who wants to get foaf file
	private String currUser	= "";
	
	private String birthday	 = ""; // FIXME: change to date format
	private String gender	 = "";
	private String place 	 = "";
	private String profession= "";
	private String interests = "";
	private String hobbies	 = "";
	private int	   profileGroup = 1; // 0 = public, 1 = private, 2 = friends
	private String action    = ""; // what this bean shall do, at the moment only "update" (i.e. write values to DB)
	private boolean validCkey = false;
	private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private Hashtable<String,String> errors;
	
	public void queryDBFOAF() {
		if (DBUserManager.isProfileViewable(currUser,name)) {
			DBUserManager.getSettingsForUser(this);
		} else {			
			errors.put("general", "You are not a friend of this user");
		}
	}
	
	// inserts the data into the DB, if everything is valid
	public void queryDB() {
		if (action.equals("update")) {
			// user wants to update his data
			if (isValidEmail(this.email) && 
				isValidHomepage(this.homepage) &&
				isValidOpenurl(this.openurl) &&
				isValidBirthday(this.birthday)
			) {
				// data is valid! write data into database
				if (!DBUserManager.setSettingsForUser(this)) {
					errors.put("general", "Could not update your settings.");
				}
			}
		} else {
			DBUserManager.getSettingsForUser(this);
		}
	}
	
	
	private boolean isValidEmail (String e) {
		if (e.equals("") || 
				 e.indexOf(' ') != -1 ||
				 e.indexOf('@') == -1 || 
				 e.lastIndexOf(".") < e.lastIndexOf("@") ||
				 e.lastIndexOf("@") != e.indexOf("@") ||
				 e.length() - e.lastIndexOf(".") < 2	) {
			errors.put("email","Please enter a valid email address");
			return false;
		}
		return true;
	}
	
	private boolean isValidHomepage (String h) {
		if (h.startsWith("http://") || h.equals("")) {
			return true;
		} else {
			errors.put("homepage", "Please enter a valid homepage");
			return false;
		}
	}
	
	private boolean isValidOpenurl (String h) {
		if (h.startsWith("http://") || h.equals("")) {
			return true;
		} else {
			errors.put("openurl", "Please enter a valid openURL");
			return false;
		}
	}
	private boolean isValidBirthday(String birthday) {
	        // allow empty birthday field
		if (birthday != null || birthday.trim().equals("")) return true;

		// if not empty, try to parse date
		try {
			df.setLenient( false );
			df.parse(birthday);			
			return true;
		} catch (ParseException e) {
			errors.put("birthday", "Please enter a valid date");
			return false;
		}
	}
	

	public SettingsBean() {
		errors  = new Hashtable<String,String>();
	}
	
	public Hashtable getErrors() {
		return errors;
	}
	
		
	public void setEmail(String email) {
		super.setEmail(email);
	}
	
	public void setHomepage(String homepage) {
		super.setHomepage(homepage);
	}
	
	// birthday
	public String getBirthday() {
		return birthday;
	}
	
	public Date getBirthdayAsDate() {
		if (birthday == null || birthday.trim().equals("")) return null;
		try {
			return new Date(df.parse(birthday).getTime());
		} catch (ParseException e) {			
		}
		return null;
	}
	
	private void setBirthday(String hdaybirthday) {
		this.birthday = hdaybirthday;		
	}
	
	public void setBirthday(Date birthday) throws ParseException {		
		if (birthday != null)
			setBirthday(df.format(birthday));			
	}

	// homecountry
	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}
	
	// gender
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getGenderLong() {
		if ("m".equals(gender))
			return "male";
		else if ("f".equals(gender))
			return "female";
		else
			return "unknown";			
	}


	// hobbies
	public String getHobbies() {
		return hobbies;
	}

	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
	}

	// interests
	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	// profession
	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	// profile group
	public int getProfileGroup() {
		return profileGroup;
	}

	public void setProfileGroup(int profileGroup) {
		this.profileGroup = profileGroup;
	}

	// action
	public void setAction(String action) {
		this.action = action;
	}

	public boolean isValidCkey() {
		return validCkey;
	}

	public void setValidCkey(boolean validCkey) {
		this.validCkey = validCkey;
	}

	public String getCurrUser() {
		return currUser;
	}

	public void setCurrUser(String currUser) {
		this.currUser = currUser;
	}
	
	// email as SHA1 Hash
	public String getSHA1Email() {
		return getSHA1Hash("mailto:" + email);
	}
	
	/**
	 * returns SHA-1 Hash of given String
	 * @param str the string to be encoded
	 * @return SHA-1 sum
	 */
	private String getSHA1Hash(final String str) {
		String s = "";
		try {
			final MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte [] buffer = md.digest(str.getBytes());
			
	    	for (int i = 0; i < buffer.length; i++) {
	    		String hex = Integer.toHexString ((int) buffer[i]);
	    		if (hex.length() == 1) {
	    			hex = "0" + hex;
	    		}
	    		s += hex.substring(hex.length() - 2);
	    	}		
		} catch (NoSuchAlgorithmException e) {			
			e.printStackTrace();
		}		
		return s;
	}
	
}
