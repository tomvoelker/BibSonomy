package org.bibsonomy.webapp.command;

import java.util.List;

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
	
	
	public void setRecOverview(List<RecAdminOverview> recOverview){
		this.recOverview = recOverview;
	}
	public List<RecAdminOverview> getRecOverview(){
		return this.recOverview;
	}
	
	
	public void setmultiplexingTagRecommender(MultiplexingTagRecommender mp){
		this.mp = mp;
	}
	public MultiplexingTagRecommender getmultiplexingTagRecommender(){
		return this.mp;
	}
}