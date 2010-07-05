package org.bibsonomy.webapp.controller.admin;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.recommender.tags.TagRecommenderConnector;
import org.bibsonomy.recommender.tags.database.DBAccess;
import org.bibsonomy.recommender.tags.database.params.RecAdminOverview;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.services.recommender.TagRecommender;
import org.bibsonomy.util.UrlUtils;
import org.bibsonomy.webapp.command.admin.AdminRecommenderViewCommand;
import org.bibsonomy.webapp.command.admin.AdminRecommenderViewCommand.Tab;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;




/**
 * @author bsc
 * @version $Id$
 */


public class AdminRecommenderController implements MinimalisticController<AdminRecommenderViewCommand>{
	private static final String CMD_EDITRECOMMENDER 		 = "editRecommender";
	private static final String CMD_UPDATE_RECOMMENDERSTATUS = "updateRecommenderstatus";
	private static final String CMD_REMOVERECOMMENDER        = "removerecommender";
	private static final String CMD_ADDRECOMMENDER           = "addrecommender";
	private static final Log log = LogFactory.getLog(AdminRecommenderController.class);
	private static final DBAccess db = (DBAccess)DBAccess.getInstance(); 
	
	private LogicInterface logic;
	private UserSettings userSettings;
	private MultiplexingTagRecommender mp;
    
	
	/** 
	 * Initialize Controller and multiplexer
	 * */
	public void init(){
		List<Long> recs = null;
		try{
			recs = db.getActiveRecommenderSettingIds();
			for(Long sid: recs)
				mp.enableRecommender(sid);
		}
		catch(SQLException e){
		    log.debug("Couldn't initialize multiplexer! ", e);
		}
	}
	
	
	public View workOn(AdminRecommenderViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();
		
		Tab tab = Tab.values()[command.getTab()];
		
		log.info("ACTIVE TAB: " + tab + " -> " + command.getTabDescription());
		
		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new AccessDeniedException("error.method_not_allowed");
		}
		
		command.setPageTitle("admin recommender");
		

		
		/* ---------------------- Actions ---------------------------- */
		
		if(command.getAction() == null){
			// Do nothing
	    }   // Add
		else if(command.getAction().equals(CMD_ADDRECOMMENDER)){
			try{
				if(!UrlUtils.isValid(command.getNewrecurl())) throw new MalformedURLException();
			    mp.addRecommender(new URL(command.getNewrecurl()));
				command.setAdminResponse("Successfully added and activated new recommender!");
			}
			catch(MalformedURLException e){
				command.setAdminResponse("Could not add new recommender. Please check if '"+ command.getNewrecurl() +"' is a valid url.");
			}
			catch(Exception e){
				log.error("Error testing 'set recommender'", e);
				command.setAdminResponse("Failed to add new recommender");
			}
			
			command.setTab(Tab.ADD);
			
		} // Remove
		else if(command.getAction().equals(CMD_REMOVERECOMMENDER)){
			URL url = null;
			try {
				url = new URL(command.getDeleteRecId());
				boolean success = mp.removeRecommender(url);
				
				if(success){
					command.setAdminResponse("Successfully removed recommender '" + command.getDeleteRecId() +"'.");
				} else {
					command.setAdminResponse("Failed to remove recommender '" + command.getDeleteRecId() + "'");
				}
			} catch (MalformedURLException ex) {
				log.warn("Invalid url '" + command.getDeleteRecId() + "'" +
						 "in removeRecommender ", ex);
			}

			command.setTab(Tab.ADD);
		}
		
		/*
		 * Store activation/deactivation-settings in the database;
		 * remove from/add to multiplexer 
		*/
		else if(command.getAction().equals(CMD_UPDATE_RECOMMENDERSTATUS)){
			
			for(Long sid: command.getActiveRecs())
				mp.enableRecommender(sid);
			for(Long sid: command.getDisabledRecs())
				mp.disableRecommender(sid);
			command.setTab(Tab.ACTIVATE);
			command.setAdminResponse("Successfully Updated Recommenderstatus!");
		}

