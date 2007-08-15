/*
 * This class is used to check the integrity 
 * and validate the entries on various sites
 */

package beans;
import helpers.database.DBPrivnoteManager;

import java.io.Serializable;


public class PrivnoteBean implements Serializable {
	
	private static final long serialVersionUID = 3835150662295433527L;
	
	private String username; // is not written to DB, just for querying
	private String privnote;
	private String oldprivnote;
	private String hash;
	private boolean update = false;

	// check if have something to insert into database, if yes: do it
	public void queryDB() {
		if (update && 
				((privnote == null && oldprivnote != null) ||
			     (privnote != null && (oldprivnote == null || !privnote.equals(oldprivnote))))) {
			// something has changed --> write it to DB
			DBPrivnoteManager.setPrivnoteForUser(this);
		} else {
			// nothing has changed --> old = new
			privnote = oldprivnote;
		}
	}


	public String getPrivnote() {
		return privnote;
	}
	public void setPrivnote(String privnote) {
		update = true;
		this.privnote = privnote;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String getOldprivnote() {
		return oldprivnote;
	}
	public void setOldprivnote(String oldprivnote) {
		this.oldprivnote = oldprivnote;
	}
	
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}

}

