package org.bibsonomy.webapp.command;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.bibsonomy.recommender.tags.database.params.RecAdminOverview;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;

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
	private final HashMap<String,String> actions;
	private Long queriesPerLatency;
	private List<Integer> activeRecs;
	private List<Integer> disabledRecs;
	
	
	public AdminRecommenderViewCommand(){
		this.queriesPerLatency = (long)1000;
		this.actions = new HashMap<String, String>();
		this.actions.put("status","Active Recommenders");
		this.actions.put("activate","Activate/deactivate");
		this.actions.put("add", "Add/Remove");
		this.setAction("status");
	}
	
	public void setRecOverview(List<RecAdminOverview> recOverview){
		this.recOverview = recOverview;
	}
	public List<RecAdminOverview> getRecOverview(){
		return this.recOverview;
	}
	
	public Set<Entry<String, String>> getActions(){
		return actions.entrySet();
	}
	
	
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
	
	public void setActiveRecs(List<Integer> activeRecs){
		this.activeRecs = activeRecs;
	}
	public List<Integer> getActiveRecs(){
		return this.activeRecs;
	}
	
	public void setDisabledRecs(List<Integer> disabledRecs){
		this.disabledRecs = disabledRecs;
	}
	public List<Integer> getDisabledRecs(){
		return this.disabledRecs;
	}
	
	public String getActionDescription(){
		return this.actions.get(this.action);
	}
	
}