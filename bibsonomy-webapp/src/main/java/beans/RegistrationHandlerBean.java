/*
 * This class is used to check the integrity 
 * and validate the entries on various sites
 */

package beans;
import java.io.Serializable;
import java.util.Hashtable;




public class RegistrationHandlerBean implements Serializable {
	
	
	
	private static final long serialVersionUID = 3835150662295433527L;
	
	private String userName  = "";
	private String realName  = "";
	private String homepage  = "";
	private String email     = "";
	private String currPass  = "";
	private String password1 = "";
	private String password2 = "";
	private String action 	 = "";
	
	private Hashtable<String,String> errors;
	private boolean passwordChange = false;
	private boolean passwordChangeReminder = false;
	private boolean passwordReminder = false;

	
	public boolean isValid() {
		return isValidUserName() & isValidEmail() & isValidPassword1() & isValidPassword2() & isValidHomepage();
	}
	
	/*
	 * TODO: was macht das?
	 */
	public boolean isValidReminder() {
		if(action.equals("reminder")) {
			passwordReminder = true;
			return isValidUserName() & isValidEmail() && errors.isEmpty();
		} else
			return false;
	}
	
	public boolean validateLight () {
		passwordChange = true;
		System.out.println("RHB: validateLight()");
		return isValidCurrPass() && isValidPassword1() && isValidPassword2();
	}
	
	/*
	 * TODO: was macht das?
	 */	
	public boolean isPasswordChangeOnRemind () {
		if(action.equals("change")) {
			passwordChangeReminder = true;
			return isValidPassword1() & isValidPassword2();
		} else
			return false;
	}
	
	private boolean isValidCurrPass () {
		if (currPass.equals("") || currPass.indexOf(' ') != -1) {
			errors.put("currPass", "Please enter your current password");
			currPass = "";
			return false;
		}
		return true;
	}
	
	private boolean isValidEmail () {
		if (email == null ||
				"".equals(email) || 
				email.indexOf(' ') != -1 ||
				email.indexOf('@') == -1 || 
				email.length() > 255 ||
				email.lastIndexOf(".") < email.lastIndexOf("@") ||
				email.lastIndexOf("@") != email.indexOf("@") ||
				email.length() - email.lastIndexOf(".") < 2	) {
			errors.put("email","Please enter a valid email address");
			email="";
			return false;
		}
		return true;
	}
	
	private boolean isValidHomepage() {
		if (homepage == null || homepage.startsWith("http://") || homepage.equals("")) {
			return true;
		} else {
			homepage = "";
			errors.put("homepage", "Please enter a valid homepage");
			return false;
		}
	}
	
	private boolean isValidPassword1 () {
		if (password1 == null || password1.equals("") || password1.indexOf(' ') != -1) {
			errors.put("password1","Please enter a valid password");
			password1="";
			return false;
		}
		return true;
	}
	
	private boolean isValidPassword2 () {
		if (password2 == null || (!password1.equals("") && (password2.equals("")) || !password1.equals(password2))) {
			errors.put("password2","Please confirm your password");
			password2="";
			return false;
		}
		return true;
	}
	
	private boolean isValidUserName () {
		/* username must not contain %, otherwise cookie auth does not work, 
		 * because %20 separates username from password in cookie auth */
		if (userName == null ||
				"".equals(userName) ||
				"public".equals(userName) ||
				"private".equals(userName) ||
				"friends".equals(userName) ||
				"null".equals(userName) ||
				userName.length()      > 30 ||
				userName.matches(".*\\s.*") ||
				userName.indexOf('-') != -1 ||
				userName.indexOf('+') != -1 ||
				userName.indexOf('/') != -1 ||
				userName.indexOf(':') != -1 ||
				userName.indexOf('&') != -1 ||
				userName.indexOf('?') != -1 ||
				userName.indexOf('"') != -1 ||
				userName.indexOf('\'') != -1 ||
				userName.indexOf('>') != -1 ||
				userName.indexOf('<') != -1 ||
				userName.indexOf('%') != -1) {
			errors.put("userName","Please enter a valid username");
			userName = "";
			return false;
		}
		return true;
	}
	
	public RegistrationHandlerBean() {
		errors = new Hashtable<String,String>();
	}
	
	public Hashtable getErrors () {
		return errors;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword1() {
		return password1;
	}
	
	public String getPassword2() {
		return password2;
	}
	
	public void setUserName(String u) {
		if (u != null) userName = u.toLowerCase();
	}
	
	public void setEmail(String eml) {
		email=eml;
	}
	
	
	public void  setPassword1(String p1) {
		password1=p1;
	}
	
	public void  setPassword2(String p2) {
		password2=p2;
	}
	
	public void setErrors(String key, String msg) {
		errors.put(key,msg);
	}

	public String getCurrPass() {
		return currPass;
	}

	public void setCurrPass(String currPass) {
		this.currPass = currPass;
	}

	public boolean isPasswordChange() {
		return passwordChange;
	}

	public void setPasswordChange(boolean changePassword) {
		this.passwordChange = changePassword;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public boolean isPasswordReminder() {
		return passwordReminder;
	}
	
	public boolean isPasswordChangeReminder() {
		return passwordChangeReminder;
	}
	
	public void setPasswordReminder(boolean b) {
		this.passwordReminder = b;
	}
	
	public void setAction(String s) {
		this.action = s;
	}
	
}

