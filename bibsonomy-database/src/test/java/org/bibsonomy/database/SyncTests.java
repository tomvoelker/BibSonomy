package org.bibsonomy.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.BibTexDatabaseManager;
import org.bibsonomy.database.managers.BookmarkDatabaseManager;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.ConflictResolutionStrategy;
import org.bibsonomy.model.sync.SyncLogicInterface;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.sync.SynchronizationStates;
import org.bibsonomy.sync.SynchronizationDatabaseManager;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wla
 * @version $Id$
 */
public class SyncTests extends AbstractDatabaseManagerTest {

    private static BibTexDatabaseManager bibTexDb;
    private static BookmarkDatabaseManager bookmarkDb;
    private static SyncLogicInterface dbLogic;
    private static SynchronizationDatabaseManager syncDb;

    private final static String userName = "Syncuser1";
    
    static DateFormat format;

    /**
     * sets up the used managers
     */
    @BeforeClass
    public static void setupDatabaseManager() {
	bookmarkDb = BookmarkDatabaseManager.getInstance();
	bibTexDb = BibTexDatabaseManager.getInstance();
	syncDb = SynchronizationDatabaseManager.getInstance();

	format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	User loginUser = new User();
	loginUser.setName(userName);
	dbLogic = new DBLogic(loginUser, getDbSessionFactory());
    }

    private HashMap<String, SynchronizationPost> listToMap(
	    List<SynchronizationPost> posts) {
	HashMap<String, SynchronizationPost> map = new HashMap<String, SynchronizationPost>();
	for (SynchronizationPost post : posts) {
	    map.put(post.getIntraHash(), post);
	}
	return map;
    }

    /**
     * test database
     */
    @Test
    public void testDatabase() {
	Map<String, SynchronizationPost> posts = bibTexDb
		.getSyncPostsMapForUser("Syncuser1", dbSession);

	assertEquals("wrong amount of bibtex in map", 5, posts.size());
	assertTrue(posts.containsKey("6a486c3b5cf17466f984f8090077274c"));
	assertTrue(posts.containsKey("b1629524db9c09f8b75af7ba83249980"));
	assertTrue(posts.containsKey("11db3d75b9e07960658984f9b012d6d7"));
	assertTrue(posts.containsKey("133de67269c9bfa71bde2b7615f0c1b3"));
	assertTrue(posts.containsKey("08cdf0d0dcce9d07fd8d41ac6267cadf"));

	posts = bookmarkDb.getSyncPostsMapForUser("Syncuser1", dbSession);
	assertEquals("wrong amount of bookmarks in map", 5, posts.size());
	assertTrue(posts.containsKey("6232752de0376fb6692917faf2e0a41e"));
	assertTrue(posts.containsKey("35b3ed178e437da1e93e2cac75333c67"));
	assertTrue(posts.containsKey("bcf7feb2dd4acba08f79b31991ed51bb"));
	assertTrue(posts.containsKey("c4bb293ee64fecf340db99b39f401008"));
	assertTrue(posts.containsKey("c7c8d5f682a6f32b7b3be9f3986a1cba"));
	
	int serviceID = 1;
	int contentType = 2;
	Date date = null;
	try {
	    date = format.parse("2011-02-02 23:00:00");
	} catch (ParseException ex) {
	    ex.printStackTrace();
	}
	Date lastSyncDate = syncDb.getLastSynchronizationDate(userName, serviceID, contentType, dbSession);
	assertNotNull("no last sync date received from db", lastSyncDate);
	assertEquals(date, lastSyncDate);
	
	List<SynchronizationPost> list = bibTexDb.getSyncPostsListForUser(userName, dbSession);
	assertEquals("wrong amount of bibtex in list", 5, list.size());
	
	list = bookmarkDb.getSyncPostsListForUser(userName, dbSession);
	assertEquals("wrong amount of bookmarks in list", 5, list.size());
    }
    
