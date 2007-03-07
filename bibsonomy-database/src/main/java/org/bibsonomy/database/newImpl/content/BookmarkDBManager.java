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
import org.bibsonomy.database.managers.DatabaseManager;
import org.bibsonomy.database.managers.getpostsqueriesForBookmark.GetBookmarksByHash;
import org.bibsonomy.database.managers.getpostsqueriesForBookmark.GetBookmarksByHashForUser;
import org.bibsonomy.database.managers.getpostsqueriesForBookmark.GetBookmarksByTagNames;
import org.bibsonomy.database.managers.getpostsqueriesForBookmark.GetBookmarksByTagNamesAndUser;
import org.bibsonomy.database.managers.getpostsqueriesForBookmark.GetBookmarksForGroup;
import org.bibsonomy.database.managers.getpostsqueriesForBookmark.GetBookmarksForGroupAndTag;
import org.bibsonomy.database.managers.getpostsqueriesForBookmark.GetBookmarksForHomePage;
import org.bibsonomy.database.managers.getpostsqueriesForBookmark.GetBookmarksForUser;
import org.bibsonomy.database.managers.getpostsqueriesForBookmark.GetBookmarksPopular;
import org.bibsonomy.database.managers.getpostsqueriesForBookmark.GetBookmarksViewable;
import org.bibsonomy.database.managers.getpostsqueriesForBookmark.RequestHandlerForGetPosts;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/*
 * only for test case
 */





public class BookmarkDBManager extends AbstractContentDBManager {
	

