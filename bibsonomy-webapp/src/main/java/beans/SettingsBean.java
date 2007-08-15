/*
 * This class is used to check the integrity 
 * and validate the entries on various sites
 */

package beans;
import helpers.database.DBUserManager;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;


public class SettingsBean implements Serializable {
	
	private static final long serialVersionUID = 3835150662295433527L;
	
	public static final String CREDENTIAL_KEY="ckey";
	
	private String username  = ""; // is not written to DB, just for querying
	private String homepage  = "";
	private String email     = "";
	private String realname  = "";
	private String openurl   = ""; // BASE_URL for this users openURL service (http://www.exlibrisgroup.com/sfx_openurl_syntax.htm)
	private String birthday	 = ""; // FIXME: change to date format
	private String gender	 = "";
	private String country	 = "";
	private String profession= "";
	private String interests = "";
	private String hobbies	 = "";
	private int	   profileGroup = 1; // 0 = public, 1 = private, 2 = friends
	private String action    = ""; // what this bean shall do, at the moment only "update" (i.e. write values to DB)
	private boolean validCkey = false;
	private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private Hashtable<String,String> errors;
	
	// gets settings from DB, if we are not doing an update
	private void getSettingsFromDB () {
		// email empty (got not filled from DB) and we don't do an update
		if (email.equals("") && !action.equals("update")) {
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
		try {
			df.setLenient( false );
			df.parse(birthday);			
			return true;
		} catch (ParseException e) {
			errors.put("birthday", "Please enter a valid date");
			return false;
		}
	}
	
	// inserts the data into the DB, if everything is valid
	public void queryDB() {
		if (action.equals("update") && 
				isValidEmail(this.email) && 
				isValidHomepage(this.homepage) &&
				isValidOpenurl(this.openurl) &&
				isValidBirthday(this.birthday)
				) {
			// write data into database
			if (!DBUserManager.setSettingsForUser(this)) {
				errors.put("general", "Could not update your settings. Please check your password.");
			}
		}
	}
	
	public SettingsBean() {
		errors = new Hashtable<String,String>();
	}
	
	public Hashtable getErrors() {
		return errors;
	}
	
	// email 
	public String getEmail() {
		getSettingsFromDB();
		return email;
	}	
	public void setEmail(String e) {
		this.email = e.trim();
	}
	
	// home page
	public String getHomepage() {
		getSettingsFromDB();
		return homepage;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage.trim();
	}

	// real name
	public String getRealname() {
		getSettingsFromDB();
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}

	// user name
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
//	 birthday
	public String getBirthday() {
		getSettingsFromDB();
		return birthday;
	}
	
	public Date getBirthdayAsSQLDate() {		
		try {
			return new java.sql.Date(df.parse(birthday).getTime());
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		return new java.sql.Date(0);
	}
	
	public void setBirthday(String birthday) {
		this.birthday = birthday;		
	}
	
	public void setBirthday(Date birthday) throws ParseException {		
		if (birthday != null)
			setBirthday(df.format(birthday));			
	}

	// homecountry
	public String getCountry() {
		getSettingsFromDB();
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	// gender
	public String getGender() {
		getSettingsFromDB();
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	// hobbies
	public String getHobbies() {
		getSettingsFromDB();
		return hobbies;
	}

	public void setHobbies(String hobbies) {
		this.hobbies = hobbies;
	}

	// interests
	public String getInterests() {
		getSettingsFromDB();
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	// profession
	public String getProfession() {
		getSettingsFromDB();
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	// profile group
	public int getProfileGroup() {
		getSettingsFromDB();
		return profileGroup;
	}

	public void setProfileGroup(int profileGroup) {
		this.profileGroup = profileGroup;
	}

	// action
	public void setAction(String action) {
		this.action = action;
	}

	public String getOpenurl() {
		getSettingsFromDB();
		return openurl;
	}

	public void setOpenurl(String openurl) {
		this.openurl = openurl;
	}

	public boolean isValidCkey() {
		return validCkey;
	}

	public void setValidCkey(boolean validCkey) {
		this.validCkey = validCkey;
	}	
}