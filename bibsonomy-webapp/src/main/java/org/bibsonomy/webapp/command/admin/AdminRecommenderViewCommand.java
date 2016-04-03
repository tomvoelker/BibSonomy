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
package org.bibsonomy.webapp.command.admin;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.bibsonomy.webapp.command.BaseCommand;

import recommender.core.database.params.RecAdminOverview;
import recommender.core.interfaces.model.RecommendationResult;

/**
 * Command bean for admin page 
 * 
 * @author bsc
 */
public class AdminRecommenderViewCommand extends BaseCommand {
	private Map<Class<? extends RecommendationResult>, List<RecAdminOverview>> recommenderOverviewMap;
	private Long queriesPerLatency;
	
	private String action;
	private Class<? extends RecommendationResult> recommendationResultClass;
	private Long recommenderId;
	private URL newrecurl;
	private boolean trusted = false;
	
	private String adminResponse;
	
	/**
	 * default constructor
	 */
	public AdminRecommenderViewCommand(){
		this.queriesPerLatency = Long.valueOf(1000);
		this.action = null;
	}
	
	/**
	 * @param action the action which will be executed by the controller and set to null again
	 */
	public void setAction(final String action){
		this.action = action;
	}
	
	/**
	 * @return the action which will be executed by the controller and set to null again
	 */
	public String getAction(){
		return this.action;
	}
	
	/**
	 * @param queriesPerLatency number of values which will be fetched from the database to calculate average recommender-latencies
	 */
	public void setQueriesPerLatency(final Long queriesPerLatency){
		// only accept positive values
		if (present(queriesPerLatency) && queriesPerLatency.longValue() > 0) {
			this.queriesPerLatency = queriesPerLatency;
		}
	}
	/**
	 * @return number of values which will be fetched from the database to calculate average recommender-latencies
	 */
	public Long getQueriesPerLatency(){
		return this.queriesPerLatency;
	}
	
	/**
	 * @param adminResponse response-message to the last action executed (e.g. failure, success etc.) set by the controller
	 */
	public void setAdminResponse(final String adminResponse){
		this.adminResponse = adminResponse;
	}
	/**
	 * @return response-message to the last action executed (e.g. failure, success etc.) set by the controller
	 */
	public String getAdminResponse(){
		return this.adminResponse;
	}
	
	/**
	 * @param recurl url of new recommender to be added
	 */
	public void setNewrecurl(final URL recurl){
		this.newrecurl = recurl;
	}
	
	/**
	 * @return url of new recommender to be added
	 */
	public URL getNewrecurl(){
		return this.newrecurl;
	}
	
	/**
	 * @return the recommendationResultClass
	 */
	public Class<? extends RecommendationResult> getRecommendationResultClass() {
		return this.recommendationResultClass;
	}

	/**
	 * @param recommendationResultClass the recommendationResultClass to set
	 */
	public void setRecommendationResultClass(
			Class<? extends RecommendationResult> recommendationResultClass) {
		this.recommendationResultClass = recommendationResultClass;
	}

	/**
	 * @return the recommenderOverviewMap
	 */
	public Map<Class<? extends RecommendationResult>, List<RecAdminOverview>> getRecommenderOverviewMap() {
		return this.recommenderOverviewMap;
	}

	/**
	 * @param recommenderOverviewMap the recommenderOverviewMap to set
	 */
	public void setRecommenderOverviewMap(
			Map<Class<? extends RecommendationResult>, List<RecAdminOverview>> recommenderOverviewMap) {
		this.recommenderOverviewMap = recommenderOverviewMap;
	}

	/**
	 * @return the recommenderId
	 */
	public Long getRecommenderId() {
		return this.recommenderId;
	}

	/**
	 * @param recommenderId the recommenderId to set
	 */
	public void setRecommenderId(Long recommenderId) {
		this.recommenderId = recommenderId;
	}

	/**
	 * @return the trusted
	 */
	public boolean isTrusted() {
		return this.trusted;
	}

	/**
	 * @param trusted the trusted to set
	 */
	public void setTrusted(boolean trusted) {
		this.trusted = trusted;
	}
}