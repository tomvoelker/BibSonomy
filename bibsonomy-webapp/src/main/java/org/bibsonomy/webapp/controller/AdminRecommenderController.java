package org.bibsonomy.webapp.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.recommender.tags.TagRecommenderConnector;
import org.bibsonomy.recommender.tags.database.DBAccess;
import org.bibsonomy.recommender.tags.database.params.RecAdminOverview;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.services.recommender.TagRecommender;
import org.bibsonomy.webapp.command.AdminRecommenderViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;




/**
 * @author bsc
 * @version $Id$
 */


public class AdminRecommenderController implements MinimalisticController<AdminRecommenderViewCommand>{
	private static final Log log = LogFactory.getLog(AdminRecommenderController.class);
	
	private LogicInterface logic;
	private UserSettings userSettings;
	private MultiplexingTagRecommender mp;
	private final Long QUERIES_PER_LATENCY = (long)1000;

	
	
	public View workOn(AdminRecommenderViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();
		final DBAccess db = (DBAccess)DBAccess.getInstance(); 
		
		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new ValidationException("error.method_not_allowed");
		}
		
		command.setPageTitle("admin recommender");
		command.setmultiplexingTagRecommender(mp);
		
		
		List<TagRecommenderConnector> distRecommenders = mp.getDistRecommenders();
		List<TagRecommender> localRecommenders = mp.getLocalRecommenders();
		List<RecAdminOverview> recommenderList = new ArrayList<RecAdminOverview>();
		
		/* Get info for active local recommenders */
		for(TagRecommender p: localRecommenders){
			try{
				RecAdminOverview current = db.getRecommenderAdminOverview(p.getClass().getCanonicalName());
				current.setLatency(db.getAverageLatencyForRecommender(current.getSettingID(), QUERIES_PER_LATENCY));
		        recommenderList.add(current);
			}
			catch(SQLException e){
				log.debug(e.toString());
			}
		}
		
		/* Get info for active distant recommenders */
		for(TagRecommenderConnector p: distRecommenders){
			try{
				RecAdminOverview current = db.getRecommenderAdminOverview(p.getId());
				current.setLatency(db.getAverageLatencyForRecommender(current.getSettingID(), QUERIES_PER_LATENCY));
		        recommenderList.add(current);
			}
			catch(SQLException e){
				log.debug(e.toString());
			}
		}
		
		/* Store info */
		command.setRecOverview(recommenderList);
		
		
		return Views.ADMIN_RECOMMENDER;
	}
	
	public AdminRecommenderViewCommand instantiateCommand() {
		return new AdminRecommenderViewCommand();
	}
	
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
	public LogicInterface getLogic() {
		return this.logic;
	}

	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}
	public UserSettings getUserSettings() {
		return this.userSettings;
	}
	
	public void setmultiplexingTagRecommender(MultiplexingTagRecommender mp){
		this.mp = mp;
	}
	


}