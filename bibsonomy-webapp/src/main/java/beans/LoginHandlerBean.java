/*
 * IDBean is used by processLogin.jsp to check the integrity 
 * and validate the entries on login/retryLogin.jsp
 *
*/
package beans;
import java.util.Hashtable;
import java.io.Serializable;

public class LoginHandlerBean implements Serializable {
	
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3544958770200589111L;
	
	private String userName;
	private String loginPassword;
	private Hashtable<String,String> errors;
	
	public boolean validate() {
	    boolean allOk=true;
	    
	    // % is used as separator in cookie
	    if (userName.equals("") || (userName.indexOf(' ') != -1) || (userName.indexOf('%') != -1)) {
	        errors.put("userName","Please enter a valid username");
	        userName="";
	        allOk=false;
	      }
	    
	    if (loginPassword.equals("") || (loginPassword.indexOf(' ') != -1) ) {
	        errors.put("loginPassword","Please enter a valid password");
	        loginPassword="";
	        allOk=false;
	      }
	
	    return allOk;
	  }
	
	public String getErrorMsg(String s) {
	    String errorMsg =(String)errors.get(s.trim());
	    return (errorMsg == null) ? "":errorMsg;
	  }
	
	public LoginHandlerBean() {
	  	userName="";
	    loginPassword="";
	    errors = new Hashtable<String,String>();
	  }
	
	 public String getUserName() {
	    return userName;
	  }
	 
	 public String getLoginPassword() {
	    return loginPassword;
	  }
	 
	 public void setUserName(String u) {
	    userName=u.toLowerCase();
	  }
	 
	 public void  setloginPassword(String p1) {
	    loginPassword=p1;
	  }

	 public void setErrors(String key, String msg) {
	    errors.put(key,msg);
	  }

}
