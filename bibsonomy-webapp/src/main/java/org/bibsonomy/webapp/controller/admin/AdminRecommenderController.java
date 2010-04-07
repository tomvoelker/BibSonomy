package org.bibsonomy.webapp.controller.admin;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
			addRecommendersToMultiplexer(recs);
		}
		catch(SQLException e){
		    log.debug("Couldn't initialize multiplexer! ", e);
		}
	}
	
	
	public View workOn(AdminRecommenderViewCommand command) {
		final RequestWrapperContext context = command.getContext();
		final User loginUser = context.getLoginUser();
		
		Tab tab = Tab.values()[command.getTab()];
		
		log.debug("ACTIVE TAB: " + tab + " -> " + command.getTabDescription());
		
		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(loginUser.getRole())) {
			throw new ValidationException("error.method_not_allowed");
		}
		
		command.setPageTitle("admin recommender");
		

		
		/* ---------------------- Actions ---------------------------- */
		
		if(command.getAction() == null){
			// Do nothing
	    }   // Add
		    // TODO: set recommender to be inactive when added?
		else if(command.getAction().equals("addrecommender")){
			try{
			    Long sid = db.insertRecommenderSetting(command.getNewrecurl(), "Webservice", command.getNewrecurl().getBytes());
				addRecommenderToMultiplexer(sid);
				command.setAdminResponse("Successfully added and activated new recommender with setting_id "+sid+"!");
			}
			catch(Exception e){
				log.error("Error testing 'set recommender'", e);
				command.setAdminResponse("Failed to add new recommender");
			}
			
			command.setTab(Tab.ADD);
			
		} // Remove
		else if(command.getAction().equals("removerecommender")){
			removeRecommenderFromMultiplexer(command.getDeletesid());
			
			//update database
			try{
			    db.removeRecommender(command.getDeletesid());
				command.setAdminResponse("Successfully removed recommender with settingId " + command.getDeletesid() +".");
			} catch(Exception e){
				command.setAdminResponse("Failed to remove recommender with settingId " + command.getDeletesid());
				log.error("Error updating database",e);
			}

			command.setTab(Tab.ADD);
		}
		
		/*
		 * Store activation/deactivation-settings in the database;
		 * remove from/add to multiplexer 
		*/
		else if(command.getAction().equals("updateRecommenderstatus")){

			// Update database 
			try{
			    db.updateRecommenderstatus(command.getActiveRecs(), command.getDisabledRecs());
			} catch(SQLException e){
				log.debug(e);
				command.setAdminResponse("Could not store data!");
			}

			
			// Update multiplexer
			if(command.getActiveRecs() != null)
				addRecommendersToMultiplexer(command.getActiveRecs());
			if(command.getDisabledRecs() != null)
				removeRecommendersFromMultiplexer(command.getDisabledRecs());
			
			command.setTab(Tab.ACTIVATE);
			command.setAdminResponse("Successfully Updated Recommenderstatus!");
			//return new ExtendedRedirectView("/admin/recommender?action=activate");
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
			if(command.getActiveRecommenders() == null && command.getDisabledRecommenders() == null){
			    try{
			      Map<Long, String> activeRecs   = db.getRecommenderIdsForSettingIds(db.getActiveRecommenderSettingIds());
			      Map<Long, String> disabledRecs = db.getRecommenderIdsForSettingIds(db.getDisabledRecommenderSettingIds());
			      
			      /*
			      command.setActiveRecs(db.getActiveRecommenderSettingIds());
			      command.setDisabledRecs(db.getDisabledRecommenderSettingIds());
			      */
			      command.setActiveRecommenders(activeRecs);
			      command.setDisabledRecommenders(disabledRecs);
			      
			    }
			    catch(SQLException e){
				    log.debug(e);
			    }
			}
		}
		/*
		 * Remove or add distant recommenders.
		 * Deleted recommender will get a seperate status-value in the database
		 */
		// TODO: add recommenderid to settingid
		else if((int)command.getTab() == Tab.ADD.ordinal()){
			try{
		      
			  List<Long> recs = db.getActiveRecommenderSettingIds();
			  recs.addAll(db.getDisabledRecommenderSettingIds());
			  Map<Long, String> recMap = db.getRecommenderIdsForSettingIds(recs);
			  
			  command.setActiveRecommenders(recMap);
			} catch(SQLException e){
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

	
	/** Find the position of a setting in the multiplexer's recommenderlist. 
	 * @param list list of TagRecommenderConnectors
	 * @param setting RecSettingParam to search for
	 * @return index of the setting, -1 if it is not in the list  
	 */
	private int findSettingIndex(List<TagRecommenderConnector> list, RecSettingParam setting){
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
	 *  @return new RecSettingParam-object for this setting_id
	 */
	private RecSettingParam instantiateSettingParamForRecId(Long id){
		log.info("Bearbeite jetzt SettingID: " + id);
		RecSettingParam newSetting = null;
		try{
			newSetting = db.getRecommender(new Long(id));
			newSetting.setSetting_id(id);
		}
		catch(SQLException e){
			log.debug("Could not instantiate RecSettingParam for Setting_id " + id + ": ", e);
		}
		return newSetting;
	}

	/** Add a recommender identified by its settingId to the multiplexer.
	 *  @param sid Setting_id of the recommender
	 */
	private void addRecommenderToMultiplexer(Long sid){
		List<Long> sids = new ArrayList<Long>();
		sids.add(sid);
		addRecommendersToMultiplexer(sids);
	}
	
	/** Add a list of recommenders identified by their settingId to the multiplexer.
	 *  @param sids Setting_ids of the recommenders
	 */
	private void addRecommendersToMultiplexer(List<Long> sids){
		List<TagRecommenderConnector> distantRecommenders = mp.getDistRecommenders();
		
		for(Long sid: sids){
			//TODO: Also check local recommenders
			if(sid == 2 || sid==3) continue;
			
		    RecSettingParam newSetting = instantiateSettingParamForRecId(sid);
		    
			/* Recommender does not exist yet? -> Add to list of distant recommenders. */
			if(findSettingIndex(distantRecommenders, newSetting) == -1){
				URI newRecURI = null;
				try{
					newRecURI = new URI(newSetting.getRecId());
					WebserviceTagRecommender newRec = new WebserviceTagRecommender(newRecURI);
					distantRecommenders.add(newRec);
				}
				catch(Exception e){
					log.debug(e);
				}
		    }
		}
		mp.setDistRecommenders(distantRecommenders);
	}

	/** Remove a recommender identified by its settingId from the multiplexer.
	 *  @param sid Setting_id of the recommender
	 */
	private void removeRecommenderFromMultiplexer(Long sid){
		List<Long> sids = new ArrayList<Long>();
		sids.add(sid);
		removeRecommendersFromMultiplexer(sids);
	}
	

	/** Remove a list of recommenders identified by their settingId from the multiplexer.
	 *  @param sids Setting_ids of the recommenders
	 */
	private void removeRecommendersFromMultiplexer(List<Long> sids){
		List<TagRecommenderConnector> distantRecommenders = mp.getDistRecommenders();

		for(Long sid: sids){
			//TODO: Also check local recommenders
			if(sid == 2 || sid==3) continue;
			RecSettingParam currentSetting = instantiateSettingParamForRecId(sid);
			
			//If recommender was found at a certain position in the list, remove it
			int currentSettingIndex = findSettingIndex(distantRecommenders, currentSetting);
			if(currentSettingIndex != -1){
				distantRecommenders.remove(currentSettingIndex);
			}
		}
	    mp.setDistRecommenders(distantRecommenders);
	}
	


}