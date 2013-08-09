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
 * @version $Id$
 */
public class AdminRecommenderViewCommand extends BaseCommand {
	private MultiplexingRecommender mp;
	private List<RecAdminOverview> recOverview; 
	private String action;
	private String adminResponse;
	private Long queriesPerLatency;
	private List<Long> activeRecs;
	private List<Long> disabledRecs;
	private final Map<Integer, String> tabdescriptor;
	/**
	 * @author bsc
	 *
	 */
	public enum Tab{ STATUS, ACTIVATE, ADD }
	private Tab tab;
	private Map<Long, String> activeRecommenders;
	private Map<Long, String> disabledRecommenders;

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
	 * @param activeRecommenders map {setting-id} -> {recommender-id}
	 */
	public void setActiveRecommenders(final Map<Long, String> activeRecommenders){
		this.activeRecommenders = activeRecommenders;
	}
	
	/**
	 * @param disabledRecommenders map {setting-id} -> {recommender-id}
	 */
	public void setDisabledRecommenders(final Map<Long, String> disabledRecommenders){
		this.disabledRecommenders = disabledRecommenders;
	}
	
	/**
	 * @return Entryset of currently activated recommenders 
	 */
	public Set<Entry<Long, String>> getActiveRecommenders(){
		if (this.activeRecommenders == null) {
			return null;
		}
		return this.activeRecommenders.entrySet();
	}
	
	/**
	 * @return Entryset of currently deactivated recommenders 
	 */
	public Set<Entry<Long, String>> getDisabledRecommenders(){
		if (this.disabledRecommenders == null) {
			return null;
		}
		return this.disabledRecommenders.entrySet();
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
	 * @param recOverview List of recommmenders contained in multiplexer
	 */
	public void setRecOverview(final List<RecAdminOverview> recOverview){
		this.recOverview = recOverview;
	}
	/**
	 * @return List of recommmenders contained in multiplexer
	 */
	public List<RecAdminOverview> getRecOverview(){
		return this.recOverview;
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
	 * @param activeRecs updated list of active setting-ids.
	 * This property can be set in the view by administrators and will be managed and set back to null by the controller. 
	 */
	public void setActiveRecs(final List<Long> activeRecs){
		this.activeRecs = activeRecs;
	}
	/**
	 * @return updated active recommenders
	 */
	public List<Long> getActiveRecs(){
		return this.activeRecs;
	}
	
	/**
	 * @param disabledRecs updated list of inactive setting-ids
	 */
	public void setDisabledRecs(final List<Long> disabledRecs){
		this.disabledRecs = disabledRecs;
	}
	/**
	 * @return updated list of inactive setting-ids
	 */
	public List<Long> getDisabledRecs(){
		return this.disabledRecs;
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