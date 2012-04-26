/*
 * This class is used to check the integrity 
 * and validate the entries on various sites
 */

package beans;
import helpers.database.DBAdminManager;

import java.io.Serializable;
import java.util.LinkedList;


public class AdminBean implements Serializable {
	
	private static final long serialVersionUID = 3835150662295433527L;
	
	private String user   = ""; // user for which we want to add a group or mark as spammer
	private String evaluator = ""; // evaluator for which the entry should take place
	private String action = ""; // what this bean shall do, at the moment only "update" (i.e. write values to DB)
	private String tag = ""; // tag to add or remove from spammertag list
	private String currUser;
	
	private final LinkedList<String> errors;
	private final LinkedList<String> infos;
	
	// inserts the data into the DB, if everything is valid
	public void queryDB() {
		if (action.equals("remove_user") && isValidUser()) {	
			DBAdminManager.removeUserFromSpammerlist(this);
		} else if (action.equals("addtag") && isValidTag()) {
			DBAdminManager.flagSpammerTag(this, true, 1);
		} else if (action.equals("rmvtag") && isValidTag()) {
			DBAdminManager.flagSpammerTag(this, false, 1);
		} else if (action.equals("cleantag")) {
			DBAdminManager.flagSpammerTag(this, true, 0);
		}
	}
	
	// checks, if username is ok
	private boolean isValidUser() {		
		return ! "".equals(user);
	}
	
	// checks the tagname
	private boolean isValidTag() {
		return ! "".equals(tag);
	}
	
	public AdminBean() {
		errors = new LinkedList<String>();
		infos  = new LinkedList<String>();
	}
	
	// errors
	public LinkedList<String> getErrors() {
		return errors;
	}
	
	public void addError (final String error) {
		errors.add(error);
	}
	
	// infos
	public LinkedList<String> getInfos() {
		return infos;
	}
	
	public void addInfo (final String info) {
		infos.add(info);
	}
		
	// action
	public void setAction(final String action) {
		this.action = action;
	}

	// user
	public String getUser() {
		return user;
	}
	public void setUser(final String user) {
		if (user != null) {
			this.user = user.toLowerCase();
		}
	}
	
	// evaluator
	public String getEvaluator() {
		return evaluator;
	}
	public void setEvaluator(final String evaluator) {
		this.evaluator = evaluator;
	}

	// tag
	public String getTag() {
		return tag;
	}

	public void setTag(final String tag) {		
			this.tag = tag;
	}

	public String getCurrUser() {
		return this.currUser;
	}

	public void setCurrUser(final String currUser) {
		this.currUser = currUser;
	}
}

