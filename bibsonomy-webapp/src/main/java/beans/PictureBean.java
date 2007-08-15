/*
 * This class is used to provide picutres for geotagging_entry.jsp
 */

package beans;

import java.io.Serializable;

import resources.Bookmark;
import helpers.database.DBPictureManager;

public class PictureBean implements Serializable {
	
	private static final long serialVersionUID = 3935850662395533527L;
	
	private String lat = "";
	private String lon = "";
	private String latD = "";
	private String lonD = "";
	private Bookmark bookmark;
	
	public PictureBean(){
		bookmark = new Bookmark();
	}
	
	//set username
	public void setRequUser(String user){
		bookmark.setUser(user);
	}
	
	//set hash
	public void setRequHash(String hash) {
		bookmark.setDocHash(hash);
	}

	//get bookmark
	public Bookmark getBookmark(){
		return bookmark;
	}
	
	//set/get lat
	public void setLat(String lat){
		this.lat = lat.substring(0, lat.length()-2);
	}
	public String getLat(){
		return lat;
	}
	
	//set/get lon
	public void setLon(String lon){
		this.lon = lon.substring(0, lon.length()-2);
	}
	public String getLon(){
		return lon;
	}
	
	//set/get latD
	public void setLatD(String value){
		this.latD = value;
	}
	public String getLatD(){
		return this.latD;
	}
	
	//set/get lonD
	public void setLonD(String value){
		this.lonD = value;
	}
	public String getLonD(){
		return this.lonD;
	}
	
	public void getContent() {
		// get bookmarkcontent from database
		DBPictureManager.getBookmarkContent(this);
	}
}