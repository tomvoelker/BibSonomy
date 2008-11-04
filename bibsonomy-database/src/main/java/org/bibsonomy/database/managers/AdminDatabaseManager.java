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
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.database.params.AdminParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.UserUtils;

/**
 * Provides functionalities which are typically only available to admins. This
 * might include flagging a user as spammer, setting the status of an
 * InetAddress (IP) and other things.
 * 
 * @author Robert Jaeschke
 * @author Stefan Stützer
 * @version $Id$
 */
public class AdminDatabaseManager extends AbstractDatabaseManager {

	private final static AdminDatabaseManager singleton = new AdminDatabaseManager();
	protected static final Logger log = Logger.getLogger(AdminDatabaseManager.class);

	private AdminDatabaseManager() {
	}

	/**
	 * @return a singleton instance of this AdminDatabaseManager
	 */
	public static AdminDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Returns an InetAddressStatus.
	 * 
	 * @param address
	 * @param session
	 * @return InetAddressStatus
	 */
	public InetAddressStatus getInetAddressStatus(final InetAddress address, final DBSession session) {
		InetAddressStatus status = (InetAddressStatus) this.queryForObject("getInetAddressStatus", address, session);
		if (status == null) return InetAddressStatus.UNKNOWN;
		return status;
	}

	/**
	 * Adds an InetAddressStatus.
	 * 
	 * @param address
	 * @param status
	 * @param session
	 */
	public void addInetAddressStatus(final InetAddress address, final InetAddressStatus status, final DBSession session) {
		final AdminParam param = new AdminParam();
		param.setInetAddress(address);
		param.setInetAddressStatus(status);
		this.insert("addInetAddressStatus", param, session);
	}

	/**
	 * Deletes an InetAddressStatus.
	 * 
	 * @param address
	 * @param session
	 */
	public void deleteInetAdressStatus(final InetAddress address, final DBSession session) {
		this.delete("deleteInetAddressStatus", address, session);
	}

	/**
	 * Flags or unflags a user as a spammer
	 * 
	 * @param user
	 *            the user to flag
	 * @param updatedBy
	 *            the admin who flags the user
	 * @param session
	 *            db session
	 * @return user name
	 */
	public String flagSpammer(final User user, final String updatedBy, final DBSession session) {
		return this.flagSpammer(user, updatedBy, "off", session);
	}

	/**
	 * Flags or unflags a user as a spammer
	 * 
	 * @param user
	 *            the user to flag
	 * @param updatedBy
	 *            the admin who flags the user
	 * @param testMode
	 *            testmode active/inactive
	 * @param session
	 *            db session
	 * @return user name
	 */
	public String flagSpammer(final User user, final String updatedBy, final String testMode, final DBSession session) {
		final AdminParam param = new AdminParam();

		param.setUserName(user.getName());
		param.setSpammer(user.getSpammer());
		param.setToClassify(user.getToClassify());

		/*
		 * set prediction
		 */
		if (user.getPrediction() == null) {
			/*
			 * map boolean to int
			 */
			if (user.isSpammer()) {
				user.setPrediction(1);
			} else {
				user.setPrediction(0);
			}
		}

		param.setPrediction(user.getPrediction());
		param.setConfidence(user.getConfidence());
		param.setMode(user.getMode());
		param.setAlgorithm(user.getAlgorithm());
		param.setUpdatedBy(updatedBy);
		param.setUpdatedAt(new Date());

		if (!updatedBy.equals("classifier")) {
			this.update("flagSpammer", param, session);
			this.updateGroupIds(param, session);
		} else if (testMode.equals("off")) {

			// only change user settings when prediction changes
			if (checkPredictionChange(param, session)) {
				this.update("flagSpammer", param, session);
				this.updateGroupIds(param, session);
			}
		}

		if (checkPredictionChange(param, session)) {
			this.insert("logPrediction", param, session);
			this.insert("logCurrentPrediction", param, session);
		}

		return user.getName();
	}

