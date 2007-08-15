package beans;

import java.util.LinkedList;
import java.io.Serializable;
import resources.TagRelation;
import helpers.database.DBRelationGetManager;
import helpers.database.DBRelationShowManager;

/**
 * Contains tag relations to show on JSP pages.
 *
 */
public class RelationBean implements Serializable {
	
	private static final long serialVersionUID = 3257850961094522929L;
	private LinkedList<TagRelation> relations; 
	/**
	 * Is <code>true</code>, if it contains the last relation in the database.
	 */
	private boolean allRelRows = false;
	private String requUser;
	/**
	 * The number of relations which are in the database.
	 */
	private int total;
	/**
	 * The number of the relation which should be the first to get from the database.
	 * ( = SQL OFFSET)
	 */
	private int startRel = 0;
	/**
	 * The number of relations which should be stored (shown at once).
	 * ( = SQL LIMIT)
	 */
	private int items = 10;
	
	public RelationBean() {
		relations = null;
	}
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

		
	public boolean isAllRelRows() {
		return allRelRows;
	}

	public void setAllRelRows(boolean allRelRows) {
		this.allRelRows = allRelRows;
	}

	public LinkedList<TagRelation> getRelations() {
		return relations;
	}
	
	public LinkedList<TagRelation> getAllRelations() {
		if (relations == null) {
			relations = DBRelationGetManager.getPopularRelations();
		}
		return relations;
	}
	
	/**
	 * Get all the relations the user wants to be shown.
	 * @return a list of all relations the user has has selected to be shown
	 */
	public LinkedList<TagRelation> getShownRelations() {
		if (relations == null) {
			relations = DBRelationGetManager.getRelations(requUser, true);
		}
		return relations;
	}
	
	/**
	 * Get all relations of the user.
	 * @return a list of all relations of the user
	 */
	public LinkedList<TagRelation> getUserRelations() {
		if (relations == null) {
			relations = DBRelationGetManager.getRelations(requUser, false);
		}
		return relations;
	}
	
	/**
	 * Get some relations of the user, for browsing/navigating.
	 * @return a list of all relations from startRel to startRel + items
	 */
	public LinkedList<TagRelation> getLimitedRelations() {
		if (relations == null) {
			DBRelationGetManager.getRelations(this);
		}
		return relations;
	}
	
	public void setRelations(LinkedList<TagRelation> relations) {
		this.relations = relations;
	}

	public int getRelationsCount(){
		return relations.size();
	}

	public String getRequUser() {
		return requUser;
	}

	public void setRequUser(String user) {
		this.requUser = user;
	}
	
	/** 
	 * Shows the given tag.
	 * This is used by AJAX.
	 * @param tag the super tag to be shown
	 */
	public void setShow (String tag) {
		DBRelationShowManager.showConcept(tag, requUser);
	}
	

	/** 
	 * Hides the given tag.
	 * This is used by AJAX.
	 * @param tag the super tag to be hidden
	 */
	public void setHide (String tag) {
		DBRelationShowManager.hideConcept(tag, requUser);
	}
	
	/**
	 * show (or hide) all relations of the user.
	 * @param to parameter which decides, if relations are shown or hidden. 
	 *   <code>show</code> : show all
	 *   <code>hide</code> : hide all
	 */
	public void setAll (String to) {
		if ("show".equals(to)) {
			DBRelationShowManager.showAll(requUser);
		} else if ("hide".equals(to)) {
			DBRelationShowManager.hideAll(requUser);
		}
	}
	
	public int getItems() {
		return items;
	}
	public void setItems(int items) {
		this.items = items;
	}
	public int getStartRel() {
		return startRel;
	}
	public void setStartRel(int startRel) {
		this.startRel = startRel;
	}

	public boolean isHaveRelRows () {
		return relations.size() > 0;
	}
	
}// end class