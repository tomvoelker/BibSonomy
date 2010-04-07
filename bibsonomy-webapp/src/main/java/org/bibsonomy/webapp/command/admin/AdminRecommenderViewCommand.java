package org.bibsonomy.webapp.command.admin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bibsonomy.recommender.tags.database.params.RecAdminOverview;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.webapp.command.BaseCommand;

/**
 * Command bean for admin page 
 * 
 * @author bsc
 * @version $Id$
 */
public class AdminRecommenderViewCommand extends BaseCommand {
	private MultiplexingTagRecommender mp;
	private List<RecAdminOverview> recOverview; 
	private String action;
	private String adminResponse;
	//private final Map<String,String> actions;
	private Long queriesPerLatency;
	private List<Long> activeRecs;
	private List<Long> disabledRecs;
	private final Map<Integer, String> tabdescriptor;
	public enum Tab{ STATUS, ACTIVATE, ADD };
	private Tab tab;
	private Map<Long, String> activeRecommenders;
	private Map<Long, String> disabledRecommenders;
	
	private long deletesid;
	private String newrecurl;
	
	private Long currentid=2l;
	
	
	public AdminRecommenderViewCommand(){
		this.queriesPerLatency = (long)1000;
		this.action = null;
		
		/*
		this.actions = new TreeMap<String, String>();
		this.actions.put("status","Active Recommenders");
		this.actions.put("activate","Activate/deactivate");
		this.actions.put("add", "Add/Remove");
		this.setAction("status");
		*/
		this.tabdescriptor = new TreeMap<Integer, String>();
		tabdescriptor.put(Tab.STATUS.ordinal(), "Active Recommenders");
		tabdescriptor.put(Tab.ACTIVATE.ordinal(), "Activate/deactivate");
		tabdescriptor.put(Tab.ADD.ordinal(), "Add/Remove");
		this.tab = Tab.STATUS;
	}
	
	public void setActiveRecommenders(Map<Long, String> activeRecommenders){
		this.activeRecommenders = activeRecommenders;
	}
	public void setDisabledRecommenders(Map<Long, String> disabledRecommenders){
		this.disabledRecommenders = disabledRecommenders;
	}
	public Set<Entry<Long, String>> getActiveRecommenders(){
		if (activeRecommenders == null) return null;
		else return activeRecommenders.entrySet();
	}
	public Set<Entry<Long, String>> getDisabledRecommenders(){
		if (disabledRecommenders == null) return null;
		return disabledRecommenders.entrySet();
	}

	
	public void setTab(Integer t){
		if(t>=0 && t<Tab.values().length)
		  this.tab = Tab.values()[t];
	}
	public void setTab(Tab t){
		this.tab = t;
	}
	public Integer getTab(){
		return this.tab.ordinal();
	}
	public String getTabDescription(){
		return tabdescriptor.get(this.tab.ordinal());
	}
	public String getTabDescription(Tab t){
		return tabdescriptor.get(t.ordinal());
	}
	public Set<Entry<Integer, String>> getTabs(){
		return tabdescriptor.entrySet();
	}
	
	public void setRecOverview(List<RecAdminOverview> recOverview){
		this.recOverview = recOverview;
	}
	public List<RecAdminOverview> getRecOverview(){
		return this.recOverview;
	}
	
	/*
	public Set<Entry<String, String>> getActions(){
		return actions.entrySet();
	}
	*/
	
	public void setMultiplexingTagRecommender(MultiplexingTagRecommender mp){
		this.mp = mp;
	}
	public MultiplexingTagRecommender getMultiplexingTagRecommender(){
		return this.mp;
	}
	
	public void setAction(String action){
		this.action = action;
	}
	public String getAction(){
		return this.action;
	}
	
	public void setQueriesPerLatency(Long queriesPerLatency){
		//only accept positive values
		if(queriesPerLatency > 0)
		   this.queriesPerLatency = queriesPerLatency;
	}
	public Long getQueriesPerLatency(){
		return this.queriesPerLatency;
	}
	
	public void setAdminResponse(String adminResponse){
		this.adminResponse = adminResponse;
	}
	public String getAdminResponse(){
		return this.adminResponse;
	}
	
	public void setActiveRecs(List<Long> activeRecs){
		this.activeRecs = activeRecs;
	}
	public List<Long> getActiveRecs(){
		return this.activeRecs;
	}
	
	public void setDisabledRecs(List<Long> disabledRecs){
		this.disabledRecs = disabledRecs;
	}
	public List<Long> getDisabledRecs(){
		return this.disabledRecs;
	}
	
	public void setCurrentId(Long id){
		this.currentid = id;
	}
	public String getRecId(){
		return this.activeRecommenders.get(currentid);
	}
	
	/*
	public String getActionDescription(){
		return this.actions.get(this.action);
	}
	*/
	
	public void setDeletesid(long sid){
		this.deletesid = sid;
	}
	public long getDeletesid(){
		return this.deletesid;
	}
	public void setNewrecurl(String recurl){
		this.newrecurl = recurl;
	}
	public String getNewrecurl(){
		return this.newrecurl;
	}
	
	
}