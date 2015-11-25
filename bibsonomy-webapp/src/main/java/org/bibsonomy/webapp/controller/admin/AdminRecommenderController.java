/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.admin;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.admin.AdminRecommenderViewCommand;
import org.bibsonomy.webapp.command.admin.AdminRecommenderViewCommand.Tab;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

import recommender.core.Recommender;
import recommender.core.database.DBLogic;
import recommender.core.database.params.RecAdminOverview;
import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.util.RecommenderUtil;
import recommender.impl.model.RecommendedItem;
import recommender.impl.model.RecommendedTag;
import recommender.impl.multiplexer.MultiplexingRecommender;

/**
 * TODO: more generic controller by using a map RecommendationResult.class -> MRecommender
 * 
 * @author bsc
 */
public class AdminRecommenderController implements MinimalisticController<AdminRecommenderViewCommand> {
	private static final Log log = LogFactory.getLog(AdminRecommenderController.class);
	
	private static final String CMD_EDITTAGRECOMMENDER = "editTagRecommender";
	private static final String CMD_EDITITEMRECOMMENDER = "editItemRecommender";
	private static final String CMD_UPDATE_RECOMMENDERSTATUS = "updateRecommenderstatus";
	private static final String CMD_REMOVETAGRECOMMENDER = "removetagrecommender";
	private static final String CMD_REMOVEITEMRECOMMENDER = "removeitemrecommender";
	private static final String CMD_ADDTAGRECOMMENDER = "addtagrecommender";
	private static final String CMD_ADDITEMRECOMMENDER = "additemrecommender";
	
	private DBLogic<TagRecommendationEntity, RecommendedTag> dbTagLogic;
	private DBLogic<ItemRecommendationEntity, RecommendedItem> dbItemLogic;
	
	private MultiplexingRecommender<TagRecommendationEntity, RecommendedTag> tagRecommender;
	private MultiplexingRecommender<ItemRecommendationEntity, RecommendedItem> itemRecommender;

	@Override
	public View workOn(final AdminRecommenderViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();

		final Tab tab = Tab.values()[command.getTab()];

		log.info("ACTIVE TAB: " + tab + " -> " + command.getTabDescription());

		/*
		 * Check user role If user is not logged in or not an admin: show error
		 * message
		 */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("please log in as admin");
		}

		/* ---------------------- Actions ---------------------------- */

		if (CMD_ADDTAGRECOMMENDER.equals(command.getAction())) {
			handleAddRecommender(command, this.tagRecommender);
		} else if (CMD_ADDITEMRECOMMENDER.equals(command.getAction())) {
			handleAddRecommender(command, this.itemRecommender);
		} else if (CMD_REMOVETAGRECOMMENDER.equals(command.getAction())) {
			handleRemoveRecommender(command, this.tagRecommender);
		} else if (CMD_REMOVEITEMRECOMMENDER.equals(command.getAction())) {
			handleRemoveRecommender(command, this.itemRecommender);
		} else if (CMD_UPDATE_RECOMMENDERSTATUS.equals(command.getAction())) {
			this.handleUpdateRecommenderStatus(command);
		} else if (CMD_EDITTAGRECOMMENDER.equals(command.getAction())) {
			handleEditRecommender(this.tagRecommender, this.dbTagLogic, command);
		} else if (CMD_EDITITEMRECOMMENDER.equals(command.getAction())) {
			handleEditRecommender(this.itemRecommender, this.dbItemLogic, command);
		}
		command.setAction(null);

		/* ---------------------- Tabs ---------------------------- */

		if (command.getTab() == Tab.STATUS.ordinal()) {
			this.showStatusTab(command);
		} else if (command.getTab() == Tab.ACTIVATE.ordinal()) {
			this.showActivationTab(command);
		} else if (command.getTab() == Tab.ADD.ordinal()) {
			this.showAddTab(command);
		}

