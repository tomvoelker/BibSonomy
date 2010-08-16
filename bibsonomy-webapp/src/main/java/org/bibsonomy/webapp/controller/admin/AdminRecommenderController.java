package org.bibsonomy.webapp.controller.admin;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.recommender.tags.database.DBAccess;
import org.bibsonomy.recommender.tags.database.DBLogic;
import org.bibsonomy.recommender.tags.database.params.RecAdminOverview;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.recommender.tags.multiplexer.util.RecommenderUtil;
import org.bibsonomy.services.recommender.TagRecommender;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.admin.AdminRecommenderViewCommand;
import org.bibsonomy.webapp.command.admin.AdminRecommenderViewCommand.Tab;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * @author bsc
 * @version $Id$
 */
public class AdminRecommenderController implements MinimalisticController<AdminRecommenderViewCommand> {
	private static final Log log = LogFactory.getLog(AdminRecommenderController.class);
	
	private static final String CMD_EDITRECOMMENDER = "editRecommender";
	private static final String CMD_UPDATE_RECOMMENDERSTATUS = "updateRecommenderstatus";
	private static final String CMD_REMOVERECOMMENDER = "removerecommender";
	private static final String CMD_ADDRECOMMENDER = "addrecommender";
	
	private static final DBLogic db = DBAccess.getInstance();
	
	private LogicInterface logic;
	private UserSettings userSettings;
	private MultiplexingTagRecommender mp;
	

	@Override
	public View workOn(AdminRecommenderViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		Tab tab = Tab.values()[command.getTab()];

		log.info("ACTIVE TAB: " + tab + " -> " + command.getTabDescription());

		/*
		 * Check user role If user is not logged in or not an admin: show error
		 * message
		 */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("error.method_not_allowed");
		}

		command.setPageTitle("admin recommender");

		/* ---------------------- Actions ---------------------------- */

		if (command.getAction() == null) {
			// Do nothing
		} else if (command.getAction().equals(CMD_ADDRECOMMENDER)) {
			handleAddRecommender(command);
		} else if (command.getAction().equals(CMD_REMOVERECOMMENDER)) {
			handleRemoveRecommender(command);
		} else if (command.getAction().equals(CMD_UPDATE_RECOMMENDERSTATUS)) {
			handleUpdateRecommenderStatus(command);
		} else if (command.getAction().equals(CMD_EDITRECOMMENDER)) {
			handleEditRecommender(command);
		}

		command.setAction(null);

		/* ---------------------- Tabs ---------------------------- */

		if (command.getTab() == Tab.STATUS.ordinal()) {
			showStatusTab(command);
		} else if (command.getTab() == Tab.ACTIVATE.ordinal()) {
			showActivationTab(command);
		} else if (command.getTab() == Tab.ADD.ordinal()) {
			showAddTab(command);
		}

