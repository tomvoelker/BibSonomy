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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.bibsonomy.webapp.command.BaseCommand;

import recommender.core.database.params.RecAdminOverview;
import recommender.impl.multiplexer.MultiplexingRecommender;

/**
 * Command bean for admin page 
 * 
 * @author bsc
 */
public class AdminRecommenderViewCommand extends BaseCommand {
	private MultiplexingRecommender mp;
	private List<RecAdminOverview> recOverviewItem;
	private List<RecAdminOverview> recOverviewTag; 
	private String action;
	private String adminResponse;
	private Long queriesPerLatency;
	private List<Long> activeItemRecs;
	private List<Long> activeTagRecs;
	private List<Long> disabledItemRecs;
	private List<Long> disabledTagRecs;
	private final Map<Integer, String> tabdescriptor;
	/**
	 * @author bsc
	 *
	 */
	public enum Tab{ STATUS, ACTIVATE, ADD }
	private Tab tab;
	private Map<Long, String> activeItemRecommenders;
	private Map<Long, String> disabledItemRecommenders;
	private Map<Long, String> activeTagRecommenders;
	private Map<Long, String> disabledTagRecommenders;

	private long editSid;
	private List<String> deleteRecIds;
	
	// TODO: use URL instead of String as type
	private String newrecurl;
	
	
	/**
	 */
	public AdminRecommenderViewCommand(){
		this.queriesPerLatency = (long)1000;
		this.action = null;
		
		this.tabdescriptor = new TreeMap<Integer, String>();
		this.tabdescriptor.put(Tab.STATUS.ordinal(), "Active Recommenders");
		this.tabdescriptor.put(Tab.ACTIVATE.ordinal(), "Activate/deactivate");
		this.tabdescriptor.put(Tab.ADD.ordinal(), "Add/Remove");
		this.tab = Tab.STATUS;
	}
	
	/**
	 * @param activeItemRecommenders map {setting-id} -> {recommender-id}
	 */
	public void setActiveItemRecommenders(final Map<Long, String> activeItemRecommenders){
		this.activeItemRecommenders = activeItemRecommenders;
	}
	
	/**
	 * @param disabledItemRecommenders map {setting-id} -> {recommender-id}
	 */
	public void setDisabledItemRecommenders(final Map<Long, String> disabledItemRecommenders){
		this.disabledItemRecommenders = disabledItemRecommenders;
	}
	
	/**
	 * @return Entryset of currently activated item recommenders 
	 */
	public Set<Entry<Long, String>> getActiveItemRecommenders(){
		if (this.activeItemRecommenders == null) {
			return null;
		}
		return this.activeItemRecommenders.entrySet();
	}
	
	/**
	 * @return Entryset of currently deactivated item recommenders 
	 */
	public Set<Entry<Long, String>> getDisabledItemRecommenders(){
		if (this.disabledItemRecommenders == null) {
			return null;
		}
		return this.disabledItemRecommenders.entrySet();
	}
	
	/**
	 * @param activeTagRecommenders map {setting-id} -> {recommender-id}
	 */
	public void setActiveTagRecommenders(final Map<Long, String> activeTagRecommenders){
		this.activeTagRecommenders = activeTagRecommenders;
	}
	
	/**
	 * @param disabledTagRecommenders map {setting-id} -> {recommender-id}
	 */
	public void setDisabledTagRecommenders(final Map<Long, String> disabledTagRecommenders){
		this.disabledTagRecommenders = disabledTagRecommenders;
	}
	
	/**
	 * @return Entryset of currently activated item recommenders 
	 */
	public Set<Entry<Long, String>> getActiveTagRecommenders(){
		if (this.activeTagRecommenders == null) {
			return null;
		}
		return this.activeTagRecommenders.entrySet();
	}
	
	/**
	 * @return Entryset of currently deactivated item recommenders 
	 */
	public Set<Entry<Long, String>> getDisabledTagRecommenders(){
		if (this.disabledTagRecommenders == null) {
			return null;
		}
		return this.disabledTagRecommenders.entrySet();
	}
	
	/**
	 * @param t ordinal number of tab to be activated
	 */
	public void setTab(final Integer t){
		if ((t>=0) && (t<Tab.values().length)) {
		  this.tab = Tab.values()[t];
		}
	}
	/**
	 * @param t Tab to be activated
	 */
	public void setTab(final Tab t){
		this.tab = t;
	}
	/**
	 * @return ordinal number of active tab
	 */
	public Integer getTab(){
		return this.tab.ordinal();
	}
	/**
	 * @return name/description of currently activated tab
	 */
	public String getTabDescription(){
		return this.tabdescriptor.get(this.tab.ordinal());
	}
	/**
	 * @param t tab to get description for
	 * @return Description of this tab
	 */
	public String getTabDescription(final Tab t){
		return this.tabdescriptor.get(t.ordinal());
	}

