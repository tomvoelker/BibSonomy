package org.bibsonomy.community.webapp.controller;

import static org.bibsonomy.community.webapp.enums.ClusterSettingActions.ADDCLUSTERS;
import static org.bibsonomy.community.webapp.enums.ClusterSettingActions.ADDRECOMMENDEDCLUSTER;
import static org.bibsonomy.community.webapp.enums.ClusterSettingActions.REMOVECLUSTERS;
import static org.bibsonomy.community.webapp.enums.ClusterSettingActions.SAVECLUSTERSETTINGS;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.community.model.ResourceCluster;
import org.bibsonomy.community.model.Tag;
import org.bibsonomy.community.model.User;
import org.bibsonomy.community.webapp.command.ResourceClusterViewCommand;
import org.bibsonomy.topics.database.TopicManagerLocal;
import org.bibsonomy.topics.search.TopicSearcher;
import org.springframework.web.servlet.ModelAndView;

/**
 * controller for saving and getting topic-based settings
 * 
 * @author andi
 *
 */
public class TopicSettingsController extends AbstractBaseController<ResourceClusterViewCommand> {
	private static final Log log = LogFactory.getLog(ClusterSettingsController.class);

	private static final int USERCLOUDLIMIT   = 25;
	private static final int TAGCLOUDLIMIT    = 25;
	
	private TopicManagerLocal topicManager;

	public TopicSettingsController() {
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
					int modelId = TopicSearcher.getInstance().getModelId();
					topicManager.addUserSettings(modelId, user, command.getClusters());
				} else if( ADDRECOMMENDEDCLUSTER.toString().equals(command.getAction()) ) {
					// add most appropriate cluster to given user's settings
					int modelId = TopicSearcher.getInstance().getModelId();
					topicManager.getRecommendedClusters(modelId, user);
				} else if( REMOVECLUSTERS.toString().equals(command.getAction()) && present(command.getClusters())) {
					// remove given clusters from user settings
					int modelId = TopicSearcher.getInstance().getModelId();
					topicManager.removeUserSettings(modelId, user, command.getClusters());
				} else if( SAVECLUSTERSETTINGS.toString().equals(command.getAction()) && present(command.getClusters())) {
					// save given cluster settings
					int modelId = TopicSearcher.getInstance().getModelId();
					topicManager.addUserSettings(modelId, user, command.getClusters());
				} else {
					// just display current settings
				}
				int modelId = TopicSearcher.getInstance().getModelId();
				topicManager.getUserSettings(user, modelId);
				populateClusterSettings(command, user, 25, 25);
			} catch (Exception e) {
				log.error("Error getting cluster affiliations for user "+user.getName(),e);
			}
		}
        return new ModelAndView("export/"+getOutputFormat(command)+"/resources", "command", command);
	}
	
	//------------------------------------------------------------------------
	// helper functions
	//------------------------------------------------------------------------
	private void populateClusterSettings(final ResourceClusterViewCommand command, final User user, final int nTags, final int nUsers ) {
		String[] topics = {"politics", "computer", "medcine"};
		List<Tag> annotations = new LinkedList<Tag>();
		for( String topic : topics ) {
			annotations.add(new Tag(topic));
		}
		
		Collection<ResourceCluster> clusters = new ArrayList<ResourceCluster>(topics.length);
		for( final Entry<Integer, Double> entry : user.getCommunityAffiliation().entrySet() ) {
			final Integer communityUId = entry.getKey();
			final Double  weight       = entry.getValue(); 
			
			ResourceCluster cluster = new ResourceCluster();
			cluster.setClusterID(communityUId);
			cluster.setWeight(weight);
			
			// TODO set modelId dynamically
			int modelId = TopicSearcher.getInstance().getModelId();
			Collection<Tag> tags = null;
			try {
				tags = topicManager.getTagCloud(modelId, communityUId, TAGCLOUDLIMIT);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			cluster.setTags(tags);
			Collection<User> members = null;
			try {
				members = topicManager.getUserCloud(modelId, communityUId, USERCLOUDLIMIT);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			cluster.setMembers(members);
			//cluster.setAnnotation(annotations.subList(i, i+1));
			clusters.add(cluster);
		}
		
		command.setClusters(clusters);
	}
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	
	public TopicManagerLocal getTopicManager() {
		return topicManager;
	}

	public void setTopicManager(TopicManagerLocal topicManager) {
		this.topicManager = topicManager;
	}
	
}