		return Views.ADMIN_RECOMMENDER;
	}

	/**
	 * Remove/add recommender page. 
	 */
	private void showAddTab(AdminRecommenderViewCommand command) {
		try {
			List<Long> recs = db.getDistantRecommenderSettingIds();
			Map<Long, String> recMap = db.getRecommenderIdsForSettingIds(recs);

			command.setActiveRecommenders(recMap);
		} catch (SQLException e) {
			log.debug(e);
		}
	}

	/**
	 * Recommender activation/deactivation page; get Settingids of
	 * active/disabled recommenders from database; store in
	 * command.activeRecs/command.disabledRecs
	 */
	private void showActivationTab(AdminRecommenderViewCommand command) {
		try {
			Map<Long, String> activeRecs = db.getRecommenderIdsForSettingIds(db.getActiveRecommenderSettingIds());
			Map<Long, String> disabledRecs = db.getRecommenderIdsForSettingIds(db.getDisabledRecommenderSettingIds());

			command.setActiveRecommenders(activeRecs);
			command.setDisabledRecommenders(disabledRecs);

		} catch (SQLException e) {
			log.debug(e);
		}
	}

	/**
	 * Recommender Statuspage; get active recommenders from multiplexer and
	 * fetch setting_id, rec_id, average latency from database; store in
	 * command.recOverview
	 */
	private void showStatusTab(AdminRecommenderViewCommand command) {
		List<TagRecommender> recommenderList = new ArrayList<TagRecommender>();
		List<RecAdminOverview> recommenderInfoList = new ArrayList<RecAdminOverview>();
		recommenderList.addAll(mp.getLocalRecommenders());
		recommenderList.addAll(mp.getDistRecommenders());

		for (TagRecommender p : recommenderList) {
			try {
				RecAdminOverview current = db.getRecommenderAdminOverview(RecommenderUtil.getRecommenderId(p));
				current.setLatency(db.getAverageLatencyForRecommender(current.getSettingID(), command.getQueriesPerLatency()));
				recommenderInfoList.add(current);
			} catch (SQLException e) {
				log.debug(e.toString());
			}
		}
		/* Store info */
		command.setRecOverview(recommenderInfoList);
	}

	private void handleEditRecommender(AdminRecommenderViewCommand command) {
		try {
			if (!UrlUtils.isValid(command.getNewrecurl())) throw new MalformedURLException();
			URL newRecurl = new URL(command.getNewrecurl());

			long sid = command.getEditSid();
			boolean recommenderEnabled = mp.disableRecommender(sid);
			db.updateRecommenderUrl(command.getEditSid(), newRecurl);
			if (recommenderEnabled) mp.enableRecommender(sid);

			command.setAdminResponse("Changed url of recommender #" + command.getEditSid() + " to " + command.getNewrecurl() + ".");
		} catch (MalformedURLException ex) {
			command.setAdminResponse("Could not edit recommender. Please check if '" + command.getNewrecurl() + "' is a valid url.");
		} catch (SQLException e) {
			log.warn("SQLException while editing recommender", e);
		}
		command.setNewrecurl(null);
		command.setTab(Tab.ADD);
	}

	private void handleUpdateRecommenderStatus(AdminRecommenderViewCommand command) {
		if (command.getActiveRecs() != null) {
			for (Long sid : command.getActiveRecs()) {
				mp.enableRecommender(sid);
			}
		}
		if (command.getDisabledRecs() != null) {
			for (Long sid : command.getDisabledRecs()) {
				mp.disableRecommender(sid);
			}
		}
		command.setTab(Tab.ACTIVATE);
		command.setAdminResponse("Successfully Updated Recommenderstatus!");
	}

	private void handleRemoveRecommender(AdminRecommenderViewCommand command) {
		try {
			int failures = 0;
			
			if(command.getDeleteRecIds() == null || command.getDeleteRecIds().isEmpty()) {
				command.setAdminResponse("Please select a recommender first!");
			} else {
				for(String urlString : command.getDeleteRecIds()) {
					URL url = new URL(urlString);
					boolean success = mp.removeRecommender(url);
					if(!success) failures++;
				}
				if (failures == 0) {
					command.setAdminResponse("Successfully removed all selected recommenders.");
				} else {
					command.setAdminResponse(failures + " recommender(s) could not be removed.");
				}
			}
		} catch (MalformedURLException ex) {
			log.warn("Invalid url in removeRecommender ", ex);
		}

		command.setTab(Tab.ADD);
	}

	private void handleAddRecommender(AdminRecommenderViewCommand command) {
		try {
			if (!UrlUtils.isValid(command.getNewrecurl())) {
				throw new MalformedURLException();
			}

			mp.addRecommender(new URL(command.getNewrecurl()));
			command.setAdminResponse("Successfully added and activated new recommender!");

		} catch (MalformedURLException e) {
			command.setAdminResponse("Could not add new recommender. Please check if '" + command.getNewrecurl() + "' is a valid url.");
		} catch (Exception e) {
			log.error("Error testing 'set recommender'", e);
			command.setAdminResponse("Failed to add new recommender");
		}

		command.setTab(Tab.ADD);
	}

	@Override
	public AdminRecommenderViewCommand instantiateCommand() {
		return new AdminRecommenderViewCommand();
	}

	/** @param logic */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}

	/** @return logic */
	public LogicInterface getLogic() {
		return this.logic;
	}

	/** @param userSettings */
	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}

	/** @return usersettings */
	public UserSettings getUserSettings() {
		return this.userSettings;
	}

	/** @param mp */
	public void setMultiplexingTagRecommender(MultiplexingTagRecommender mp) {
		this.mp = mp;
	}

}