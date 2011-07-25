package org.bibsonomy.webapp.controller.browsing;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.UserRelationSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.SphereResourceViewCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.TagCloudCommand;
import org.bibsonomy.webapp.controller.SingleResourceListControllerWithTags;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * Shows for every tagged relation the profile pictures of corresponding
 * users together with the most recent posts
 * 
 * @author fei
 * @version $Id$
 */
public class SphereListController extends SingleResourceListControllerWithTags implements MinimalisticController<SphereResourceViewCommand> {
	
	private static final TagCloudSort TAG_CLOUD_SORT = TagCloudSort.ALPHA;
	private static final int TAG_CLOUD_MINFREQ = 3;
	private static final int TAG_CLOUD_SIZE = 25;

	@Override
	public SphereResourceViewCommand instantiateCommand() {
		return new SphereResourceViewCommand();
	}

	@Override
	public View workOn(SphereResourceViewCommand command) {
		User loginUser = command.getContext().getLoginUser();
		
		List<User> relatedUsers = this.logic.getUserRelationship(loginUser.getName(), UserRelation.OF_FRIEND, null);
		
		// XXX: we collect all information by hand - this should be done already
		//      in an appropriate database query and result mapping
		Map<String, Set<User>> aspects = new HashMap<String, Set<User>>();
		
		// cycle through every related users and add to each aspect he/she 
		// belongs to (as given by the relation system tags)
		for (User relatedUser : relatedUsers) {
			for (Tag tag : relatedUser.getTags() ) {
				String relationName = null;
				if (SystemTagsUtil.isSystemTag(tag.getName(), UserRelationSystemTag.NAME)) {
					relationName = SystemTagsUtil.extractArgument(tag.getName());
				}
				if (present(relationName)) {
					if (!aspects.containsKey(relationName)) {
						aspects.put(relationName, new HashSet<User>());
					}
					// add user to the aspect given by the relation name
					Set<User> aspectUsers = aspects.get(relationName);
					aspectUsers.add(relatedUser);
				}
			}
		}
		command.setAspects(aspects);
		
		// XXX: we collect all information by hand - this should be done already
		//      in an appropriate database query and result mapping
		Map<String, ListCommand<Post<Bookmark>>> aspectsBMPosts = new HashMap<String, ListCommand<Post<Bookmark>>>();
		Map<String, ListCommand<Post<BibTex>>> aspectsPBPosts = new HashMap<String, ListCommand<Post<BibTex>>>();
		Map<String, TagCloudCommand> aspectsTagClouds = new HashMap<String, TagCloudCommand>();
		for (Entry<String,Set<User>> aspect : aspects.entrySet() ) {
			List<String> aspectTags = new ArrayList<String>();
			aspectTags.add(SystemTagsUtil.buildSystemTagString(UserRelationSystemTag.NAME, aspect.getKey()));
			List<Post<Bookmark>> bmPosts = logic.getPosts(Bookmark.class, GroupingEntity.FRIEND, loginUser.getName(), aspectTags, null, Order.ADDED, null, 0, 5, null);
			List<Post<BibTex>> pbPosts = logic.getPosts(BibTex.class, GroupingEntity.FRIEND, loginUser.getName(), aspectTags, null, Order.ADDED, null, 0, 5, null);
			
			ListCommand<Post<Bookmark>> bmListCommand = new ListCommand<Post<Bookmark>>(command);
			ListCommand<Post<BibTex>> pbListCommand = new ListCommand<Post<BibTex>>(command);
			
			bmListCommand.setList(bmPosts);
			pbListCommand.setList(pbPosts);
			
			aspectsBMPosts.put(aspect.getKey(), bmListCommand);
			aspectsPBPosts.put(aspect.getKey(), pbListCommand);

			// set tag cloud
			List<Tag> aspectTagCloud= logic.getTags(Resource.class, GroupingEntity.FRIEND, loginUser.getName(), null, aspectTags, null, Order.FREQUENCY, 0, 25, null, null);
			final TagCloudCommand tagCloudCommand = new TagCloudCommand();
			tagCloudCommand.setMaxCount(TAG_CLOUD_SIZE);
			tagCloudCommand.setMinFreq(TAG_CLOUD_MINFREQ);
			tagCloudCommand.setSort(TAG_CLOUD_SORT);
			tagCloudCommand.setTags(aspectTagCloud);
			aspectsTagClouds.put(aspect.getKey(), tagCloudCommand);
			
		}
		
		command.setAspectsBMPosts(aspectsBMPosts);
		command.setAspectsPBPosts(aspectsPBPosts);
		command.setAspectsTagClouds(aspectsTagClouds);
		
		
		// all done
		return Views.SPHERELIST;
	}

}
