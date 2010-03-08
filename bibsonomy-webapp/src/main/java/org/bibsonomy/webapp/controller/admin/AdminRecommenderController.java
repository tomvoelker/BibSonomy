package org.bibsonomy.webapp.controller.admin;

import java.net.URI;
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
import org.bibsonomy.recommender.tags.WebserviceTagRecommender;
import org.bibsonomy.recommender.tags.database.DBAccess;
import org.bibsonomy.recommender.tags.database.params.RecAdminOverview;
import org.bibsonomy.recommender.tags.database.params.RecSettingParam;
import org.bibsonomy.recommender.tags.multiplexer.MultiplexingTagRecommender;
import org.bibsonomy.services.recommender.TagRecommender;
import org.bibsonomy.webapp.command.admin.AdminRecommenderViewCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
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
    
	
	public void init(){
	}
	
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
		//command.setmultiplexingTagRecommender(mp);
		
		/*
		 * Recommender activation/deactivation page;
		 * get Settingids of active/disabled recommenders from database;
		 * store in command.activeRecs/command.disabledRecs
		 */
		if(command.getAction().equals("activate")){
			if(command.getActiveRecs() == null && command.getDisabledRecs() == null){
			    try{
			      command.setActiveRecs(db.getActiveRecommenderSettingIds());
			      command.setDisabledRecs(db.getDisabledRecommenderSettingIds());
			    }
			    catch(SQLException e){
				    log.debug(e.toString());
			    }
			}
		}
		
		/*
		 * Store activation/deactivation-settings in the database;
		 * remove from/add to multiplexer 
		*/
		else if(command.getAction().equals("updateRecommenderstatus")){
			
			//---------------------------------------------------------
			// Update database 
			
			try{
			    db.updateRecommenderstatus(command.getActiveRecs(), command.getDisabledRecs());
			} catch(SQLException e){
				log.debug(e.toString());
				command.setAdminResponse("Could not store data!");
			}
			

			//---------------------------------------------------------
			// Update multiplexer
			
			List<TagRecommender> localRecommenders = mp.getLocalRecommenders();
			List<TagRecommenderConnector> distantRecommenders = mp.getDistRecommenders();
			
			//Add
			if(command.getActiveRecs() != null){
			  for(Integer sid: command.getActiveRecs()){
				if(sid == 2 || sid==3) continue; // TODO: Also check local recommenders!!!
				
				RecSettingParam currentSetting = instantiateSettingParamForRecId(sid, db);
				
				/* Recommender does not exist yet? -> Add to multiplexer. */
				if(findSettingIndex(distantRecommenders, currentSetting) == -1){
					URI newRecURI = null;
					try{ newRecURI = new URI(currentSetting.getRecId()); }
					catch(Exception e){ log.debug(e.toString()); }
					
					WebserviceTagRecommender newRec = new WebserviceTagRecommender(newRecURI);
					distantRecommenders.add(newRec);
				}
			  }
			}
			
			//Remove
			if(command.getDisabledRecs() != null){
			  for(Integer sid: command.getDisabledRecs()){
				if(sid == 2 || sid==3) continue; // TODO: Also check local recommenders!!!
				
				RecSettingParam currentSetting = instantiateSettingParamForRecId(sid, db);
				boolean currentRecIsConnector = currentSetting.getRecId().startsWith("http");

				//If recommender was found at a certain position in the list, remove it
				int currentSettingIndex = findSettingIndex(distantRecommenders, currentSetting);
				if(currentSettingIndex != -1)
					distantRecommenders.remove(currentSettingIndex);
			  }
			}
			
			// Store new multiplexer-settings
			mp.setDistRecommenders(distantRecommenders);
			
			
			command.setAction("activate");
			command.setAdminResponse("Successfully Updated Recommenderstatus!");
			return new ExtendedRedirectView("/admin/recommender?action=activate");
		}

		
		
	   //---------------------------------------------------------
	   /* Recommender Statuspage;
	    * get active recommenders from multiplexer and fetch
	    * setting_id, rec_id, average latency from database;
	    * store in command.recOverview
	    */
		else if(command.getAction().equals("status")){
			List<TagRecommender> recommenderList = new ArrayList<TagRecommender>();
			List<RecAdminOverview> recommenderInfoList = new ArrayList<RecAdminOverview>();
			recommenderList.addAll(mp.getLocalRecommenders());
			recommenderList.addAll(mp.getDistRecommenders());
			
			for(TagRecommender p: recommenderList){
				String recId;
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

	
	/** Find the position of a setting in the multiplexer's recommenderlist. 
	 * @param list list of TagRecommenderConnectors
	 * @param setting RecSettingParam to search for
	 * @return index of the setting, -1 if it is not in the list  
	 */
	public static int findSettingIndex(List<TagRecommenderConnector> list, RecSettingParam setting){
		int i = -1;
		for(TagRecommenderConnector p: list){
		    i++;
			//compare recID
			if(p.getId().equals(setting.getRecId())){
				byte[] meta1 = setting.getRecMeta();
				byte[] meta2 = p.getMeta();
				
				//compare meta-data
				if(meta1.length != meta2.length) continue;
				for(int j=0; j<meta1.length; j++)
					if(meta1[j] != meta2[j]) continue;
				
				//recommender with same rec_id and meta already exists 
				return i;
			}
		}
		//recommender was not found
		return -1;
	}
	
	/** Get information for a setting-id from the database
	 *  @param id Setting_id of the recommender
	 *  @param db DBAccess to fetch information from 
	 *  @return new RecSettingParam-object for this setting_id
	 */
	public static RecSettingParam instantiateSettingParamForRecId(int id, DBAccess db){
		log.info("Bearbeite jetzt SettingID: " + id);
		RecSettingParam newSetting = null;
		try{
			newSetting = db.getRecommender(new Long(id));
			newSetting.setSetting_id(id);
		}
		catch(SQLException e){
			log.debug("Could not instantiate RecSettingParam for Setting_id " + id + ": " + e.toString());
		}
		return newSetting;
	}
	


}