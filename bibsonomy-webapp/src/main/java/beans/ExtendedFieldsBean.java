package beans;

import helpers.database.DBExtendedFieldGetManager;

import java.io.Serializable;
import java.util.LinkedList;

public class ExtendedFieldsBean implements Serializable {

	private static final long serialVersionUID = -6213938254172744859L;
	private String currUser;
	private String hash;
	
	public LinkedList<String> getFields() {
		return DBExtendedFieldGetManager.getExtendedFields(currUser, hash);
	}

	public void setCurrUser(String currUser) {
		this.currUser = currUser;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	
}
