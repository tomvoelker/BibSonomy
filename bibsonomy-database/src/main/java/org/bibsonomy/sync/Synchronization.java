package org.bibsonomy.sync;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bibsonomy.model.synch.SynchronizationPost;
import org.bibsonomy.model.synch.SynchronizationStates;

/**
 * @author wla
 * @version $Id$
 */
public abstract class Synchronization {

    private ConflictResolutionStrategy conflictStrategy;

    /**
     * 
     * @param clientList list of client SynchPosts
     * @param serverList list of server SynchPosts 
     * @param synchDate date of the last synchronization
     * @return list of SynchPosts with set synchronization state.
     */
    public List<SynchronizationPost> synchronizePosts(List<SynchronizationPost> clientList,
	    List<SynchronizationPost> serverList, Date synchDate) {
	/*
	 * create hashmap form server posts for efficient search
	 */
	HashMap<String, SynchronizationPost> serverPosts = new HashMap<String, SynchronizationPost>();
	for (SynchronizationPost post : serverList) {
	    serverPosts.put(post.getInterHash(), post);
	}
	
	/*
	 * TODO plan:
	 *	try to find every client post on server. If found them -> synchronize and remove from server map.
	 *	Else is this a new post in client or deleted from server. Handle this case.
	 *	Every remaining post in server map is new or deleted from client. Handle this case and add 
	 *	required posts to client list.
	 */

	/*
	 * check all posts from client, remove founded posts from server map
	 */
	for (SynchronizationPost post : clientList) {
	    if (serverPosts.containsKey(post.getInterHash())) {// possibly same post is found 

		switch (synchronizeTwoPosts(post,
			serverPosts.get(post.getInterHash()), synchDate)) {
		//TODO finish this switch statement
		case OK:
		    serverPosts.remove(post.getInterHash());
		    break;
		case UPDATE:
		    break;
		case UPDATE_CLIENT:
		    break;
		case ASK:
		    break;
		default:
		    break;
		}

	    } else { // no such post found
		if (post.getCreateDate().getTime() < synchDate.getTime()) { // was deleted on server
		    //TODO hier check for conflict
		    post.setStatus(SynchronizationStates.DELETE_CLIENT);
		} else { // is new post
		    post.setStatus(SynchronizationStates.CREATE);
		}
	    }
	}

	return clientList;
    }

    private SynchronizationStates synchronizeTwoPosts(SynchronizationPost clientPost, SynchronizationPost serverPost, Date synchDate) {
	
	/*
	 * if same posts, nothing to do
	 */
	if (clientPost.same(serverPost)) { // posts are same, nothing to do
	    clientPost.setStatus(SynchronizationStates.OK);
	    return SynchronizationStates.OK;
	}

	
	if (clientPost.getIntraHash().equals(serverPost.getIntraHash())) { // same post but was changed
	    int timeIndex = clientPost.getChangeDate().compareTo(
		    serverPost.getChangeDate());
	    
	    if (timeIndex > 0) { // client has newer version	
		/* check server has also changes since last synchronization */
		timeIndex = serverPost.getChangeDate().compareTo(synchDate);
		if (timeIndex > 0) { // server also changed, confilct situation
		    
		    switch (conflictStrategy) {
		    case ASK_USER:
			clientPost.setStatus(SynchronizationStates.ASK);
			return SynchronizationStates.ASK;
		    case FIRST_WINS:
		    case SERVER_WINS:
			clientPost.setStatus(SynchronizationStates.UPDATE_CLIENT);
			return SynchronizationStates.UPDATE_CLIENT;
		    case LAST_WINS:
		    case CLIENT_WINS:
			clientPost.setStatus(SynchronizationStates.UPDATE);
			return SynchronizationStates.UPDATE;
		    }
		    
		} else if (timeIndex <= 0) { // server was not changed -> update on server
		    clientPost.setStatus(SynchronizationStates.UPDATE);
		    return SynchronizationStates.UPDATE;
		}

	    } else if (timeIndex < 0) { // server has newer version
		/* check client has also changes since last synchronization */
		timeIndex = clientPost.getChangeDate().compareTo(synchDate);
		if (timeIndex > 0 ) { //client also changed, conflict situation
		    switch (conflictStrategy) {
		    case ASK_USER:
			clientPost.setStatus(SynchronizationStates.ASK);
			return SynchronizationStates.ASK;
		    case FIRST_WINS: 
		    case CLIENT_WINS:
			clientPost.setStatus(SynchronizationStates.UPDATE);
			return SynchronizationStates.UPDATE;
		    case LAST_WINS: 
		    case SERVER_WINS:
			clientPost.setStatus(SynchronizationStates.UPDATE_CLIENT);
			return SynchronizationStates.UPDATE_CLIENT;
		    } 
		} else if (timeIndex <= 0) { // client was not changed -> update on client
		    clientPost.setStatus(SynchronizationStates.UPDATE);
		    return SynchronizationStates.UPDATE;
		}

	    } else { // same changeDate, same hashes, something is wrong
		     // TODO what to do in this case?
		return SynchronizationStates.UNDEFINED;
	    }
	} else { // TODO different posts? or more comparations possible? maybe
		 // same create date?
	    return SynchronizationStates.UNDEFINED;
	}
	return SynchronizationStates.UNDEFINED;

    }

    /**
     * @param conflictStrategy
     *            the conflictStrategy to set
     */
    public void setConflictStrategy(ConflictResolutionStrategy conflictStrategy) {
	this.conflictStrategy = conflictStrategy;
    }

    /**
     * @return the conflictStrategy
     */
    public ConflictResolutionStrategy getConflictStrategy() {
	return conflictStrategy;
    }

}
