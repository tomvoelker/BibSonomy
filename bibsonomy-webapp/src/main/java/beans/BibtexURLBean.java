package beans;

import helpers.database.DBBibtexURLManager;

import java.io.Serializable;
import java.util.LinkedList;

import resources.BibtexURL;

public class BibtexURLBean implements Serializable {

	private static final long serialVersionUID = -1422995497250818791L;
	private String hash;
	private String currUser;
	private String user;
	private String url;
	private String text;
	private String action;
	private LinkedList<BibtexURL> list = null;
	private boolean validCkey = false;


	public LinkedList<BibtexURL> getBibtexURL () {
		if (list == null) {
			list = DBBibtexURLManager.readURL(hash, user);
		}
		return list;
	}
	
	public boolean getLeer() {
		return getBibtexURL().size() == 0;
	}

	/** Decides, what to do, depending on the value of parameter "action".
	 * 
	 * @return
	 */
	public boolean getActionResult () {
		if ("addURL".equals(action)) {
			return DBBibtexURLManager.createURL (new BibtexURL(url, text), hash, currUser, validCkey);
		} else if ("deleteURL".equals(action)) {
			return DBBibtexURLManager.deleteURL (new BibtexURL(url, text), hash, currUser, validCkey);
		}
		return false;
	}


	public void setHash(String hash) {
		this.hash = hash;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setCurrUser(String currUser) {
		this.currUser = currUser;
	}

	public boolean isValidCkey() {
		return validCkey;
	}

	public void setValidCkey(boolean validCkey) {
		this.validCkey = validCkey;
	}

}
