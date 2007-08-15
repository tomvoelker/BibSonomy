package beans;

import helpers.database.DBFolkrankManager;

import java.io.Serializable;
import java.util.LinkedList;

import resources.FolkrankItem;

/**
 * Stores ranking results of folkrank algorithm 
 */
public class FolkrankBean implements Serializable {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -1672590835777813008L;
	
	/**
	 * request attributes tags, users, and recources
	 */
	private String requTag 		= "";
	private String requUser 	= "";
	private String requResource = "";
	
	/**
	 * sets of folkranked items to the requested item
	 */	
	private LinkedList<FolkrankItem> tags;
	private LinkedList<FolkrankItem> users;
	private LinkedList<FolkrankItem> resources;	
	
	/**
	 * Max number of results
	 */
	private final int LIMIT = 50;
	
	public FolkrankBean() {
		tags 		= new LinkedList<FolkrankItem>();
		users 		= new LinkedList<FolkrankItem>();
		resources 	= new LinkedList<FolkrankItem>();
	}
	
	/**
	 * Getter and Setter	 
	 */
	public LinkedList<FolkrankItem> getResources() {
		return resources;
	}
	
	public LinkedList<FolkrankItem> getTags() {
		if (requTag != null) {			
			tags = DBFolkrankManager.getRankingSet(requTag, 0, 0, 0, LIMIT);			
		}		
		return tags;
	}
	
	public LinkedList<FolkrankItem> getUsers() {
		if (requTag != null) {
			users = DBFolkrankManager.getRankingSet(requTag, 0, 1, 0, LIMIT);
		}
		return users;
	}
		
	public void setRequResource(String requResource) {
		this.requResource = requResource;
	}
	
	public void setRequTag(String requTag) {
		this.requTag = requTag;
	}
	
	public void setRequUser(String requUser) {
		this.requUser = requUser;
	}
}