		/*
		 * Change the url of a recommender
		*/
		else if(command.getAction().equals(CMD_EDITRECOMMENDER)){
			try{
				if(!UrlUtils.isValid(command.getNewrecurl())) throw new MalformedURLException();
				URL newRecurl = new URL(command.getNewrecurl());

				long sid = command.getEditSid();
				boolean recommenderEnabled = mp.disableRecommender(sid);
				db.updateRecommenderUrl(command.getEditSid(), newRecurl);
				if(recommenderEnabled) mp.enableRecommender(sid);
				
				command.setAdminResponse("Changed url of recommender #"+command.getEditSid()+" to " + command.getNewrecurl()+".");
			}
			catch (MalformedURLException ex) {
				command.setAdminResponse("Could not edit recommender. Please check if '"+ command.getNewrecurl() +"' is a valid url.");
			}
			catch(SQLException e){
		        log.warn("SQLException while editing recommender",e);
			}
			command.setNewrecurl(null);
			command.setTab(Tab.ADD);
		}
		
		command.setAction(null);
		
		
		
		/* ---------------------- Tabs ---------------------------- */

		/* Recommender Statuspage;
		* get active recommenders from multiplexer and fetch
		* setting_id, rec_id, average latency from database;
		* store in command.recOverview
		*/
		if((int)command.getTab() == Tab.STATUS.ordinal()){
			List<TagRecommender> recommenderList = new ArrayList<TagRecommender>();
			List<RecAdminOverview> recommenderInfoList = new ArrayList<RecAdminOverview>();
			recommenderList.addAll(mp.getLocalRecommenders());
			recommenderList.addAll(mp.getDistRecommenders());
			
			for(TagRecommender p: recommenderList){
				String recId;
				// TODO: Container-class -> same getter for recId
				if(p instanceof TagRecommenderConnector)
				     recId = ((TagRecommenderConnector)p).getId();
				else recId = p.getClass().getCanonicalName();
				
				try{
					RecAdminOverview current = db.getRecommenderAdminOverview(recId);
				    current.setLatency(db.getAverageLatencyForRecommender(current.getSettingID(), command.getQueriesPerLatency()));
					recommenderInfoList.add(current);
				}
				catch(SQLException e){
					log.debug(e.toString());
				}
			}
			/* Store info */
			command.setRecOverview(recommenderInfoList);
		}
		
		/*
		 * Recommender activation/deactivation page;
		 * get Settingids of active/disabled recommenders from database;
		 * store in command.activeRecs/command.disabledRecs
		 */
		else if((int)command.getTab() == Tab.ACTIVATE.ordinal()){
			    try{
			      Map<Long, String> activeRecs   = db.getRecommenderIdsForSettingIds(db.getActiveRecommenderSettingIds());
			      Map<Long, String> disabledRecs = db.getRecommenderIdsForSettingIds(db.getDisabledRecommenderSettingIds());
			      
			      command.setActiveRecommenders(activeRecs);
			      command.setDisabledRecommenders(disabledRecs);
			      
			    }
			    catch(SQLException e){
				    log.debug(e);
			    }
		}
		/*
		 * Remove or add distant recommenders.
		 * Deleted recommender will get a seperate status-value in the database
		 */
		else if ((int)command.getTab() == Tab.ADD.ordinal()) {
			try {
				
			  List<Long> recs = db.getDistantRecommenderSettingIds();
			  Map<Long, String> recMap = db.getRecommenderIdsForSettingIds(recs);
			  
			  command.setActiveRecommenders(recMap);
			} catch (SQLException e) {
			  log.debug(e);
			}
		} 
		
		return Views.ADMIN_RECOMMENDER;
	}
	
	public AdminRecommenderViewCommand instantiateCommand() {
		return new AdminRecommenderViewCommand();
	}
	/** @param logic */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
	/** @return logic */
	public LogicInterface getLogic() {
		return this.logic;
	}
    /** @param userSettings */
	public void setUserSettings(UserSettings userSettings) {
		this.userSettings = userSettings;
	}
	/** @return usersettings */
	public UserSettings getUserSettings() {
		return this.userSettings;
	}
	/** @param mp */
	public void setMultiplexingTagRecommender(MultiplexingTagRecommender mp){
		this.mp = mp;
	}

}