		return Views.ADMIN_RECOMMENDER;
	}

	/**
	 * Remove/add recommender page. 
	 */
	private void showAddTab(final AdminRecommenderViewCommand command) {
		final List<Long> tagRecs = this.dbTagLogic.getDistantRecommenderSettingIds();
		final Map<Long, String> tagRecMap = this.dbTagLogic.getRecommenderIdsForSettingIds(tagRecs);
		command.setActiveTagRecommenders(tagRecMap);
		final List<Long> itemRecs = this.dbItemLogic.getDistantRecommenderSettingIds();
		final Map<Long, String> itemRecMap = this.dbItemLogic.getRecommenderIdsForSettingIds(itemRecs);
		command.setActiveItemRecommenders(itemRecMap);
	}

	/**
	 * Recommender activation/deactivation page; get Settingids of
	 * active/disabled recommenders from database; store in
	 * command.activeRecs/command.disabledRecs
	 */
	private void showActivationTab(final AdminRecommenderViewCommand command) {
		final Map<Long, String> activeTagRecs = this.dbTagLogic.getRecommenderIdsForSettingIds(this.dbTagLogic.getActiveRecommenderSettingIds());
		final Map<Long, String> activeItemRecs = this.dbItemLogic.getRecommenderIdsForSettingIds(this.dbItemLogic.getActiveRecommenderSettingIds());
		final Map<Long, String> disabledTagRecs = this.dbTagLogic.getRecommenderIdsForSettingIds(this.dbTagLogic.getDisabledRecommenderSettingIds());
		final Map<Long, String> disabledItemRecs = this.dbItemLogic.getRecommenderIdsForSettingIds(this.dbItemLogic.getDisabledRecommenderSettingIds());

		
		command.setActiveItemRecommenders(activeItemRecs);
		command.setDisabledItemRecommenders(disabledItemRecs);
		command.setActiveTagRecommenders(activeTagRecs);
		command.setDisabledTagRecommenders(disabledTagRecs);
	}

	/**
	 * Recommender Statuspage; get active recommenders from multiplexer and
	 * fetch setting_id, rec_id, average latency from database; store in
	 * command.recOverview
	 */
	private void showStatusTab(final AdminRecommenderViewCommand command) {
		final List<Recommender<TagRecommendationEntity, RecommendedTag>> tagRecommenderList = new ArrayList<Recommender<TagRecommendationEntity, RecommendedTag>>();
		final List<Recommender<ItemRecommendationEntity, RecommendedItem>> itemRecommenderList = new ArrayList<Recommender<ItemRecommendationEntity, RecommendedItem>>();
		final List<RecAdminOverview> itemRecommenderInfoList = new ArrayList<RecAdminOverview>();
		final List<RecAdminOverview> tagRecommenderInfoList = new ArrayList<RecAdminOverview>();
		tagRecommenderList.addAll(this.tagRecommender.getLocalRecommenders());
		tagRecommenderList.addAll(this.tagRecommender.getDistRecommenders());
		itemRecommenderList.addAll(this.itemRecommender.getLocalRecommenders());
		itemRecommenderList.addAll(this.itemRecommender.getDistRecommenders());

		for (final Recommender<TagRecommendationEntity, RecommendedTag> p : tagRecommenderList) {
			final RecAdminOverview current = this.dbTagLogic.getRecommenderAdminOverview(RecommenderUtil.getRecommenderId(p));
			current.setLatency(this.dbTagLogic.getAverageLatencyForRecommender(current.getSettingID(), command.getQueriesPerLatency()));
			tagRecommenderInfoList.add(current);
		}
		
		for (final Recommender<ItemRecommendationEntity, RecommendedItem> p : itemRecommenderList) {
			final RecAdminOverview current = this.dbItemLogic.getRecommenderAdminOverview(RecommenderUtil.getRecommenderId(p));
			current.setLatency(this.dbItemLogic.getAverageLatencyForRecommender(current.getSettingID(), command.getQueriesPerLatency()));
			itemRecommenderInfoList.add(current);
		}
		
		/* Store info */
		command.setRecOverviewItem(itemRecommenderInfoList);
		command.setRecOverviewTag(tagRecommenderInfoList);
	}

	private static void handleEditRecommender(final MultiplexingRecommender<?, ?> recommender, final DBLogic<?, ?> logic, final AdminRecommenderViewCommand command) {
		try {
			// TODO: add a validator?
			if (!UrlUtils.isValid(command.getNewrecurl())) {
				throw new MalformedURLException();
			}
			final URL newRecurl = new URL(command.getNewrecurl());

			final Long sid = Long.valueOf(command.getEditSid());
			final boolean recommenderEnabled = recommender.disableRecommender(sid);
			logic.updateRecommenderUrl(command.getEditSid(), newRecurl);
			if (recommenderEnabled) {
				recommender.enableRecommender(sid);
			}

			command.setAdminResponse("Changed url of item recommender #" + command.getEditSid() + " to " + command.getNewrecurl() + ".");
		} catch (final MalformedURLException ex) {
			command.setAdminResponse("Could not edit item recommender. Please check if '" + command.getNewrecurl() + "' is a valid url.");
		}
		command.setNewrecurl(null);
		command.setTab(Tab.ADD);
	}

	private void handleUpdateRecommenderStatus(final AdminRecommenderViewCommand command) {
		if (command.getActiveItemRecs() != null) {
			for (final Long sid : command.getActiveItemRecs()) {
				this.itemRecommender.enableRecommender(sid);
			}
		}
		if (command.getDisabledItemRecs() != null) {
			for (final Long sid : command.getDisabledItemRecs()) {
				this.itemRecommender.disableRecommender(sid);
			}
		}
		if (command.getActiveTagRecs() != null) {
			for (final Long sid : command.getActiveTagRecs()) {
				this.tagRecommender.enableRecommender(sid);
			}
		}
		if (command.getDisabledTagRecs() != null) {
			for (final Long sid : command.getDisabledTagRecs()) {
				this.tagRecommender.disableRecommender(sid);
			}
		}
		command.setTab(Tab.ACTIVATE);
		command.setAdminResponse("Successfully Updated Recommenderstatus!");
	}
	
	private static void handleRemoveRecommender(final AdminRecommenderViewCommand command, final MultiplexingRecommender<?, ?> recommender) {
		try {
			int failures = 0;
			
			if (!present(command.getDeleteRecIds())) {
				command.setAdminResponse("Please select a recommender first!");
			} else {
				for(final String urlString : command.getDeleteRecIds()) {
					final URL url = new URL(urlString);
					final boolean success = recommender.removeRecommender(url);
					if (!success) {
						failures++;
					}
				}
				if (failures == 0) {
					command.setAdminResponse("Successfully removed all selected item recommenders.");
				} else {
					command.setAdminResponse(failures + " recommender(s) could not be removed.");
				}
			}
		} catch (final MalformedURLException ex) {
			log.warn("Invalid url in removeRecommender ", ex);
		}

		command.setTab(Tab.ADD);
	}
	
	private static void handleAddRecommender(final AdminRecommenderViewCommand command, final MultiplexingRecommender<?, ?> recommender) {
		try {
			if (!UrlUtils.isValid(command.getNewrecurl())) {
				throw new MalformedURLException();
			}
			recommender.addRecommender(new URL(command.getNewrecurl()));
			command.setAdminResponse("Successfully added and activated new item recommender!");
		} catch (final MalformedURLException e) {
			command.setAdminResponse("Could not add new item recommender. Please check if '" + command.getNewrecurl() + "' is a valid url.");
		} catch (final Exception e) {
			log.error("Error testing 'set recommender'", e);
			command.setAdminResponse("Failed to add new item recommender");
		}

		command.setTab(Tab.ADD);
	}
	
	@Override
	public AdminRecommenderViewCommand instantiateCommand() {
		return new AdminRecommenderViewCommand();
	}

	/** @param mp */
	public void setTagRecommender(final MultiplexingRecommender<TagRecommendationEntity, RecommendedTag> mp) {
		this.tagRecommender = mp;
	}
	
	/**
	 * @param itemRecommender
	 */
	public void setItemRecommender(MultiplexingRecommender<ItemRecommendationEntity, RecommendedItem> itemRecommender) {
		this.itemRecommender = itemRecommender;
	}

	/**
	 * @param dbTagLogic 
	 */
	public void setDbTagLogic(final DBLogic<TagRecommendationEntity, RecommendedTag> dbTagLogic) {
		this.dbTagLogic = dbTagLogic;
	}
	
	/**
	 * @param dbItemLogic 
	 */
	public void setDbItemLogic(DBLogic<ItemRecommendationEntity, RecommendedItem> dbItemLogic) {
		this.dbItemLogic = dbItemLogic;
	}
}