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
import recommender.core.interfaces.model.RecommendedItem;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.core.model.RecommendedTag;
import recommender.impl.multiplexer.MultiplexingRecommender;
import recommender.impl.multiplexer.tags.util.RecommenderUtil;

/**
 * @author bsc
 * @version $Id$
 */
public class AdminRecommenderController implements MinimalisticController<AdminRecommenderViewCommand> {
	private static final Log log = LogFactory.getLog(AdminRecommenderController.class);
	
	private static final int RECOMMENDER_ITEM_TYPE_ID = 1;
	private static final int RECOMMENDER_TAG_TYPE_ID = 0;
	
	private static final String CMD_EDITRECOMMENDER = "editRecommender";
	private static final String CMD_UPDATE_RECOMMENDERSTATUS = "updateRecommenderstatus";
	private static final String CMD_REMOVERECOMMENDER = "removerecommender";
	private static final String CMD_ADDRECOMMENDER = "addrecommender";
	
	private DBLogic<TagRecommendationEntity, RecommendedTag> dbTagLogic;
	private DBLogic<ItemRecommendationEntity, RecommendedItem> dbItemLogic;
	
	private MultiplexingRecommender<TagRecommendationEntity, RecommendedTag> tagRecommender;
	private MultiplexingRecommender<ItemRecommendationEntity, RecommendedItem> itemRecommender;
	
	/**
	 * FIXME: why isn't this done by the {@link MultiplexingRecommender#init()} method?
	 * Initialize multiplexer
	 **/
	public void init() {
		/*
		 * does nothing at all
		 */
	}

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

		if (!present(command.getAction())) {
			// Do nothing
		} else if (CMD_ADDRECOMMENDER.equals(command.getAction())) {
			this.handleAddRecommender(command);
		} else if (CMD_REMOVERECOMMENDER.equals(command.getAction())) {
			this.handleRemoveRecommender(command);
		} else if (CMD_UPDATE_RECOMMENDERSTATUS.equals(command.getAction())) {
			this.handleUpdateRecommenderStatus(command);
		} else if (CMD_EDITRECOMMENDER.equals(command.getAction())) {
			this.handleEditRecommender(command);
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
		final List<Long> recs = this.dbTagLogic.getDistantRecommenderSettingIds();
		final Map<Long, String> recMap = this.dbTagLogic.getRecommenderIdsForSettingIds(recs);
		command.setActiveRecommenders(recMap);
	}

	/**
	 * Recommender activation/deactivation page; get Settingids of
	 * active/disabled recommenders from database; store in
	 * command.activeRecs/command.disabledRecs
	 */
	private void showActivationTab(final AdminRecommenderViewCommand command) {
		final Map<Long, String> activeRecs = this.dbTagLogic.getRecommenderIdsForSettingIds(this.dbTagLogic.getActiveRecommenderSettingIds());
		activeRecs.putAll(this.dbItemLogic.getRecommenderIdsForSettingIds(this.dbItemLogic.getActiveRecommenderSettingIds()));
		final Map<Long, String> disabledRecs = this.dbTagLogic.getRecommenderIdsForSettingIds(this.dbTagLogic.getDisabledRecommenderSettingIds());
		disabledRecs.putAll(this.dbItemLogic.getRecommenderIdsForSettingIds(this.dbItemLogic.getDisabledRecommenderSettingIds()));

		
		command.setActiveRecommenders(activeRecs);
		command.setDisabledRecommenders(disabledRecs);
	}

	/**
	 * Recommender Statuspage; get active recommenders from multiplexer and
	 * fetch setting_id, rec_id, average latency from database; store in
	 * command.recOverview
	 */
	private void showStatusTab(final AdminRecommenderViewCommand command) {
		final List<Recommender<TagRecommendationEntity, RecommendedTag>> tagRecommenderList = new ArrayList<Recommender<TagRecommendationEntity, RecommendedTag>>();
		final List<Recommender<ItemRecommendationEntity, RecommendedItem>> itemRecommenderList = new ArrayList<Recommender<ItemRecommendationEntity, RecommendedItem>>();
		final List<RecAdminOverview> recommenderInfoList = new ArrayList<RecAdminOverview>();
		tagRecommenderList.addAll(this.tagRecommender.getLocalRecommenders());
		tagRecommenderList.addAll(this.tagRecommender.getDistRecommenders());
		itemRecommenderList.addAll(this.itemRecommender.getLocalRecommenders());
		itemRecommenderList.addAll(this.itemRecommender.getDistRecommenders());

		for (final Recommender<TagRecommendationEntity, RecommendedTag> p : tagRecommenderList) {
			final RecAdminOverview current = this.dbTagLogic.getRecommenderAdminOverview(RecommenderUtil.getRecommenderId(p));
			current.setLatency(this.dbTagLogic.getAverageLatencyForRecommender(current.getSettingID(), command.getQueriesPerLatency()));
			recommenderInfoList.add(current);
		}
		
		for (final Recommender<ItemRecommendationEntity, RecommendedItem> p : itemRecommenderList) {
			final RecAdminOverview current = this.dbItemLogic.getRecommenderAdminOverview(RecommenderUtil.getRecommenderId(p));
			current.setLatency(this.dbItemLogic.getAverageLatencyForRecommender(current.getSettingID(), command.getQueriesPerLatency()));
			recommenderInfoList.add(current);
		}
		
		this.generateNamesForRecWorkingTypes(recommenderInfoList);
		
		/* Store info */
		command.setRecOverview(recommenderInfoList);
	}