	/**
	 * checks if the last prediction of the classifier or admin is the same as
	 * the current one
	 * 
	 * @param param
	 * @param session
	 * 
	 * @return true, if prediction and confidence change, false if values are
	 *         the same
	 */

	public boolean checkPredictionChange(final AdminParam param, final DBSession session) {

		if (param.getConfidence() != null && param.getPrediction() != null) {
			// check if prediction and confidence values changed, only update if
			// they changed
			List<User> history = getClassifierHistory(param.getUserName(), session);

			for (int i = 0; i < history.size(); i++) {
				// last predictor needs to be the same as this predictor
				if (history.get(i).getConfidence() != null && history.get(i).getPrediction() != null) {
					
					if (history.get(i).getAlgorithm().equals(param.getAlgorithm())) {
						
						double newConf = Math.round(param.getConfidence());
						double oldConf = Math.round(history.get(i).getConfidence());
						
						
						if (oldConf == newConf && param.getPrediction().compareTo(history.get(i).getPrediction()) == 0) {
							return false;
						}
						// first entry is not the same
						return true;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Updates the groupids of a user to set its posts viewable or not in
	 * dependence of its spammer status
	 * 
	 * @param param
	 * @param session
	 */
	public void updateGroupIds(final AdminParam param, final DBSession session) {
		final boolean spammer = param.isSpammer();

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

	private void updateGroupId(final AdminParam param, final DBSession session) {
		this.update("updateTasGroupIds", param, session);
		this.update("updateBookmarkGroupIds", param, session);
		this.update("updateBibtexGroupIds", param, session);
		this.update("updateSearchBookmarkGroupIds", param, session);
		this.update("updateSearchBibtexGroupIds", param, session);
	}

	/**
	 * Returns all users that are classified to the specified state by the given
	 * classifier
	 * 
	 * @param classifier
	 *            something that classfied the user
	 * @param status
	 *            the state to which the user was classified
	 * @param interval
	 * @param session
	 *            the db session
	 * @return list of users
	 */
	public List<User> getClassifiedUsers(final Classifier classifier, final SpamStatus status, final int interval, DBSession session) {
		final AdminParam param = new AdminParam();
		param.setInterval(interval);
		param.setLimit(100);

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
	 * @param settingsKey
	 *            the setting
	 * @param session
	 *            db session
	 * @return current value for setting
	 */
	public String getClassifierSettings(final ClassifierSettings settingsKey, final DBSession session) {
		return this.queryForObject("getClassifierSettings", settingsKey.toString(), String.class, session);
	}

	/**
	 * Updtaes a setting value
	 * 
	 * @param key
	 *            setting
	 * @param value
	 *            the new value
	 * @param session
	 *            db session
	 */
	public void updateClassifierSettings(final ClassifierSettings key, final String value, final DBSession session) {
		final AdminParam param = new AdminParam();
		param.setKey(key.toString());
		param.setValue(value);
		this.update("updateClassifierSettings", param, session);
	}

	/**
	 * Returns number of classfied user
	 * 
	 * @param classifier
	 *            the classifier
	 * @param status
	 *            the status classifed
	 * @param interval
	 *            the time period of classifications
	 * @param session
	 *            db session
	 * @return count of users
	 */
	public Integer getClassifiedUserCount(final Classifier classifier, final SpamStatus status, final int interval, final DBSession session) {
		final AdminParam param = new AdminParam();
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
	 * @param userName
	 *            the username
	 * @param session
	 *            db session
	 * @return the prediction history
	 */
	public List<User> getClassifierHistory(final String userName, final DBSession session) {
		return this.queryForList("getClassifierHistory", userName, User.class, session);
	}

	/**
	 * Retrieves a comparison of classification results of admins and the
	 * automatic classifier
	 * 
	 * @param interval
	 *            the time period of classifications
	 * @param session
	 *            db session
	 * @return Userlist with spammer flag of admin and prediction of classifier
	 */
	public List<User> getClassifierComparison(final int interval, final DBSession session) {
		final AdminParam param = new AdminParam();
		param.setInterval(interval);
		param.setLimit(100);
		return this.queryForList("getClassifierComparison", param, User.class, session);
	}
}