package org.bibsonomy.lucene;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.services.searcher.ResourceSearch;

//FIXME: remove this comment (used only for triggering cvs-commit)

/**
 * abstract parent class for lucene search
 * 
 * @author fei
 *
 * @param <R> resource type
 */
public abstract class LuceneSearch<R extends Resource> implements ResourceSearch<R> {
	private static final Logger log = Logger.getLogger(LuceneSearch.class);
	
	/** logic interface for retrieving data from bibsonomy */
	private LogicInterface dbLogic;
	
	/** lucene index updater */
	private LuceneUpdater indexUpdater;
	
	/** known resource types */
	List<Class<? extends Resource>> resourceTypes = new LinkedList<Class<? extends Resource>>(); 
	
	
	/**
	 * flag/unflag spammer, depending on user.getPrediction()
	 */
	public void  flagSpammer(User user) {
		log.debug("flagSpammer called for user " + user.getName());
		switch( user.getPrediction() ) {
		case 0:
			log.debug("unflag non-spammer");
			List<Post<R>> userPosts = this.getDbLogic().getPosts(
						getResourceType(), 
						GroupingEntity.USER, 
						user.getName(), 
						new LinkedList<String>(), "", 
						Order.ADDED,
						FilterEntity.UNFILTERED, 
						0, 
						Integer.MAX_VALUE, 
						"");
			for( Post<R> post : userPosts ) {
				log.debug("removing post "+post.getResource().getTitle());
			}
			if( indexUpdater!=null ) {
				indexUpdater.unflagEntryAsSpam(userPosts);
			} else {
				log.error("Trying to flag spammer while no index updater present");
			}
			break;
		case 1:
			log.debug("flag spammer");
			if( indexUpdater!=null ) {
				indexUpdater.flagEntryAsSpam(user.getName());
			} else {
				log.error("Trying to flag spammer while no index updater present");
			}
			break;
		}
	}

	/** inserts all posts for given spammer into the index */
	abstract protected Class<R> getResourceType();

	public void setDbLogic(LogicInterface dbLogic) {
		this.dbLogic = dbLogic;
	}

	public LogicInterface getDbLogic() {
		return dbLogic;
	}

	public void setIndexUpdater(LuceneUpdater indexUpdater) {
		this.indexUpdater = indexUpdater;
	}

	public LuceneUpdater getIndexUpdater() {
		return indexUpdater;
	}

}
