package org.bibsonomy.database.managers;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.AdminParam;
import org.bibsonomy.database.plugin.DatabasePluginRegistry;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;

/** 
 * Provides functionalities which are typically only available to admins.
 * This might include flagging a user as spammer, setting the status of an 
 * InetAddress (IP), and other things.
 * 
 * @author rja
 * @author sts
 * @version $Id$
 */
public class AdminDatabaseManager extends AbstractDatabaseManager {

	
	private static final Logger LOGGER = Logger.getLogger(AdminDatabaseManager.class);
	private final DatabasePluginRegistry plugins;
	private final static AdminDatabaseManager singleton = new AdminDatabaseManager();

	private AdminDatabaseManager() {
		plugins = DatabasePluginRegistry.getInstance();
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
	public String flagSpammer(User user, String updatedBy, DBSession session) {
		return this.flagSpammer(user, updatedBy, "off", session);
	}
	
	public String flagSpammer(User user, String updatedBy, String testMode, DBSession session) {
		final AdminParam param = new AdminParam();
		
		param.setUserName(user.getName());
		param.setSpammer(user.getSpammer());
		param.setToClassify(user.getToClassify());
		
		param.setPrediction(user.getPrediction() != null ? user.getPrediction() : user.getSpammer());
		param.setMode(user.getMode());
		param.setAlgorithm(user.getAlgorithm());
		param.setUpdatedBy(updatedBy);
		param.setUpdatedAt(new Date());
		
		if (!updatedBy.equals("classifier") || testMode.equals("off")) {
			this.update("flagSpammer", param, session);		
			this.updateGroupIds(param, session);
		}
		
		this.insert("logPrediction", param, session);		
		return user.getName();		
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

	/**
	 * Returns all users that are classified to the specified state by
	 * the given classifier 
	 * 
	 * @param classifier something that classfied the user
	 * @param status the state to which the user was classified
	 * @param session the db session
	 */
	public List<User> getClassifiedUsers(final Classifier classifier, final SpamStatus status, final int interval, DBSession session) {
		AdminParam param = new AdminParam();
		param.setInterval(interval);
		param.setLimit(50);
		
		if (classifier.equals(Classifier.ADMIN) && (status.equals(SpamStatus.SPAMMER) || status.equals(SpamStatus.NO_SPAMMER))) {
			param.setPrediction(status.getId());
			return this.queryForList("getAdminClassifiedUsers", param, User.class, session);			
		} else if (classifier.equals(Classifier.CLASSIFIER)) {
			param.setPrediction(status.getId());
			return this.queryForList("getClassifiedUsers", param, User.class, session);			
		}		
		return null;
	}
	
	/**
	 * Retrieves the setting value for the specified setting 
	 * 
	 * @param settingsKey the setting
	 * @param session db session
	 * @return current value for setting
	 */
	public String getClassifierSettings(final ClassifierSettings settingsKey, final DBSession session) {
		String key = settingsKey.toString();
		return this.queryForObject("getClassifierSettings", key , String.class, session);	
	}
	
	/**
	 * Updtaes a setting value
	 * 
	 * @param key setting
	 * @param value the new value
	 * @param session db session
	 */
	public void updateClassifierSettings(final ClassifierSettings key, final String value, final DBSession session) {
		AdminParam param = new AdminParam();
		param.setKey(key.toString());
		param.setValue(value);
		
		this.update("updateClassifierSettings", param, session);
	}

	/**
	 * Returns number of classfied user
	 * 
	 * @param classifier the classifier
	 * @param status the status classifed
	 * @param interval the time period of classifications 
	 * @param session db session
	 * @return count of users
	 */
	public Integer getClassifiedUserCount(final Classifier classifier, final SpamStatus status, final int interval, final DBSession session) {
		AdminParam param = new AdminParam();
		param.setInterval(interval);
		
		if (classifier.equals(Classifier.ADMIN) && (status.equals(SpamStatus.SPAMMER) || status.equals(SpamStatus.NO_SPAMMER))) {
			param.setPrediction(status.getId());
			return this.queryForObject("getAdminClassifiedUsersCount", param, Integer.class, session);			
		} else if (classifier.equals(Classifier.CLASSIFIER)) {
			param.setPrediction(status.getId());
			return this.queryForObject("getClassifiedUsersCount", param, Integer.class, session);			
		}		
		return null;
	}
	
	/**
	 * Returns the history of classifier predictions
	 * 
	 * @param userName the username
	 * @param session db session
	 * @return the prediction history
	 */
	public List<User> getClassifierHistory(final String userName, final DBSession session) {		
		return this.queryForList("getClassifierHistory", userName, User.class, session);
		
	}
}