	private void handleEditRecommender(final AdminRecommenderViewCommand command) {
		try {
			// TODO: add a validator?
			if (!UrlUtils.isValid(command.getNewrecurl())) {
				throw new MalformedURLException();
			}
			final URL newRecurl = new URL(command.getNewrecurl());

			final long sid = command.getEditSid();
			final boolean recommenderEnabled = this.tagRecommender.disableRecommender(sid);
			this.dbTagLogic.updateRecommenderUrl(command.getEditSid(), newRecurl);
			if (recommenderEnabled) {
				this.tagRecommender.enableRecommender(sid);
			}

			command.setAdminResponse("Changed url of recommender #" + command.getEditSid() + " to " + command.getNewrecurl() + ".");
		} catch (final MalformedURLException ex) {
			command.setAdminResponse("Could not edit recommender. Please check if '" + command.getNewrecurl() + "' is a valid url.");
		}
		command.setNewrecurl(null);
		command.setTab(Tab.ADD);
	}

	private void handleUpdateRecommenderStatus(final AdminRecommenderViewCommand command) {
//		if (command.getActiveRecs() != null) {
//			for (final Long sid : command.getActiveRecs()) {
//				if (this.dbTagLogic.checkRecommenderInstanceType(sid) == RECOMMENDER_TAG_TYPE_ID) {
//					this.tagRecommender.enableRecommender(sid);
//				} else {
//					this.itemRecommender.enableRecommender(sid);
//				}
//			}
//		}
//		if (command.getDisabledRecs() != null) {
//			for (final Long sid : command.getDisabledRecs()) {
//				if (this.dbTagLogic.checkRecommenderInstanceType(sid) == RECOMMENDER_TAG_TYPE_ID) {
//					this.tagRecommender.disableRecommender(sid);
//				} else {
//					this.itemRecommender.disableRecommender(sid);
//				}
//			}
//		}
		command.setTab(Tab.ACTIVATE);
		command.setAdminResponse("Successfully Updated Recommenderstatus!");
	}

	private void handleRemoveRecommender(final AdminRecommenderViewCommand command) {
		try {
			int failures = 0;
			
			if((command.getDeleteRecIds() == null) || command.getDeleteRecIds().isEmpty()) {
				command.setAdminResponse("Please select a recommender first!");
			} else {
				for(final String urlString : command.getDeleteRecIds()) {
					final URL url = new URL(urlString);
					final boolean success = this.tagRecommender.removeRecommender(url);
					if(!success) {
						failures++;
					}
				}
				if (failures == 0) {
					command.setAdminResponse("Successfully removed all selected recommenders.");
				} else {
					command.setAdminResponse(failures + " recommender(s) could not be removed.");
				}
			}
		} catch (final MalformedURLException ex) {
			log.warn("Invalid url in removeRecommender ", ex);
		}

		command.setTab(Tab.ADD);
	}

	private void handleAddRecommender(final AdminRecommenderViewCommand command) {
		try {
			if (!UrlUtils.isValid(command.getNewrecurl())) {
				throw new MalformedURLException();
			}

			this.tagRecommender.addRecommender(new URL(command.getNewrecurl()));
			command.setAdminResponse("Successfully added and activated new recommender!");

		} catch (final MalformedURLException e) {
			command.setAdminResponse("Could not add new recommender. Please check if '" + command.getNewrecurl() + "' is a valid url.");
		} catch (final Exception e) {
			log.error("Error testing 'set recommender'", e);
			command.setAdminResponse("Failed to add new recommender");
		}

		command.setTab(Tab.ADD);
	}

	private void generateNamesForRecWorkingTypes(final List<RecAdminOverview> recos) {
//		for(RecAdminOverview overview : recos) {
//			if(overview.getRecWorkingType() == RECOMMENDER_TAG_TYPE_ID) {
//				overview.setWorkingTypeString("tagrecommender");
//			} else {
//				overview.setWorkingTypeString("itemrecommender");
//			}
//		}
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