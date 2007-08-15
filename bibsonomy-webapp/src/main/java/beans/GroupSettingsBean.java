/*
 * This class is used to check the integrity 
 * and validate the entries on various sites
 */

package beans;

import helpers.database.DBGroupSettingsManager;

import java.io.Serializable;


public class GroupSettingsBean implements Serializable {
	
	private static final long serialVersionUID = 3835150662295433527L;
	
	private String username  = ""; // is not written to DB, just for querying
	private int privlevel    = -1;
	private String action    = ""; // what this bean shall do, at the moment only "update" (i.e. write values to DB)
		
	
	// inserts the data into the DB, if everything is valid
	public void queryDB() {
		if (action.equals("update_privlevel") && isValidPrivlevel()) {
			// write data into database
			DBGroupSettingsManager.setPrivlevel(this);
		}
	}

	private boolean isValidPrivlevel () {
		return privlevel >=0 && privlevel <= 2;
	}

	
	// user name
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	// privlevel
	public int getPrivlevel() {
		if (privlevel == -1)  {
			DBGroupSettingsManager.getPrivlevel(this);
		}
	  return privlevel;
	}
	public void setPrivlevel(int privlevel) {
		this.privlevel = privlevel;
	}
	
	// action
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
}

