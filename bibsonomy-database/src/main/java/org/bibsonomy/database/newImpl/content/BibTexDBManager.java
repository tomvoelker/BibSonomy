package org.bibsonomy.database.newImpl.content;



/*******
* 
* @author mgr
*
**/
 /*
 * TODO check
 */

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.getpostsqueriesForBibtex.GetBibtexByHash;
import org.bibsonomy.database.managers.getpostsqueriesForBibtex.GetBibtexByHashForUser;
import org.bibsonomy.database.managers.getpostsqueriesForBibtex.GetBibtexByTagNames;
import org.bibsonomy.database.managers.getpostsqueriesForBibtex.GetBibtexByTagNamesAndUser;
import org.bibsonomy.database.managers.getpostsqueriesForBibtex.GetBibtexForGroup;
import org.bibsonomy.database.managers.getpostsqueriesForBibtex.GetBibtexForGroupAndTag;
import org.bibsonomy.database.managers.getpostsqueriesForBibtex.GetBibtexForHomePage;
import org.bibsonomy.database.managers.getpostsqueriesForBibtex.GetBibtexForUser;
import org.bibsonomy.database.managers.getpostsqueriesForBibtex.GetBibtexPopular;
import org.bibsonomy.database.managers.getpostsqueriesForBibtex.GetBibtexViewable;
import org.bibsonomy.database.managers.getpostsqueriesForBibtex.RequestHandlerForGetBibTexPosts;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/*
 * only for test case
 */





public class BibTexDBManager implements AbstractContentDBManager {
	

	/*
	 * TODO: das hier auch als Singleton?!
	 */

	
	   /*RequestHandlerForGetPosts getBibTexByHash =new GetBibtexByHash();
	   RequestHandlerForGetPosts getBibTexByHashForUser=new GetBibtexByHashForUser();
	   RequestHandlerForGetPosts getBibTexByTagNames=new GetBibtexByTagNames();
	   RequestHandlerForGetPosts getBibTexByTagNamesAndUser=new GetBibtexByTagNamesAndUser();
	   RequestHandlerForGetPosts getBibTexForGroup=new GetBibtexForGroup();
	   RequestHandlerForGetPosts getBibTexForGroupAndTag=new GetBibtexForGroupAndTag();
	   RequestHandlerForGetPosts getBibTexForHomePage=new GetBibtexForHomePage();
	   RequestHandlerForGetPosts getBibTexForPopular=new GetBibtexPopular();
	   RequestHandlerForGetPosts getBibTexViewable=new GetBibtexViewable();*/
	   
	   RequestHandlerForGetBibTexPosts getBibTexForUser=new GetBibtexForUser();
	   
   /*
    * Selection of appropriate methods follows model of chain of responsibility
    */
	public BibTexDBManager() {
          
		 getBibTexForUser=new GetBibtexForUser();
		 
		 
		 
		 /*
		  * TODO not tested
		  */
		//getBookmarksOfFriendsByTags=new GetBookmarksOfFriendsByTags();
		/*getBoomarksForHomePage.setNext(getBoomarksForPopular);
		getBoomarksForPopular.setNext(getBookmarksForUser);
		getBoomarksForUser.setNext(getBookmarksByTagNames);
		getBookmarksByTagNames.setNext(getBookmarksByHashForUser); 
		getBookmarksByHashForUser.setNext(getBookmarksByHash); 
		getBookmarksByHash.setNext(getBoomarksByTagNamesAndUser);
		getBoomarksByTagNamesAndUser.setNext(getBoomarksForGroup); 
		getBoomarksForGroup.setNext(getBoomarksForGroupAndTag); 
		getBoomarksForGroupAndTag.setNext(getBoomarksViewable);*/
		
		/* 
		 * getBoomarksViewable.setNext(getBookmarksByUserFriends);
		 * getBookmarksByUserFriends.setNext(getBookmarksConcept);
		 */
		
	}
	
	public List<Post<? extends Resource>> getPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous) {
        
		/*
		 * Test options
		 */
		List test_getBibtexForUser =getBibTexForUser.perform("jaeschke", GroupingEntity.USER, "jaeschke",null,null,false, false, 0, 19);
		System.out.println("test="+test_getBibtexForUser.size());
		System.out.println("authUser = " + authUser);
		System.out.println("grouping = " + grouping);
		System.out.println("groupingName = " + groupingName);
		System.out.println("tags = " + tags);
		System.out.println("hash = " + hash);
		System.out.println("start = " + start);
		System.out.println("end = " + end);

		List<Post<? extends Resource>> posts = getBibTexForUser.perform(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
		System.out.println("BibTexDbManager posts.size= " + posts.size());
		return posts;
		
	}

	
	
	public Post<Resource> getPostDetails(String authUser, String resourceHash, String userName) {
		// TODO Auto-generated method stub
		return null;
	}


	public boolean deletePost(String userName, String resourceHash) {
	
		return false;
	}

	public boolean storePost(String userName, Post post, boolean update) {
		
	    	
        return true;
	}
}