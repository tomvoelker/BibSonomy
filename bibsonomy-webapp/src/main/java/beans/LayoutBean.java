/*
 * This class is used to check the integrity 
 * and validate the entries on various sites
 */

package beans;

import helpers.database.DBLayoutManager;

import java.io.Serializable;

/**
 * Still used to display the active layouts on the /settings page.
 * 
 * 
 * @author rja
 *
 */
public class LayoutBean implements Serializable {

	private static final long serialVersionUID = 5842480477147103044L;

	private String username  = ""; 

	private String beginName = null;
	private String beginHash = null;
	private String itemName = null;
	private String itemHash = null;
	private String endName = null;
	private String endHash = null;
	
	private String error = null;
	
	public String getBeginHash() {
		return beginHash;
	}
	public void setBeginHash(String beginHash) {
		this.beginHash = beginHash;
	}
	public String getBeginName() {
		return beginName;
	}
	public void setBeginName(String beginName) {
		this.beginName = beginName;
	}
	public String getEndHash() {
		return endHash;
	}
	public void setEndHash(String endHash) {
		this.endHash = endHash;
	}
	public String getEndName() {
		return endName;
	}
	public void setEndName(String endName) {
		this.endName = endName;
	}
	public String getItemHash() {
		return itemHash;
	}
	public void setItemHash(String itemHash) {
		this.itemHash = itemHash;
	}
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
		DBLayoutManager.getLayoutSettingsForUser(this);
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}		
}

