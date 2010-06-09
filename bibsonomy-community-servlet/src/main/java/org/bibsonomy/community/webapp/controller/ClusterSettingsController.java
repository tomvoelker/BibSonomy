package org.bibsonomy.community.webapp.controller;

import static org.bibsonomy.community.webapp.enums.ClusterSettingActions.*;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.database.CommunityManager;
import org.bibsonomy.community.database.TagManager;
import org.bibsonomy.community.database.UserSettingsManager;
import org.bibsonomy.community.enums.Ordering;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.community.util.Pair;
import org.bibsonomy.community.webapp.command.ResourceClusterViewCommand;
import org.springframework.web.servlet.ModelAndView;


public class ClusterSettingsController extends AbstractBaseController<ResourceClusterViewCommand> {
	private static final Log log = LogFactory.getLog(ClusterSettingsController.class);

	private UserSettingsManager userSettingsManager;
	private CommunityManager communityManager;
	private TagManager tagManager;
	
	public ClusterSettingsController() {
		setCommandClass(ResourceClusterViewCommand.class);
	}
	
	//------------------------------------------------------------------------
	// controller interface
	//------------------------------------------------------------------------

	@Override
	protected ResourceClusterViewCommand instantiateCommand() {
		return new ResourceClusterViewCommand();
	}

	@Override
	public ModelAndView workOn(ResourceClusterViewCommand command) {
		if( command.getContext().isUserLoggedIn() ) {
			User user = new User(command.getContext().getLoginUser());
			try {
				if( ADDCLUSTERS.toString().equals(command.getAction()) && present(command.getClusters())) {
					// add given clusters to user settings
					userSettingsManager.addUserAffiliation(user, command.getClusters());
				} else if( REMOVECLUSTERS.toString().equals(command.getAction()) && present(command.getClusters())) {
					// remove given clusters from user settings
					userSettingsManager.removeUserAffiliation(user, command.getClusters());
				} else {
					// just display current settings
				}
				userSettingsManager.fillUserAffiliation(user);
				populateClusterSettings(command, user, 25);
			} catch (Exception e) {
				log.error("Error getting cluster affiliations for user "+user.getName(),e);
			}
		}
        return new ModelAndView("export/"+getOutputFormat(command)+"/settings", "command", command);
	}
	
	//------------------------------------------------------------------------
	// helper functions
	//------------------------------------------------------------------------
	private void populateClusterSettings(final ResourceClusterViewCommand command, final User user, final int nTags ) {
		String[] topics = {"politics", "computer", "medcine"};
		List<Tag> annotations = new LinkedList<Tag>();
		for( String topic : topics ) {
			annotations.add(new Tag(topic));
		}
		
		Collection<ResourceCluster> clusters = new ArrayList<ResourceCluster>(topics.length);
		for( final Map.Entry<Pair<Integer,Integer>, Double> entry : user.getCommunityAffiliation().entrySet() ) {
			final Integer runId        = entry.getKey().getFirst();
			final Integer communityId = entry.getKey().getSecond();
			final Double  weight       = entry.getValue(); 
			
			ResourceCluster cluster = new ResourceCluster();
			cluster.setRunID(runId);
			cluster.setClusterID(communityId);
			Collection<Tag> tags = tagManager.getTagCloudForCommunity(runId, communityId, Ordering.POPULAR, nTags, 0); 
			cluster.setTags(tags);
			//cluster.setAnnotation(annotations.subList(i, i+1));
			clusters.add(cluster);
		}
		
		command.setClusters(clusters);
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setCommunityManager(CommunityManager communityManager) {
		this.communityManager = communityManager;
	}

	public CommunityManager getCommunityManager() {
		return communityManager;
	}

	public void setUserSettingsManager(UserSettingsManager userSettingsManager) {
		this.userSettingsManager = userSettingsManager;
	}

	public UserSettingsManager getUserSettingsManager() {
		return userSettingsManager;
	}

	public void setTagManager(TagManager tagManager) {
		this.tagManager = tagManager;
	}

	public TagManager getTagManager() {
		return tagManager;
	}


	
}