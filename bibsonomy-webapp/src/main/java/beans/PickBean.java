package beans;

import helpers.database.DBPickManager;

import java.io.Serializable;

/**
 * Contains tag relations to show on JSP pages.
 *
 */
public class PickBean implements Serializable {
	
	private static final long serialVersionUID = 3257850961094522929L;
	private String user;
	private String currUser;
	private String pick;
	private String unpick;
	
	public int getPickCount () {
		/* pick */
		if (pick != null && currUser != null) {
			DBPickManager.pickEntryForUser(pick, user, currUser);
		}
		/* unpick */
		if (unpick != null && currUser != null) {
			DBPickManager.unPickEntryForUser(unpick, user, currUser);
		}
		
		/* return count */
		return DBPickManager.getPickCount(currUser);
	}

	public void setCurrUser(String currUser) {
		this.currUser = currUser;
	}

	public void setUser(String owner) {
		this.user = owner;
	}

	public void setPick(String pick) {
		this.pick = pick;
	}

	public void setUnpick(String unpick) {
		this.unpick = unpick;
	}
	
}