	/**
	 * @return Entryset containing Tab-id and their descriptions
	 */
	public Set<Entry<Integer, String>> getTabs(){
		return this.tabdescriptor.entrySet();
	}
	
	/**
	 * @param recOverviewItem List of item recommmenders contained in item-multiplexer
	 */
	public void setRecOverviewItem(final List<RecAdminOverview> recOverviewItem){
		this.recOverviewItem = recOverviewItem;
	}
	/**
	 * @return List of item recommmenders contained in item-multiplexer
	 */
	public List<RecAdminOverview> getRecOverviewItem(){
		return this.recOverviewItem;
	}
	/**
	 * @param recOverviewTag list of tag recommenders contained in tag-multiplexer
	 */
	public void setRecOverviewTag(List<RecAdminOverview> recOverviewTag) {
		this.recOverviewTag = recOverviewTag;
	}
	/**
	 * @return list of tag recommenders contained in tag-multiplexer
	 */
	public List<RecAdminOverview> getRecOverviewTag() {
		return this.recOverviewTag;
	}
	/**
	 * @param mp multiplexer
	 */
	public void setMultiplexingTagRecommender(final MultiplexingRecommender mp){
		this.mp = mp;
	}
	/**
	 * @return multiplexer
	 */
	public MultiplexingRecommender getMultiplexingTagRecommender(){
		return this.mp;
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
		//only accept positive values
		if(queriesPerLatency > 0) {
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
	 * @param activeItemRecs updated list of active item recommender setting-ids.
	 * This property can be set in the view by administrators and will be managed and set back to null by the controller. 
	 */
	public void setActiveItemRecs(final List<Long> activeItemRecs){
		this.activeItemRecs = activeItemRecs;
	}
	/**
	 * @return updated active item recommenders
	 */
	public List<Long> getActiveItemRecs(){
		return this.activeItemRecs;
	}
	
	/**
	 * @param disabledItemRecs updated list of inactive item recommender setting-ids
	 */
	public void setDisabledItemRecs(final List<Long> disabledItemRecs){
		this.disabledItemRecs = disabledItemRecs;
	}
	/**
	 * @return updated list of inactive setting-ids
	 */
	public List<Long> getDisabledItemRecs(){
		return this.disabledItemRecs;
	}
	
	/**
	 * @param activeTagRecs updated list of active tag recommender setting-ids.
	 * This property can be set in the view by administrators and will be managed and set back to null by the controller. 
	 */
	public void setActiveTagRecs(final List<Long> activeTagRecs){
		this.activeTagRecs = activeTagRecs;
	}
	/**
	 * @return updated active item recommenders
	 */
	public List<Long> getActiveTagRecs(){
		return this.activeTagRecs;
	}
	
	/**
	 * @param disabledTagRecs updated list of inactive tag recommender setting-ids
	 */
	public void setDisabledTagRecs(final List<Long> disabledTagRecs){
		this.disabledTagRecs = disabledTagRecs;
	}
	/**
	 * @return updated list of inactive setting-ids
	 */
	public List<Long> getDisabledTagRecs(){
		return this.disabledTagRecs;
	}

	/**
	 * @param editSid setting-id of recommender to be edited
	 */
	public void setEditSid(final long editSid) {
		this.editSid = editSid;
	}
	/**
	 * @return setting-id of recommender to be edited
	 */
	public long getEditSid() {
		return this.editSid;
	}

	/**
	 * @return ids/urls of recommenders to be deleted
	 */
	public List<String> getDeleteRecIds() {
		return this.deleteRecIds;
	}

	/**
	 * @param deleteRecIds ids/urls of recommenders to be edited
	 */
	public void setDeleteRecIds(final List<String> deleteRecIds) {
		this.deleteRecIds = deleteRecIds;
	}
	/**
	 * @param recurl url of new recommender to be added
	 */
	public void setNewrecurl(final String recurl){
		this.newrecurl = recurl;
	}
	/**
	 * @return url of new recommender to be added
	 */
	public String getNewrecurl(){
		return this.newrecurl;
	}
	
	
}