    @Test
    public void getSynchronizationBibTexTest() {

	Class<? extends Resource> resourceType = BibTex.class;
	ConflictResolutionStrategy strategy = ConflictResolutionStrategy.ASK_USER;

	String serviceIdentifier = "1"; // TODO replace this with correct
					// identifier

	List<SynchronizationPost> clientPosts = new LinkedList<SynchronizationPost>();

	SynchronizationPost post;

	try {
	    /*
	     * post 1: "post without changes" is the same post as in database
	     */
	    post = new SynchronizationPost();
	    post.setInterHash("69f46427bfed611701eef5aed85f3a28");
	    post.setIntraHash("6a486c3b5cf17466f984f8090077274c");
	    post.setChangeDate(format.parse("2011-01-31 14:32:00"));
	    post.setCreateDate(format.parse("2011-01-10 14:32:00"));
	    clientPosts.add(post);

	    /*
	     * post 2: "post deleted on server" here created and modified before
	     * last synchronization
	     */
	    post = new SynchronizationPost();
	    post.setInterHash("0cabab7456df24ce9111c8960af42c5d");
	    post.setIntraHash("167b670252215232dc59829364e361a2");
	    post.setChangeDate(format.parse("2009-11-02 12:23:00"));
	    post.setCreateDate(format.parse("2009-11-02 12:20:00"));
	    clientPosts.add(post);

	    /*
	     * post 3: "post deleted on client" is not in the client list
	     */

	    /*
	     * post 4: "post changed on server" same hashes and create date as
	     * in database, but change date is before last synchronization
	     */
	    post = new SynchronizationPost();
	    post.setInterHash("319872adc49bfeae3f799d29a18b0634");
	    post.setIntraHash("11db3d75b9e07960658984f9b012d6d7");
	    post.setChangeDate(format.parse("2011-01-16 17:58:00"));
	    post.setCreateDate(format.parse("2010-09-16 14:35:00"));
	    clientPosts.add(post);

	    /*
	     * post 5: "post changed on client" same hashes and create date as
	     * in database, but change date is after the last synchronization
	     * date
	     */
	    post = new SynchronizationPost();
	    post.setInterHash("2f0fc12a47ba98a11a2746376b118e48");
	    post.setIntraHash("133de67269c9bfa71bde2b7615f0c1b3");
	    post.setChangeDate(format.parse("2011-03-25 10:59:00"));
	    post.setCreateDate(format.parse("2009-12-31 23:59:00"));
	    clientPosts.add(post);

	    /*
	     * post 6: "post created on server" is not in the client list
	     */

	    /*
	     * post 7: "post created on client" created and modified after last
	     * synchronization
	     */
	    post = new SynchronizationPost();
	    post.setInterHash("66665c7e236f5e7111f6699fbd4015a2");
	    post.setIntraHash("418397b6f507faffe6f9b02569ffbc9e");
	    post.setChangeDate(format.parse("2011-03-18 14:13:00"));
	    post.setCreateDate(format.parse("2011-03-18 14:13:00"));
	    clientPosts.add(post);
	} catch (ParseException ex) {
	    ex.printStackTrace();
	}
	assertEquals(5, clientPosts.size());

	List<SynchronizationPost> synchronizedPosts = dbLogic
		.getSynchronization(userName, resourceType, clientPosts,
			strategy, serviceIdentifier);

	assertNotNull("no synchronized posts returned", synchronizedPosts);

	HashMap<String, SynchronizationPost> map = listToMap(synchronizedPosts);
	String hash;
	/*
	 * test post 1  "post without changes"
	 */
	hash = "6a486c3b5cf17466f984f8090077274c";
	assertTrue(map.containsKey(hash));
	assertEquals(SynchronizationStates.OK, map.get(hash).getState());
	
	/*
	 * test post 2 "post deleted on server"
	 */
	hash = "167b670252215232dc59829364e361a2";
	assertTrue(map.containsKey(hash));
	assertEquals(SynchronizationStates.DELETE_CLIENT, map.get(hash).getState());
	
	/*
	 * test post 3 "post deleted on client"
	 */
	hash = "b1629524db9c09f8b75af7ba83249980";
	assertTrue(map.containsKey(hash));
	assertEquals(SynchronizationStates.DELETE, map.get(hash).getState());
	
	/*
	 * test post 4 "post changed on server"
	 */
	hash = "11db3d75b9e07960658984f9b012d6d7";
	assertTrue(map.containsKey(hash));
	assertEquals(SynchronizationStates.UPDATE_CLIENT, map.get(hash).getState());
	
	/*
	 * test post 5 "post changed on client"
	 */
	hash = "133de67269c9bfa71bde2b7615f0c1b3";
	assertTrue(map.containsKey(hash));
	assertEquals(SynchronizationStates.UPDATE, map.get(hash).getState());
	
	/*
	 * test post 6 "post created on server"
	 */
	hash = "08cdf0d0dcce9d07fd8d41ac6267cadf";
	assertTrue(map.containsKey(hash));
	assertEquals(SynchronizationStates.CREATE_CLIENT, map.get(hash).getState());
	
	/*
	 * test post 7 "post created on client" 
	 */
	hash = "418397b6f507faffe6f9b02569ffbc9e";
	assertTrue(map.containsKey(hash));
	assertEquals(SynchronizationStates.CREATE, map.get(hash).getState());
    }

    public void getSynchronizationBookmarkTest() {
	//TODO ??
    }
    
}
