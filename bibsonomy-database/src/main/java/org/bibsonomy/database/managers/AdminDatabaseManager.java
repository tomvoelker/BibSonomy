package org.bibsonomy.database.managers;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.InetAddressStatus;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.AdminParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.User;

/**
 * Provides functionalities which are typically only available to admins. This
 * might include flagging a user as spammer, setting the status of an
 * InetAddress (IP) and other things.
 * 
 * @author Robert Jäschke
 * @author Stefan Stützer
 * @author Beate Krause
 * @version $Id: AdminDatabaseManager.java,v 1.14 2008-11-04 17:25:11 beatekr
 *          Exp $
 */
public class AdminDatabaseManager extends AbstractDatabaseManager {

	private final static AdminDatabaseManager singleton = new AdminDatabaseManager();
	protected static final Log log = LogFactory.getLog(AdminDatabaseManager.class);

	/**
	 * Holds the names of the tables where group ids must be updated, when a
	 * user is flagged as spammer or deleted.
	 * 
	 * TODO: Make database names constants.
	 */
	private final List<String> tableNames = Arrays.asList(new String[] { "tas", "grouptas", "bibtex", "bookmark", "search_bibtex", "search_bookmark" });

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
				user.setPrediction(SpamStatus.SPAMMER.getId());
			} else {
				user.setPrediction(SpamStatus.NO_SPAMMER.getId());
			}
		}

		param.setPrediction(user.getPrediction());
		param.setConfidence(user.getConfidence());
		param.setMode(user.getMode());
		param.setAlgorithm(user.getAlgorithm());
		param.setUpdatedBy(updatedBy);
		param.setUpdatedAt(new Date());
		param.setGroupRange(Integer.MIN_VALUE);

		// update user and group table so that user is flagged as spammer
		// and all groups are updated appropriately
		// one session to prevent case of only updating spammer without updating the groups
		session.beginTransaction();
		/*
		 * What's the outcome when this method is called by deleteUser()?
		 * When method deleteUser() calls flag spammer, the deleted user is treated as a spammer. 
		 * Consequence: It is flagged as a spammer in the user table, and the groups are set to spammer groups.
		 */
		boolean predictionChange = checkPredictionChange(param, session);

		try {

			/*
			 * on_delete (calling method deleteUser()) 
			 * admins (from BibSonomy Admin Interface) 
			 * consequence: users are flagged as spammers, groups updated
			 */
			if (!"classifier".equals(updatedBy)) {
				// flag spammer
				this.update("flagSpammer", param, session);
				// update the groups
				this.updateGroupIds(param, session);
			/*
			 * spam framework (classifier) flags user as spammer
			 * an update only takes place when the user has not been updated before 
			 * by the classifier with the same prediction and confidence
			 */
			} else if ("off".equals(testMode)) {
				// gets user data to check if to_classify is still set to 1
				List<User> userData = this.queryForList("getClassifierUserBeforeUpdate", param, User.class, session);
				// only update if to_classify is set to 1, else admin has
				// already classified the specific user
				if (userData.get(0).getToClassify() == 1) {
					// only change user settings when prediction changes
					if (predictionChange) {
						this.update("flagSpammer", param, session);
						this.updateGroupIds(param, session);
					}
				}
			}

			// update log tables
			if (predictionChange) {

				// logs all predictions ever made
				this.insert("logPrediction", param, session);

				// logs the current prediction
				this.insert("logCurrentPrediction", param, session);

			}
			
			// set session counter to 0, so that transaction will be commited in 
			// session wrapper
			session.commitTransaction();
		}

		catch (final Exception ex) {
			log.error(ex.getMessage(), ex);

		} finally {
			// in case of failure, session should be locked in DbSessionImpl
			session.endTransaction();
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

			final List<User> history = getClassifierHistory(param.getUserName(), session);

			for (final User user : history) {

				if (user.getConfidence() != null && user.getPrediction() != null) {

					if (user.getAlgorithm().equals(param.getAlgorithm())) {

						// FIXME: collect constants in appropriate class
						if (Math.abs(param.getConfidence() - user.getConfidence()) < 0.0001) {

							if (param.getPrediction().compareTo(user.getPrediction()) == 0) {
								return false;
							}
							// first entry is not the same
							return true;
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Updates group ids in different tables
	 * 
	 * @param param
	 *            the admin's parameters
	 * @param session
	 *            the current db session
	 */
	private void updateGroupIds(final AdminParam param, final DBSession session) {
		for (final String table : tableNames) {
			param.setGroupIdTable(table);
			this.update("updateGroupIds", param, session);
		}
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
	public List<User> getClassifiedUsers(final Classifier classifier, final SpamStatus status, final int limit, DBSession session) {
		final AdminParam param = new AdminParam();
		param.setInterval(1000);
		param.setLimit(limit);

		if (classifier.equals(Classifier.ADMIN) && (status.equals(SpamStatus.SPAMMER) || status.equals(SpamStatus.NO_SPAMMER) || status.equals(SpamStatus.UNKNOWN))) {
			param.setPrediction(status.getId());
			return this.queryForList("getAdminClassifiedUsers", param, User.class, session);
		} else if (classifier.equals(Classifier.BOTH)) {
			return this.queryForList("getAllUsersWithSpam", param, User.class, session);
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
	 * Updates a setting value
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
		
		// set values for settings update
		param.setKey(key.toString());
		param.setValue(value);
		
		// if classifier update is concerned with a whitelist update, 
		// handle separately
		if (ClassifierSettings.WHITELIST_EXP.equals(key)){
			this.insert("insertClassifierWhitelist", param, session);
		}else{
			// rest is in classifier settings table
			this.update("updateClassifierSettings", param, session);
		}
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
	 * Stores classifier's meta information in given user object
	 * 
	 * @param user
	 *            the user object
	 */
	public User getClassifierUserDetails(final User user, final DBSession session) {
		return this.queryForObject("getClassifierUserDetails", user.getName(), user, session);
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
		log.debug("Get BibTex for users: " + param.getInterval() + " " + param.getLimit());
		return this.queryForList("getBibtexUsers", param, User.class, session);
	}

}