	/*
	 * TODO: das hier auch als Singleton?!
	 */

	
	   RequestHandlerForGetPosts getBookmarksForUser =new GetBookmarksForUser();
	   RequestHandlerForGetPosts getBookmarksByHash =new GetBookmarksByHash();
	   RequestHandlerForGetPosts getBookmarksByHashForUser=new GetBookmarksByHashForUser();
	   RequestHandlerForGetPosts getBookmarksByTagNames=new GetBookmarksByTagNames();
	   RequestHandlerForGetPosts getBoomarksByTagNamesAndUser=new GetBookmarksByTagNamesAndUser();
	   RequestHandlerForGetPosts getBoomarksForGroup=new GetBookmarksForGroup();
	   RequestHandlerForGetPosts getBoomarksForGroupAndTag=new GetBookmarksForGroupAndTag();
	   RequestHandlerForGetPosts getBoomarksForHomePage=new GetBookmarksForHomePage();
	   RequestHandlerForGetPosts getBoomarksForUser=new GetBookmarksForUser();
	   RequestHandlerForGetPosts getBoomarksForPopular=new GetBookmarksPopular();
	   RequestHandlerForGetPosts getBoomarksViewable=new GetBookmarksViewable();
	   //RequestHandlerForGetPosts getBookmarksOfFriendsByUser=new GetBookmarksOfFriendsByUser();
	  //RequestHandlerForGetPosts getBookmarksByUserFriends =new GetBookmarksByUserFriends() ;
	   //RequestHandlerForGetPosts getBookmarksConceptForUser =new GetBookmarksByConceptForUser() ;
	   //RequestHandlerForGetPosts getBookmarksOfFriendsByTags=new GetBookmarksOfFriendsByTags();
   /*
    * Selection of appropriate methods follows model of chain of responsibility
    */
	public BookmarkDBManager() {
		
		 //getBookmarksConceptForUser =new GetBookmarksByConceptForUser();  
		   
		//getBookmarksOfFriendsByTags=new GetBookmarksOfFriendsByTags();
	    getBoomarksForUser=new GetBookmarksForUser();
	    getBookmarksByHashForUser = new GetBookmarksByHashForUser();  
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
	
	@Override
	
	public List<Post<? extends Resource>> getPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous) {
        
	/*
	 * For test options
	 * 
	 * */
		
        /*List <String> testtags=new LinkedList<String>();
		
		testtags.add("gps");*/
		
		List test_getBookmarksBoomarksForUser =getBoomarksForUser.perform("jaeschke", GroupingEntity.USER, "jaeschke",null,null,false, false, 0, 19);
		System.out.println("test="+test_getBookmarksBoomarksForUser.size());
		System.out.println("authUser = " + authUser);
		System.out.println("grouping = " + grouping);
		System.out.println("groupingName = " + groupingName);
		System.out.println("tags = " + tags);
		System.out.println("hash = " + hash);
		System.out.println("start = " + start);
		System.out.println("end = " + end);

		List<Post<? extends Resource>> posts = getBoomarksForUser.perform(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
		System.out.println("BoookmarkDbManager posts.size= " + posts.size());
		return posts;
		
	}

	
	
	@Override
	public Post<Resource> getPostDetails(String authUser, String resourceHash, String userName) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public boolean deletePost(String userName, String resourceHash) {
		
		DatabaseManager databasemanager = new DatabaseManager();
		GetBookmarksByHashForUser get =new GetBookmarksByHashForUser();
        BookmarkParam paramFromUrlValue=new BookmarkParam();
        
		paramFromUrlValue.setUserName(userName);
		paramFromUrlValue.setHash(resourceHash);	
		
		String hashFromUrl=paramFromUrlValue.getResource().getIntraHash();
		String userFromUrl=paramFromUrlValue.getUserName();
		
		Post<? extends Resource> storeTemp=(Post<? extends Resource>) get.perform(userFromUrl,GroupingEntity.USER,userFromUrl,null,hashFromUrl,false, false, 0, 1);
		databasemanager.bookmarkDatabaseManager.updateBookmarkHashDec(paramFromUrlValue);
		  	/*** copy the bookmark entries into the log_Bookmark table ***/
		databasemanager.bookmarkDatabaseManager.insertBookmarkLog(paramFromUrlValue);
		  	/***delete the selected bookmark from the current database table***/ 					  
	    databasemanager.bookmarkDatabaseManager.deleteBookmarkByContentId(paramFromUrlValue);
		paramFromUrlValue.setTags(storeTemp.getTags()); 
		databasemanager.tagDatabaseManager.deleteTags(paramFromUrlValue);
		return true;
	}



	@Override
	public boolean storePost(String userName, Post post, boolean update) {
		
	    DatabaseManager db = new DatabaseManager();
		BookmarkParam paramFromUrlValue =new BookmarkParam();
		paramFromUrlValue.setUserName(userName);
		paramFromUrlValue.setHash(post.getResource().getIntraHash());	
		String hashFromUrl=paramFromUrlValue.getResource().getIntraHash();
		String userFromUrl=paramFromUrlValue.getUserName();
		
	    /*
	     * check, if current user is a spammer 
	    
	    
	    if(post.getUser().isSpammer()==true){

	    	paramFromUrlValue.setSpammer(ConstantID.SPAMMER_TRUE);
	    	
	    }
	    
	    else{
	    	
	    	paramFromUrlValue.setSpammer(ConstantID.SPAMMER_FALSE);
	    	
	    }
	    
	    
	    if(paramFromUrlValue.getSpammer()==ConstantID.SPAMMER_TRUE.getId()) {
	    	
	    	/* TODO
	    	 * if spammer, then set  a new groupID (for external invisibility) without any loss of group information
	    	 */
	    	
		/*	paramFromUrlValue.setGroupId(-3);
		} */
	
	    
	    
	    /*
	     * check  preassumptions:
	     * TODO  check if a post is valid!!
	     * hash=!null, user!=null, post should be valide, update=true
	     */
	    
	    
	    
	    if(update==true && post.getResource().getIntraHash()!=null && post.getUser().getName()!=null){
	    	
	    	GetBookmarksByHashForUser getBookmarksByHashForUser =new GetBookmarksByHashForUser();
	    	
	    	/*
	    	 * create database object with given Arguments from current URL 
	    	 */
	    	
        	Post<? extends Resource> storeTemp=(Post<? extends Resource>) getBookmarksByHashForUser.perform(userFromUrl,GroupingEntity.USER,userFromUrl,null,hashFromUrl,false, false, 0, 1);
	    	
        	/*
        	 * request object from database does not exist for attribute hash and bookmark
        	 */
        	
        	if(storeTemp.getResource().getIntraHash()==null){
        		
        		throw new IllegalArgumentException("No bookmark for hash and user into current database");
	    	
        	}     	
        	
        	/*
        	 * get Hash from database object, rsp. we get a  post from database
        	 * according arguments and compute a hash from current url and compare
        	 * it with hash from already existing hash in database   
        	 */
        	
        	 else if(storeTemp.getResource().getIntraHash()!= null){
        		
        		/*
        		 * compute new hash for bookmark object from URL
        		 */
        		 
        		final String newHash=((Bookmark) storeTemp.getResource()).getHash();
        		 
        		 
        		 if(newHash==storeTemp.getResource().getIntraHash()){
        			
        			   /* 
        	  		    * 1. save content_id to logged_bookmark or 
        	  		    * 2. insert bookmark (with old and new contend_id) object into queue for logging
        	  		    * 3. delete old bookmark from database
        	  		    * 4. insert changed bookmark into database
        	  		    * 
        	  		 	* insert bookmark object with changed paramter values:
        	  		 	* 1. store both old and new contend'ids (are not equal)
        	  		 	* 2. create unique content_id from table id_generator
        	  		 	*/
        	  		 
        	  		paramFromUrlValue.setContendIDbyBookmark(db.bookmarkDatabaseManager.getContentIDForBookmark(paramFromUrlValue));  
        	  		paramFromUrlValue.setNewContentId(db.bookmarkDatabaseManager.getNewContentID(paramFromUrlValue));	
        	  		paramFromUrlValue.setHash(newHash);
        	  		 /* 
        	  		  *  database get old and new contentID from Bookmark object
        	  		  */
        	  		 
        	  		 db.bookmarkDatabaseManager.updateBookmarkLog(paramFromUrlValue);
       	  		  
       	             /*oder TODO fillWithBookmarkObjact(paramFromUrlValue);*/
                           
        	  		   deletePost(paramFromUrlValue.getUserName(),paramFromUrlValue.getResource().getUrl());
        			   
        			   /*
        			    * insert current bookmark parameters 
        			    */
        			
        			   db.bookmarkDatabaseManager.insert("insertBookmark", paramFromUrlValue);
        			   
        			   /*
        			    * increment URL counter
        			    */
        			   
        			   db.bookmarkDatabaseManager.insert("insertBookmarkInc", paramFromUrlValue);
        			   
        			   /*
        			    * insert a set of tags according bookmark 
        			    */
        			   
        			   db.bookmarkDatabaseManager.insert("insertTag",paramFromUrlValue);   
        			
        			
        		} 
        		 
        		/*
        		 * if old hash is not equal to new generated hash
        		 */
        		 
        	 else if(newHash!=storeTemp.getResource().getIntraHash()){
        		 
        		 GetBookmarksByHashForUser proveNewHash =new GetBookmarksByHashForUser();
        		 
        		 /*
     	    	 * create database object with given Arguments from current URL and new generated hash 
     	    	 */
     	    	
             	Post<? extends Resource> existBookmarkForNewHash =(Post<? extends Resource>) proveNewHash.perform(userFromUrl,GroupingEntity.USER,userFromUrl,null,newHash,false, false, 0, 1);
        		 
             	/*
        		  * prove if a new Hash already exists in current database 
        		  */
        		 
        		 if(newHash!=existBookmarkForNewHash.getResource().getIntraHash()){
        			 
        			 
        			 /* 
      	  		    * 1. save content_id to logged_bookmark or 
      	  		    * 2. insert bookmark (with old and new contend_id) object into queue for logging
      	  		    * 3. delete old bookmark from database
      	  		    * 4. insert changed bookmark into database
      	  		    * 
      	  		 	* insert bookmark object with changed paramter values:
      	  		 	* 1. store both old and new contend'ids (are not equal)
      	  		 	* 2. create unique content_id from table id_generator
      	  		 	*/
      	  		 
      	  		paramFromUrlValue.setContendIDbyBookmark(db.bookmarkDatabaseManager.getContentIDForBookmark(paramFromUrlValue));  
      	  		paramFromUrlValue.setNewContentId(db.bookmarkDatabaseManager.getNewContentID(paramFromUrlValue));	
      	  		paramFromUrlValue.setHash(newHash);
      	  		 /* 
      	  		  *  database get old and new contentID from Bookmark object
      	  		  */
      	  		 
      	  		 db.bookmarkDatabaseManager.updateBookmarkLog(paramFromUrlValue);
     	  		  
     	             /*oder TODO fillWithBookmarkObjact(paramFromUrlValue);*/
                         
      	  		   deletePost(paramFromUrlValue.getUserName(),paramFromUrlValue.getResource().getUrl());
      	  		 
      			   
      			   /*
      			    * insert current bookmark parameters 
      			    */
      			
      			   db.bookmarkDatabaseManager.insert("insertBookmark", paramFromUrlValue);
      			   
      			   /*
      			    * increment URL counter
      			    */
      			   
      			   db.bookmarkDatabaseManager.insert("insertBookmarkInc", paramFromUrlValue);
      			   
      			   /*
      			    * insert a set of tags according bookmark 
      			    */
      			   
      			   db.bookmarkDatabaseManager.insert("insertTag",paramFromUrlValue);   
      			 
        			 
        			 
        			 
        			 
        		 }
        			 
        			 
        		 else{
        			/*
        			 * new hash already exists in database.
        			 */
        			 
        			 throw new IllegalArgumentException("Bookmark already exist");
        			 
        			 
        		 }
        			 
        			
        		}
        		
        		
        	 }
	    
	    
        	}
	    
	    
	    else{
	    	
	    	/*
	    	 * user would like to post a bookmark, which is assumed as unexisting
	    	 * TODO  handling and proving valid post
             * TODO exception handling	    	
	    	 */
	    	
	    	
	    	if(update==false && post.getUser().getName()!=null){
	    		
	    		GetBookmarksByHashForUser get =new GetBookmarksByHashForUser();
		    	
		    	/*
		    	 * create database object with given Arguments from current URL 
		    	 */
		    	
	        	Post<? extends Resource> storeTemp=(Post<? extends Resource>) get.perform(userFromUrl,GroupingEntity.USER,userFromUrl,null,hashFromUrl,false, false, 0, 1);
	    		
	        	/*
	    		 * hash already exists in database
	    		 */
	        	
	    		if(storeTemp.getResource().getIntraHash()!=null){
	    		
	    			throw new IllegalArgumentException("Hash already exist");
	    			
	    			
	    		}
	    		
	    		/*
	    		 * hash does not exist in database, hence the sequence for insertion a bookmark is executed
	    		 */
	    		
	    		else if(storeTemp.getResource().getIntraHash()==null){
	    			
	    			db.bookmarkDatabaseManager.insert("insertBookmark", paramFromUrlValue);
     			   
     			   /*
     			    * increment URL counter
     			    */
     			   
     			   db.bookmarkDatabaseManager.insert("insertBookmarkInc", paramFromUrlValue);
     			   
     			   /*
     			    * insert a set of tags according bookmark 
     			    */
     			   
     			   db.bookmarkDatabaseManager.insert("insertTag",paramFromUrlValue);   
	    		
	    		
	    				}
	    		
	    	     }
	    	
	    	}
	    	
        return true;
	}
}