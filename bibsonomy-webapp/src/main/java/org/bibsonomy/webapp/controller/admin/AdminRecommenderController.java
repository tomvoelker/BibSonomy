/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.model.User;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.admin.AdminRecommenderViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.security.access.AccessDeniedException;

import recommender.core.Recommender;
import recommender.core.database.DBLogic;
import recommender.core.database.params.RecAdminOverview;
import recommender.core.interfaces.model.RecommendationResult;
import recommender.core.util.RecommenderUtil;
import recommender.impl.multiplexer.MultiplexingRecommender;
import recommender.impl.webservice.WebserviceRecommender;

/**
 * TODO: more generic controller by using a map RecommendationResult.class -> MRecommender
 * 
 * @author bsc
 */
public class AdminRecommenderController implements MinimalisticController<AdminRecommenderViewCommand> {
	private static final Log log = LogFactory.getLog(AdminRecommenderController.class);
	
	private static final String CMD_ACTIVATE_RECOMMENDER = "activateRecommender";
	private static final String CMD_DEACTIVATE_RECOMMENDER = "deactivateRecommender";
	private static final String CMD_REMOVE_RECOMMENDER = "removeRecommender";
	private static final String CMD_ADD_RECOMMENDER = "addRecommender";
	
	private Map<Class<? extends RecommendationResult>, DBLogic<?, ?>> recommenderLogicMap;
	private Map<Class<? extends RecommendationResult>, MultiplexingRecommender<?, ?>> recommenderMap;
	
	@Override
	public View workOn(final AdminRecommenderViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();
		/*
		 * Check user role If user is not logged in or not an admin: show error
		 * message
		 */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("please log in as admin");
		}
		
		final Class<? extends RecommendationResult> recClass = command.getRecommendationResultClass();
		final MultiplexingRecommender<?, ?> recommender = this.recommenderMap.get(recClass);

		String action = command.getAction();
		if (CMD_ADD_RECOMMENDER.equals(action)) {
			handleAddRecommender(command, recommender);
		} else if (CMD_REMOVE_RECOMMENDER.equals(action)) {
			handleRemoveRecommender(command, recommender);
		} else if (CMD_ACTIVATE_RECOMMENDER.equals(action)) {
			handleActivateRecommender(command, recommender);
		} else if (CMD_DEACTIVATE_RECOMMENDER.equals(action)) {
			handleDeactivateRecommender(command, recommender);
		}
		
		command.setAction(null);
		this.getRecommenderInformationsForView(command);
		return Views.ADMIN_RECOMMENDER;
	}

	/**
	 * Recommender Statuspage; get active recommenders from multiplexer and
	 * fetch setting_id, rec_id, average latency from database; store in
	 * command.recOverview
	 */
	private void getRecommenderInformationsForView(final AdminRecommenderViewCommand command) {
		final Map<Class<? extends RecommendationResult>, List<RecAdminOverview>> overviewMap = new HashMap<Class<? extends RecommendationResult>, List<RecAdminOverview>>();
		for (final Entry<Class<? extends RecommendationResult>, MultiplexingRecommender<?, ?>> recommenderMapEntity : this.recommenderMap.entrySet()) {
			final Class<? extends RecommendationResult> recomClass = recommenderMapEntity.getKey();
			final MultiplexingRecommender<?, ?> multiplexer = recommenderMapEntity.getValue();
			final DBLogic<?, ?> logic = this.recommenderLogicMap.get(recomClass);
			
			final List<RecAdminOverview> recommenderInfoList = new ArrayList<RecAdminOverview>();
			for (final Recommender<?, ?> recommender : multiplexer.getAllRecommenders()) {
				final RecAdminOverview overview = logic.getRecommenderAdminOverview(RecommenderUtil.getRecommenderId(recommender));
				overview.setLatency(logic.getAverageLatencyForRecommender(overview.getSettingID(), command.getQueriesPerLatency()));
				recommenderInfoList.add(overview);
			}
			
			overviewMap.put(recomClass, recommenderInfoList);
		}
		command.setRecommenderOverviewMap(overviewMap);
	}
	
	private static void handleActivateRecommender(final AdminRecommenderViewCommand command, final MultiplexingRecommender<?, ?> recommender) {
		final Long recommenderId = command.getRecommenderId();
		recommender.enableRecommender(recommenderId);
		command.setAdminResponse("Activated recommender!");
	}
	
	private static void handleDeactivateRecommender(final AdminRecommenderViewCommand command, final MultiplexingRecommender<?, ?> recommender) {
		if (recommender.getAllActiveRecommenders().size() == 1) {
			command.setAdminResponse("Can't deactivate last active recommender");
			return;
		}
		final Long recommenderId = command.getRecommenderId();
		recommender.disableRecommender(recommenderId);
		command.setAdminResponse("Deactivated recommender!");
	}
	
	private static void handleRemoveRecommender(final AdminRecommenderViewCommand command, final MultiplexingRecommender<?, ?> recommender) {
		final Long selectedRecommender = command.getRecommenderId();
		if (!present(selectedRecommender)) {
			command.setAdminResponse("Please select a recommender first!");
		} else {
			if (recommender.removeRecommender(selectedRecommender)) {
				command.setAdminResponse("Successfully removed recommender.");
			} else {
				command.setAdminResponse("Recommender could not be removed.");
			}
		}
	}
	
	private static <E, R extends RecommendationResult> void handleAddRecommender(final AdminRecommenderViewCommand command, final MultiplexingRecommender<E, R> recommender) {
		final URL recommenderUrl = command.getNewrecurl();
		try {
			if (!UrlUtils.isValid(recommenderUrl.toString())) {
				throw new MalformedURLException();
			}
			
			final WebserviceRecommender<E, R> webserviceRecommender = new WebserviceRecommender<E, R>();
			webserviceRecommender.setAddress(recommenderUrl);
			webserviceRecommender.setTrusted(command.isTrusted());
			recommender.addRecommender(webserviceRecommender);
			command.setAdminResponse("Successfully added and activated new recommender!");
		} catch (final Exception e) {
			log.error("Error testing 'set recommender'", e);
			command.setAdminResponse("Failed to add new recommender");
		}
	}
	
	@Override
	public AdminRecommenderViewCommand instantiateCommand() {
		return new AdminRecommenderViewCommand();
	}

	/**
	 * @param recommenderLogicMap the recommenderLogicMap to set
	 */
	public void setRecommenderLogicMap(Map<Class<? extends RecommendationResult>, DBLogic<?, ?>> recommenderLogicMap) {
		this.recommenderLogicMap = recommenderLogicMap;
	}

	/**
	 * @param recommenderMap the recommenderMap to set
	 */
	public void setRecommenderMap(Map<Class<? extends RecommendationResult>, MultiplexingRecommender<?, ?>> recommenderMap) {
		this.recommenderMap = recommenderMap;
	}
}