package org.bibsonomy.database.newImpl.content;



/*******
* 
* @author mgr
*
**/
 /*
 * TODO check
 */

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
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
import org.bibsonomy.model.Tag;

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
		getBookmarksByHashForUser =new GetBookmarksByHashForUser();
		//getBookmarksOfFriendsByTags=new GetBookmarksOfFriendsByTags();
	    getBoomarksForUser=new GetBookmarksForUser();
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
        
		List test_getBookmarksBoomarksForUser =getBoomarksForUser.perform("jaeschke", GroupingEntity.USER, "jaeschke",null,null,false, false, 0, 19);
		System.out.println("test="+test_getBookmarksBoomarksForUser.size());
		System.out.println("authUser = " + authUser);
		System.out.println("grouping = " + grouping);
		System.out.println("groupingName = " + groupingName);
		System.out.println("tags = " + tags);
		System.out.println("hash = " + hash);
		System.out.println("start = " + start);
		System.out.println("end = " + end);

		List<Post<? extends Resource>> posts = getBookmarksForUser.perform(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
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
		
		DatabaseManager db = new DatabaseManager();
		
		GetBookmarksByHashForUser get =new GetBookmarksByHashForUser();
        BookmarkParam paramDelete=new BookmarkParam();
        
		paramDelete.setUserName(userName);
		paramDelete.setHash(resourceHash);	
		
		System.out.println("paramFromValue in deleteBookmark " + paramDelete.getUserName()+ " " +paramDelete.getHash());
		
		String hashFromUrl=paramDelete.getHash();
		String userFromUrl=paramDelete.getUserName();
		
		System.out.println("hashFromUrl " + hashFromUrl);
		System.out.println("userFromUrl " + userFromUrl);
		
		/*
		 * return a bookmark object for current hash value
		 */
		
		List<Post<? extends Resource>> storeTemp=get.perform(userFromUrl,GroupingEntity.USER,userFromUrl,null,hashFromUrl,false, false, 0, 1);
		System.out.println("storeTemp.size: " + storeTemp.size());
		if(storeTemp.size()==0){
			System.out.println("bookmark ist schon gelöscht");
			
		}
		Post<? extends Resource> provePost =storeTemp.get(0);
	    
	    paramDelete.setRequestedContentId(provePost.getContentId());
        System.out.println("paramDelete.getRequestedContentId " +paramDelete.getRequestedContentId());
        
        /*
         * counter in urls table is decremented (-1)
         */
        
		db.bookmarkDatabaseManager.updateBookmarkHashDec(paramDelete);

		/***delete the selected bookmark (by given contentId) from current database table***/ 	
		
	    db.bookmarkDatabaseManager.deleteBookmarkByContentId(paramDelete);
	    System.out.println("Lösche bookmark bei gegebener Content_Id");
	    
	    db.tagDatabaseManager.deleteTas(paramDelete);
	    System.out.println("Lösche TAS wieder");
	    
		return true;
	}



	@SuppressWarnings("unchecked")
	@Override
	public boolean storePost(String userName, Post post, boolean update) {
		/*
    	 * TODO  handling and proving valid post
    	 */
	    DatabaseManager db = new DatabaseManager();
		BookmarkParam bookmarkParam =new BookmarkParam();
		Bookmark bookmark =new Bookmark();
	
		bookmarkParam.setUserName(userName);
		bookmarkParam.setHash(post.getResource().getIntraHash());
		bookmarkParam.setDescription(post.getResource().getDescription());
		bookmarkParam.setDate(post.getDate());
		bookmarkParam.setGroupId(post.getGroupId());
		bookmarkParam.setResource(bookmark);

		bookmarkParam.setTags(post.getTags());
		List <Tag> tagliste=new LinkedList<Tag>();
		tagliste=post.getTags();
		
		
		String hashFromUrl=bookmarkParam.getHash();
		String userFromUrl=bookmarkParam.getUserName();
        
		/* TODO: if current user is a spammer*/ 
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
	     *  User would like an existing bookmark
	     */
	    
	    if(update==true && post.getResource().getIntraHash()!=null && post.getUser().getName()!=null){
	    	System.out.println("******************************************************");
	    	System.out.println("User möchte bereits bestehenden Eintrag aktualisieren");
	    	System.out.println("******************************************************");
	    	
	    	System.out.println("post hash "+ post.getResource().getIntraHash());
	    	System.out.println("post userName "+ post.getUser().getName());
	    	System.out.println("post groupID: " +post.getGroupId());
	    	
	    	/*
	    	 * create bookmark object from database with current 
	    	 * hash and user values (getBookmarkByHashForUser)
	    	 */
	    	
	    	List<Post<? extends Resource>> storeTemp =getBookmarksByHashForUser.perform(userFromUrl,GroupingEntity.USER,userFromUrl,null,hashFromUrl,false, false, 0, 1);
        	
	    	System.out.println("storeTemp.size()= "+storeTemp.size());
			System.out.println("authUser = " + userFromUrl);
			System.out.println("grouping = " + GroupingEntity.USER);
			System.out.println("groupingName = " + userFromUrl);
			System.out.println("hash = " + hashFromUrl);
	    	
        	/*
        	 * request object from database does not exist for attribute hash and user, 
        	 */
			
        	if(storeTemp.size()==0){
        		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        		System.out.println("++++++++++++++++kein Bookmark Objekt vorhanden zu aktuellem Hash und User++++++++++");
        		System.out.println("++++++++++++++++++++EXCEPTION++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        	}     	
        	
        	/*
        	 * get Hash from database object, rsp. we get a  post from database
        	 * according arguments and compute a new hash from current url and compare
        	 * it with the hash from already existing hash in our database
        	 */
        	
        	 else 
        		 {
        		 Post<? extends Resource> provePost =storeTemp.get(0);
        		 System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                 System.out.println("+++++++Objekt wird zurückgeliefert für die Werte Hash und aktueller User++++++++++++");     
                 System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
       		     System.out.println("getUserName() = " + provePost.getUser().getName());
     			 System.out.println("getIntraHash() = " + provePost.getResource().getIntraHash());	
        		
     			/*
        		 * compute new hash for bookmark object from URL
        		 * and compare it with hash from database
        		 */
        		 
        		 /**TODO compute new hash from URL******
        		  * with bookmark.getHash
        		  * TODO add newHash and oldHash also to logging object
        		  */
        		  
        		 String newHash="8643901092ebe3ef26cb4a33b4b16b8d";
        		 System.out.println("new Hash "+ newHash);
        		 
        		 /*
        		  * computed hash equal to saved hash
        		  */
        		
        		 if(newHash.equals(provePost.getResource().getIntraHash())){
        			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); 
        			System.out.println("Neu berechneter Hash entspricht altem Hash in der Datenbank+++");
        			System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        			
                    System.out.println("Reihenfolge: loggen, löschen, einfügen");
        			 
        			 /*
        			  * TODO  LOGGEN  QUEUE FUNKTION
        			  */
        			 
        			 
        			 /*
        			  * LÖSCHEN
        			  */
                    
        		     String oldHash=provePost.getResource().getIntraHash();
        			 deletePost(userName, oldHash);
        			 
        			 /*
        			  * EINFÜGEN
        			  */
        			 
        			 System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	                 System.out.println("++++++++++++++++++Objekt wird eingefügt++++++++++++++++++++++++++++++++++++++++++++");
	                 System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	                 
	                 bookmarkParam.setIdsType(ConstantID.IDS_CONTENT_ID);
	            	 System.out.println("paramFromUrlValue.getIdsType" + bookmarkParam.getIdsType());
	            	 
   	                 bookmarkParam.setId(db.bookmarkDatabaseManager.getCurrentContentIdFromIds(bookmarkParam));
   	                 System.out.println("bekomme aktuellen value/content_id aus ids table: "+ bookmarkParam.getId());
	            		
	            	 db.bookmarkDatabaseManager.updateIds(bookmarkParam);
	            	 bookmark.setContentId(db.bookmarkDatabaseManager.getCurrentContentIdFromIds(bookmarkParam));
	            	 bookmarkParam.setRequestedContentId(bookmark.getContentId());
	            	 System.out.println("bookmark content_id: " + bookmarkParam.getRequestedContentId());

	            	 bookmarkParam.setUrl(post.getUrl());
	          	  	 bookmarkParam.setIdsType(ConstantID.IDS_TAS_ID);

	          	   	 bookmarkParam.setResource(bookmark);
                     bookmarkParam.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
                     
                     
	          	  	System.out.println("bookmarkParam  TasId:"+ bookmarkParam.getNewTasId());
	          	  	System.out.println("bookmarkParam Content_ID :"+ bookmarkParam.getRequestedContentId());
	          	  	System.out.println("bookmarkParam: " +bookmarkParam.getContentType());
	          	    System.out.println("booomarkParam user_name: "+ bookmarkParam.getUserName());
	          	  	System.out.println("bookmarkParam date" +bookmarkParam.getDate());
	          	  	System.out.println("bookmarkParam group:" +bookmarkParam.getGroupId());
	          	    System.out.println("bookmarkParam hash " + bookmarkParam.getHash());
	          	  	System.out.println("bookmarkParam url: "+ bookmarkParam.getUrl());
	          	  	System.out.println("bookmarkParam description: "+ bookmarkParam.getDescription());
	          	        
	          	    System.out.println("bookmarkParam ids: " + bookmarkParam.getIdsType());
	          	    System.out.println("bookmarkParam tags: " +bookmarkParam.getTags());
	          	        
	                   
	                db.bookmarkDatabaseManager.insert("insertBookmark",bookmarkParam);
	                System.out.println("insert Bookmark into bookmark table");
                        
	     		   /*
     			    * increment URL counter and insert hash
     			    */
	                    
	     			  db.bookmarkDatabaseManager.insert("insertBookmarkInc",bookmarkParam);
	     			  System.out.println("zähle URL counter hoch plus hash plus url insert");
     			  
                 
	     			  
	     			  
	     			 for(Tag tag: tagliste){
	     				 
	     				bookmarkParam.setId(db.bookmarkDatabaseManager.getCurrentTasIdFromIds(bookmarkParam));
		          	   	System.out.println("currentTasID:" +bookmarkParam.getId());
	     				 
		          	    db.bookmarkDatabaseManager.updateIds(bookmarkParam);
		          	    bookmarkParam.setNewTasId(db.bookmarkDatabaseManager.getCurrentTasIdFromIds(bookmarkParam));
	     				 
	     				 
	     				bookmarkParam.setTagName(tag.getName());
	     				db.tagDatabaseManager.insert("insertTas",bookmarkParam);
	     				 
	     				System.out.println("Tags in BookmarkObject heißen: "+ bookmarkParam.getTagName());
	     				System.out.println("TAS eingefügt");
	     			}
        			
        		} 
        		 
        		/*
        		 * if old hash is not equal to new generated hash
        		 */
        		 
        	 else{
        		 
        		 System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); 
     			 System.out.println("+neuer Hash ist ungleich dem alten Hash in der Datenbank++++++");
     			 System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
  
        		 /*
     	    	 * create database object with given Arguments from current URL and new generated hash 
     	    	 */
     	    	
        		List<Post<? extends Resource>> storeTempWithNewHash = getBookmarksByHashForUser.perform(userFromUrl,GroupingEntity.USER,userFromUrl,null,newHash,false, false, 0, 1);
        		 
     			System.out.println("authUser = " + userFromUrl);
     			System.out.println("grouping = " + GroupingEntity.USER);
     			System.out.println("groupingName = " + userFromUrl);
     			System.out.println("newhash = " + newHash);
     			System.out.println("size storeTempWithNewHash: " +storeTempWithNewHash.size());
     			
     			
     		 /*
       		  * prove if new Hash already exists in database 
       		  */
     			
     			if(storeTempWithNewHash.size()==0){
     				
        			System.out.println("wenn neuer Hash nicht in der Datenbank/ungleich dem hash in database");
        			System.out.println("Reihenfolge: loggen, löschen, einfügen ( mit neuem Hash)");
        			 
        			 /*
        			  * TODO  LOGGEN  QUEUE FUNKTION
        			  */
        			 
        			 
        			 /*
        			  * LÖSCHEN
        			  */
        			 
        		     String oldHash=provePost.getResource().getIntraHash();
        		     System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	                 System.out.println("++++++++++++++++++Lösche Objekt mit altem Hash Wert++++++++++++++++++++++++++++++++");
	                 System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        			 deletePost(userName, oldHash);
        			 System.out.println("Objekt wurde gelöscht");
        			 
        			 /*
        			  * EINFÜGEN
        			  */
        			 
        			 System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	                 System.out.println("++++++++++++++++++Objekt mit neuem Hash wird eingefügt+++++++++++++++++++++++++++++");
	                 System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	                 
	                 bookmarkParam.setIdsType(ConstantID.IDS_CONTENT_ID);
	            	 System.out.println("paramFromUrlValue.getIdsType" + bookmarkParam.getIdsType());
	            	 bookmarkParam.setHash(newHash);
   	                 bookmarkParam.setId(db.bookmarkDatabaseManager.getCurrentContentIdFromIds(bookmarkParam));
   	                 System.out.println("bekomme aktuellen value/content_id aus ids table: "+ bookmarkParam.getId());
	            		
	            	 db.bookmarkDatabaseManager.updateIds(bookmarkParam);
	            	 
	            	 bookmark.setContentId(db.bookmarkDatabaseManager.getCurrentContentIdFromIds(bookmarkParam));
	            	 bookmarkParam.setRequestedContentId(bookmark.getContentId());
	          	  	 bookmarkParam.setUrl(post.getUrl());
	          	  	 bookmarkParam.setIdsType(ConstantID.IDS_TAS_ID);
	          	   	 bookmarkParam.setResource(bookmark);
                     bookmarkParam.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
                     
                     
	          	  	System.out.println("bookmarkParam  TasId:"+ bookmarkParam.getNewTasId());
	          	  	System.out.println("bookmarkParam Content_ID :"+ bookmarkParam.getRequestedContentId());
	          	  	System.out.println("bookmarkParam: " +bookmarkParam.getContentType());
	          	  	System.out.println("booomarkParam user_name: "+ bookmarkParam.getUserName());
	          	  	System.out.println("bookmarkParam date" +bookmarkParam.getDate());
	          	  	System.out.println("bookmarkParam group:" +bookmarkParam.getGroupId());
	          	    System.out.println("bookmarkParam hash " + bookmarkParam.getHash());
	          	  	System.out.println("bookmarkParam url: "+ bookmarkParam.getUrl());
	          	  	System.out.println("bookmarkParam description: "+ bookmarkParam.getDescription());
	          	        
	          	    System.out.println("bookmarkParam ids: " + bookmarkParam.getIdsType());
	          	    System.out.println("bookmarkParam tags: " +bookmarkParam.getTags());
	          	        
	                   
	                    db.bookmarkDatabaseManager.insert("insertBookmark",bookmarkParam);
	                    System.out.println("insert Bookmark into bookmark table");
                        
	     		   /*
     			    * increment URL counter and insert hash
     			    */
	                    
	     			  db.bookmarkDatabaseManager.insert("insertBookmarkInc",bookmarkParam);
	     			  System.out.println("zähle URL counter hoch plus hash plus url insert");
	     			  
	     			 for(Tag tag: tagliste){
	     				 
	     				bookmarkParam.setId(db.bookmarkDatabaseManager.getCurrentTasIdFromIds(bookmarkParam));
		          	   	System.out.println("currentTasID:" +bookmarkParam.getId());
	     				 
		          	    db.bookmarkDatabaseManager.updateIds(bookmarkParam);
		          	    bookmarkParam.setNewTasId(db.bookmarkDatabaseManager.getCurrentTasIdFromIds(bookmarkParam));
	     				 
	     				 
	     				bookmarkParam.setTagName(tag.getName());
	     				db.tagDatabaseManager.insert("insertTas",bookmarkParam);
	     				 
	     				System.out.println("Tags in BookmarkObject heißen: "+ bookmarkParam.getTagName());
	     				System.out.println("TAS eingefügt");
	     			}
        		 } /*end if*/

     			
     			else{
     			/*
    			 * new hash already exists in database.
    			 */
 				
    			 System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    			 System.out.println("++++++++++++++++++++++++++Bookmark already exists++++++++++++++++++++++++++++++++++");
    			 System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");   
     			
     			}
     			
        	} /*end else*/
          }
	    
	    
        } /*end if*/
	    
	    
	    else{
	    	
	    	/* user would like to post a bookmark, which is assumed as unexisting*/
	    	
	    	System.out.println("*************************************************************************************************");
	    	System.out.println("*************************User möchte neuen Eintrag zur DB hinzufügen*****************************");
	    	System.out.println("*************************************************************************************************");
	    	
	    	if(update==false && post.getUser().getName()!=null){
		    	/*
		    	 * create database object with given Arguments from current URL 
		    	 */
	    		List<Post<? extends Resource>> storeTemp = getBookmarksByHashForUser.perform(userFromUrl,GroupingEntity.USER,userFromUrl,null,hashFromUrl,false, false, 0, 1);
	    		System.out.println("sizeOf storeTemp: " + storeTemp.size());
	        	
	    		/*
	    		 * hash already exists in database
	    		 */
	    		
	    		if(storeTemp.size()!=0){
	    		
	    			System.out.println("****************************************************************************************");
	    			System.out.println("************************Hash already exists*********************************************");
	    			System.out.println("******************************BREAK*****************************************************");
	    			
	    		}
	    		
	    		/*
	    		 * hash does not exist in database, hence the sequence for insertion a bookmark is executed
	    		 */
	    		
	    		else if(storeTemp.size()==0){
	    		     
	        		 System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	                 System.out.println("+++++++es wird kein Objekt für aktuelle Wertbelegung zurückgegeben+++++++++++++++++");     
	                 System.out.println("++++++++++++++++++Objekt wird eingefügt++++++++++++++++++++++++++++++++++++++++++++");
	    			 
	                 bookmarkParam.setIdsType(ConstantID.IDS_CONTENT_ID);
	            	 System.out.println("bookmarkParam IdsType" + bookmarkParam.getIdsType());
	            	 
   	                 bookmarkParam.setId(db.bookmarkDatabaseManager.getCurrentContentIdFromIds(bookmarkParam));
   	                 System.out.println("bookmarkParam currentContentIDFromIds"+ bookmarkParam.getId());
	            		
	            	 db.bookmarkDatabaseManager.updateIds(bookmarkParam);
	            	 
	            	 bookmark.setContentId(db.bookmarkDatabaseManager.getCurrentContentIdFromIds(bookmarkParam));
	            	 bookmarkParam.setRequestedContentId(bookmark.getContentId());

	            	 bookmarkParam.setUrl(post.getUrl());
	          	  	 bookmarkParam.setIdsType(ConstantID.IDS_TAS_ID);
	          	  	 
	          	   	 bookmarkParam.setResource(bookmark);
                     bookmarkParam.setContentType(ConstantID.BOOKMARK_CONTENT_TYPE);
                     
                     
	          	  		System.out.println("bookmarkParam  TasID:"+ bookmarkParam.getNewTasId());
	          	  		System.out.println("bookmarkParam ContentID :"+ bookmarkParam.getRequestedContentId());
	          	  		System.out.println("bookmarkParam ContentType: " +bookmarkParam.getContentType());
	          	  	    System.out.println("booomarkParam userName: "+ bookmarkParam.getUserName());
	          	  	    System.out.println("bookmarkParam date" +bookmarkParam.getDate());
	          	  	    System.out.println("bookmarkParam group:" +bookmarkParam.getGroupId());
	          	  		System.out.println("bookmarkParam hash " + bookmarkParam.getHash());
	          	  		System.out.println("bookmarkParam url: "+ bookmarkParam.getUrl());
	          	  	    System.out.println("bookmarkParam description: "+ bookmarkParam.getDescription());
	          	        System.out.println("bookmarkParam idsType: " + bookmarkParam.getIdsType());
	          	        
	                   
	                    db.bookmarkDatabaseManager.insert("insertBookmark",bookmarkParam);
	                    System.out.println("insert Bookmark into bookmark table");
                        
	     		   /*
     			    * increment URL counter and insert hash
     			    */
	                    
	     			  db.bookmarkDatabaseManager.insert("insertBookmarkInc",bookmarkParam);
	     			  System.out.println("zähle URL counter hoch plus hash plus url insert");
     			  
	     			 for(Tag tag: tagliste){
	     				 
	     				bookmarkParam.setId(db.bookmarkDatabaseManager.getCurrentTasIdFromIds(bookmarkParam));
		          	   	System.out.println("currentTasID:" +bookmarkParam.getId());
	     				 
		          	    db.bookmarkDatabaseManager.updateIds(bookmarkParam);
		          	    bookmarkParam.setNewTasId(db.bookmarkDatabaseManager.getCurrentTasIdFromIds(bookmarkParam));
	     				 
	     				 
	     				bookmarkParam.setTagName(tag.getName());
	     				db.tagDatabaseManager.insert("insertTas",bookmarkParam);
	     				 
	     				System.out.println("Tags in BookmarkObject heißen: "+ bookmarkParam.getTagName());
	     				System.out.println("TAS eingefügt");
	     			}
	     			 
     			      
	    		
	    				} /*end else*/
	    		
	    	     } /*end if*/
	    	
	    	} /*end else*/
	    	
        return true;
	}
}