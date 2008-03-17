package org.bibsonomy.database.managers;

import java.net.InetAddress;
import java.util.Date;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.AdminParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;

/** 
 * Provides functionalities which are typically only available to admins.
 * This might include flagging a user as spammer, setting the status of an 
 * InetAddress (IP), and other things.
 * 
 * @author rja
 * @version $Id$
 */
public class AdminDatabaseManager extends AbstractDatabaseManager {

	
	private static final Logger LOGGER = Logger.getLogger(AdminDatabaseManager.class);
	private final static AdminDatabaseManager singleton = new AdminDatabaseManager();

	private AdminDatabaseManager() {
	}

	/**
	 * @return a singleton instance of this AdminDatabaseManager
	 */
	public static AdminDatabaseManager getInstance() {
		return singleton;
	}
	
	public void addInetAddressStatus(InetAddress address, InetAddressStatus status, DBSession session) {
		final AdminParam param = new AdminParam();
		param.setInetAddress(address);
		param.setInetAddressStatus(status);
		this.insert("addInetAddressStatus", param, session);
	}

	public void deleteInetAdressStatus(InetAddress address, DBSession session) {
		this.delete("deleteInetAddressStatus", address, session);
	}

	public InetAddressStatus getInetAddressStatus(InetAddress address, DBSession session) {
		InetAddressStatus status = (InetAddressStatus) this.queryForObject("getInetAddressStatus", address, session);
		if (status == null) return InetAddressStatus.UNKNOWN;
		return status;
	}
	
	/**
	 * Flags or unflags a user as a spammer
	 * @param user the user to flag
	 * @param updatedBy the admin who flags the user
	 * @param session
	 */
	public void flagSpammer(User user, String updatedBy, DBSession session) {
		final AdminParam param = new AdminParam();
		
		param.setUserName(user.getName());
		param.setSpammer(user.getSpammer());
		param.setUpdatedBy(updatedBy);
		param.setUpdatedAt(new Date());
		
		this.update("flagSpammer", param, session);		
		this.updateGroupIds(param, session);
	}
	
	/**
	 * Updates the groupids of a user to set its posts
	 * viewable or not in dependence of its spammer status
	 * @param param 
	 * @param session
	 */
	public void updateGroupIds(AdminParam param, DBSession session) {
		boolean spammer = (param.getSpammer() == 1) ? true : false;
		
		// private ids
		param.setOldGroupId(UserUtils.getGroupId(GroupID.PRIVATE.getId(), !spammer));
		param.setNewGroupId(UserUtils.getGroupId(GroupID.PRIVATE.getId(), spammer));
		this.updateGroupId(param, session);
		
		// public ids
		param.setOldGroupId(UserUtils.getGroupId(GroupID.PUBLIC.getId(), !spammer));
		param.setNewGroupId(UserUtils.getGroupId(GroupID.PUBLIC.getId(), spammer));
		this.updateGroupId(param, session);
		
		// friend ids
		param.setOldGroupId(UserUtils.getGroupId(GroupID.FRIENDS.getId(), !spammer));
		param.setNewGroupId(UserUtils.getGroupId(GroupID.FRIENDS.getId(), spammer));
		this.updateGroupId(param, session);		
	}	
	
	private void updateGroupId(AdminParam param, DBSession session) {
		this.update("updateTasGroupIds", param, session);
		this.update("updateBookmarkGroupIds", param, session);
		this.update("updateBibtexGroupIds", param, session);
		this.update("updateSearchBookmarkGroupIds", param, session);
		this.update("updateSearchBibtexGroupIds", param, session);		
	}
}