package org.bibsonomy.database.managers;

import java.util.LinkedList;
import java.util.List;
import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByHashForUser;
import org.bibsonomy.database.params.BookmarkParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

/**
 * Used to retrieve bookmarks from the database.
 * 
 * @author Christian Schenk
 * @author mgr
 */
public class BookmarkDatabaseManager extends AbstractDatabaseManager implements CrudableContent {

	private final static BookmarkDatabaseManager singleton = new BookmarkDatabaseManager();
	private final GeneralDatabaseManager generalDb = GeneralDatabaseManager.getInstance();
	private final TagDatabaseManager tagDb = TagDatabaseManager.getInstance();

	private BookmarkDatabaseManager() {
	}

	public static BookmarkDatabaseManager getInstance() {
		return singleton;
	}

	/**
	 * Can be used to start a query that retrieves a list of bookmarks.
	 */
	@SuppressWarnings("unchecked")
	protected List<Bookmark> bookmarkList(final String query, final BookmarkParam param) {
		return (List<Bookmark>) queryForAnything(query, param, QueryFor.LIST);
	}

	// FIXME return value needs to be changed to org.bibsonomy.model.Post
	@SuppressWarnings("unchecked")
	protected List<Post<? extends Resource>> bookmarkList(final String query, final BookmarkParam param, final boolean test) {
		return (List<Post<? extends Resource>>) queryForAnything(query, param, QueryFor.LIST);
	}

	/**
	 * <em>/tag/EinTag</em>, <em>/viewable/EineGruppe/EinTag</em><br/><br/>
	 * 
	 * On the <em>/tag</em> page only public entries are shown (groupType must
	 * be set to public) which have all of the given tags attached. On the
	 * <em>/viewable/</em> page only posts are shown which are set viewable to
	 * the given group and which have all of the given tags attached. 
	 */
	public List<Post<? extends Resource>> getBookmarkByTagNames(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkByTagNames", param,true);
	}

	/**
	 * <em>/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all bookmarks for a given
	 * user name (requestedUser) and given tags.<br/>
	 * 
	 * Additionally the group to be shown can be restricted. The queries are
	 * built in a way, that not only public posts are retrieved, but also
	 * friends or private or other groups, depending upon if userName us allowed
	 * to see them.
	 */
	public List<Post<? extends Resource>> getBookmarkByTagNamesForUser(final BookmarkParam param) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param);
		return this.bookmarkList("getBookmarkByTagNamesForUser", param,true);
	}

	/**
	 * <em>/concept/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all bookmarks for a given
	 * user name (requestedUser) and given tags. The tags are interpreted as
	 * supertags and the queries are built in a way that they results reflect
	 * the semantics of
	 * http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199 p. 91,
	 * formular (4).<br/>
	 * 
	 * Additionally the group to be shown can be restricted. The queries are
	 * built in a way, that not only public posts are retrieved, but also
	 * friends or private or other groups, depending upon if userName us allowed
	 * to see them.
	 */
	public List<Post<? extends Resource>> getBookmarkByConceptForUser(final BookmarkParam param) {
		DatabaseUtils.setGroups(this.generalDb, param);
		return this.bookmarkList("getBookmarkByConceptForUser", param,true);
	}

	/**
	 * <em>/friends</em><br/><br/>
	 * 
	 * Prepares queries which show all posts of users which have currUser as
	 * their friend.
	 */
	public List<Post<? extends Resource>> getBookmarkByUserFriends(final BookmarkParam param) {
		// groupType must be set to friends
		param.setGroupType(ConstantID.GROUP_FRIENDS);
		return this.bookmarkList("getBookmarkByUserFriends", param,true);
	}

	/**
	 * This method prepares queries which retrieve all bookmarks for the home
	 * page of BibSonomy. These are typically the X last posted entries. Only
	 * public posts are shown.
	 */
	public List<Post<? extends Resource>> getBookmarkForHomepage(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkForHomepage", param,true);
	}

	/**
	 * This method prepares queries which retrieve all bookmarks for the
	 * <em>/popular</em> page of BibSonomy. The lists are retrieved from two
	 * separate temporary tables which are filled by an external script.
	 */
	public List<Post<? extends Resource>> getBookmarkPopular(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkPopular", param,true);
	}

	/**
	 * Prepares a query which retrieves all bookmarks which are represented by
	 * the given hash. Retrieves only public bookmarks!
	 */
	public List<Post<? extends Resource>> getBookmarkByHash(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkByHash", param,true);
	}

	/**
	 * Retrieves the number of bookmarks represented by the given hash.
	 */
	public Integer getBookmarkByHashCount(final BookmarkParam param) {
		return (Integer) this.queryForObject("getBookmarkByHashCount", param);
	}

	/**
	 * Prepares a query which retrieves the bookmark (which is represented by
	 * the given hash) for a given user. Since user name is given, full group
	 * checking is done, i.e. everbody who may see the bookmark will see it.
	 */
	public List<Post<? extends Resource>> getBookmarkByHashForUser(final BookmarkParam param) {
		DatabaseUtils.setGroups(this.generalDb, param);
		return this.bookmarkList("getBookmarkByHashForUser", param,true);
	}

	/**
	 * <em>/search/ein+lustiger+satz</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which match a fulltext search in the
	 * fulltext search table.<br/>
	 * 
	 * The search string, as given by the user will be mangled up in the method
	 * to do what the user expects (AND searching). Unfortunately this also
	 * destroys some other features (e.g. <em>phrase searching</em>).<br/>
	 * 
	 * If requestedUser is given, only (public) posts from the given user are
	 * searched. Otherwise all (public) posts are searched.
	 */
	public List<Post<? extends Resource>> getBookmarkSearch(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkSearch", param,true);
	}

	/**
	 * Returns the number of bookmarks for a given search.
	 */
	public Integer getBookmarkSearchCount(final BookmarkParam param) {
		return (Integer) this.queryForObject("getBookmarkSearchCount", param);
	}

	/**
	 * <em>/viewable/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which are set viewable to group.
	 */
	public List<Post<? extends Resource>> getBookmarkViewable(final BookmarkParam param) {
		return this.bookmarkList("getBookmarkViewable", param,true);
	}

	/**
	 * <em>/group/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries which show all bookmarks of all users belonging to the
	 * group. This is an aggregated view of all posts of the group members.<br/>
	 * Full viewable-for checking is done, i.e. everybody sees everything he is
	 * allowed to see.<br/>
	 * 
	 * See also
	 * http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199 page
	 * 92, formula (9) for formal semantics of this query.
	 */
	public List<Post<? extends Resource>> getBookmarkForGroup(final BookmarkParam param) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param);
		return this.bookmarkList("getBookmarkForGroup", param,true);
	}

	/**
	 * Returns the number of bookmarks belonging to the group.<br/><br/>
	 * 
	 * TODO: these are just approximations - users own private/friends bookmarks
	 * and friends bookmarks are not included (same for publications)
	 */
	public Integer getBookmarkForGroupCount(final BookmarkParam param) {
		DatabaseUtils.setGroups(this.generalDb, param);
		return (Integer) this.queryForObject("getBookmarkForGroupCount", param);
	}

	/**
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getBookmarkForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 */
	public List<Post<? extends Resource>> getBookmarkForGroupByTag(final BookmarkParam param) {
		DatabaseUtils.prepareGetPostForGroup(this.generalDb, param);
		return this.bookmarkList("getBookmarkForGroupByTag", param,true);
	}

	/**
	 * <em>/user/MaxMustermann</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all bookmarks for a given
	 * user name (requestedUserName). Additionally the group to be shown can be
	 * restricted. The queries are built in a way, that not only public posts
	 * are retrieved, but also friends or private or other groups, depending
	 * upon if userName is allowed to see them.
	 */
	public List<Post<? extends Resource>> getBookmarkForUser(final BookmarkParam param) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param);
		return this.bookmarkList("getBookmarkForUser", param, true);
	}

	/**
	 * Returns the number of bookmarks for a given user.
	 */
	public Integer getBookmarkForUserCount(final BookmarkParam param) {
		DatabaseUtils.prepareGetPostForUser(this.generalDb, param);
		return (Integer) this.queryForObject("getBookmarkForUserCount", param);
	}


	
	/**
	 * This methods  are for setting functions concerning bookmark entries
	 */
	  public void insertBookmark(final BookmarkParam bookmark) {
		this.insert("insertBookmark", bookmark);
	}

	public void insertBookmarkLog(final BookmarkParam bookmark) {
		// TODO not tested
		this.insert("insertBookmarkLog", bookmark);
	}
/*
 * insert counter, hash and url of bookmark
 */
	public void insertBookmarkInc(final Bookmark param) {
		this.insert("insertBookmarkInc", param);
	}

	public void updateBookmarkHashDec(final BookmarkParam param) {
		this.insert("updateBookmarkHashDec", param);
	}

	public void updateBookmarkLog(final BookmarkParam param) {
		// TODO not tested
		this.insert("updateBookmarkLog", param);
	}

	public void deleteBookmarkByContentId(final BookmarkParam param) {
		this.insert("deleteBookmarkByContentId", param);
	}
	/**
	 * Get a current ContentID for setting a bookmark update the current
	 * ContendID for bookmark and bibtex
	 */
	
	public Integer getCurrentContentIdFromIds(final BookmarkParam param) {
		return (Integer) this.queryForObject("getNewContentID", param);
	}

	public void updateIds(final BookmarkParam param) {
		this.insert("updateIds", param);
	}
	
	public Integer getCurrentTasIdFromIds(final BookmarkParam param) {
		return (Integer) this.queryForObject("getNewTasID", param);
	}

	public Integer getContentIDForBookmark(final BookmarkParam param) {
		return (Integer) this.queryForObject("getContentIDForBookmark", param);
	}

	public List<Post<? extends Resource>> getPosts(String authUser, GroupingEntity grouping, String groupingName, List<String> tags, String hash, boolean popular, boolean added, int start, int end, boolean continuous) {
        /*
         * For test options
         */
		// TODO fix this
//		List test_getBookmarksBoomarksForUser = getBoomarksForUser.perform("jaeschke", GroupingEntity.USER, "jaeschke",null,null,false, false, 0, 19);
//		System.out.println("test="+test_getBookmarksBoomarksForUser.size());
//		System.out.println("authUser = " + authUser);
//		System.out.println("grouping = " + grouping);
//		System.out.println("groupingName = " + groupingName);
//		System.out.println("tags = " + tags);
//		System.out.println("hash = " + hash);
//		System.out.println("start = " + start);
//		System.out.println("end = " + end);
//
//		List<Post<? extends Resource>> posts = getBookmarksForUser.perform(authUser, grouping, groupingName, tags, hash, popular, added, start, end);
//		System.out.println("BoookmarkDbManager posts.size= " + posts.size());
//		return posts;
		return null;
	}

	
	
	public Post<Resource> getPostDetails(String authUser, String resourceHash, String userName) {
		// TODO Auto-generated method stub
		return null;
	}



	public boolean deletePost(String userName, String resourceHash) {
		
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
        
		this.updateBookmarkHashDec(paramDelete);

		/***delete the selected bookmark (by given contentId) from current database table***/ 	
		
	    this.deleteBookmarkByContentId(paramDelete);
	    System.out.println("Lösche bookmark bei gegebener Content_Id");
	    
	    this.tagDb.deleteTas(paramDelete);
	    System.out.println("Lösche TAS wieder");
	    
		return true;
	}



	@SuppressWarnings("unchecked")
	public boolean storePost(String userName, Post post, boolean update) {
		/*
    	 * TODO  handling and proving valid post
    	 */
		BookmarkParam bookmarkParam =new BookmarkParam();
		Bookmark bookmark =new Bookmark();
	
		bookmarkParam.setUserName(userName);
		bookmarkParam.setHash(post.getResource().getIntraHash());
		bookmarkParam.setDescription(post.getDescription());
		bookmarkParam.setDate(post.getDate());
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
	    	
	    	/*
	    	 * create bookmark object from database with current 
	    	 * hash and user values (getBookmarkByHashForUser)
	    	 */
	    	List<Post<? extends Resource>> storeTemp = null; //FIXME this.getBookmarkByHashForUser.perform(userFromUrl,GroupingEntity.USER,userFromUrl,null,hashFromUrl,false, false, 0, 1);
        	
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
	            	 
   	                 bookmarkParam.setId(this.getCurrentContentIdFromIds(bookmarkParam));
   	                 System.out.println("bekomme aktuellen value/content_id aus ids table: "+ bookmarkParam.getId());
	            		
	            	 this.updateIds(bookmarkParam);
	            	 post.setContentId(this.getCurrentContentIdFromIds(bookmarkParam));
	            	 bookmarkParam.setRequestedContentId(post.getContentId());
	            	 System.out.println("bookmark content_id: " + bookmarkParam.getRequestedContentId());

	            	 bookmarkParam.setUrl(((Bookmark)post.getResource()).getUrl());
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
	          	        
	                   
	                this.insert("insertBookmark",bookmarkParam);
	                System.out.println("insert Bookmark into bookmark table");
                        
	     		   /*
     			    * increment URL counter and insert hash
     			    */
	                    
	     			  this.insert("insertBookmarkInc",bookmarkParam);
	     			  System.out.println("zähle URL counter hoch plus hash plus url insert");
     			  
                 
	     			  
	     			  
	     			 for(Tag tag: tagliste){
	     				 
	     				bookmarkParam.setId(this.getCurrentTasIdFromIds(bookmarkParam));
		          	   	System.out.println("currentTasID:" +bookmarkParam.getId());
	     				 
		          	    this.updateIds(bookmarkParam);
		          	    bookmarkParam.setNewTasId(this.getCurrentTasIdFromIds(bookmarkParam));
	     				 
	     				 
	     				bookmarkParam.setTagName(tag.getName());
	     				this.insert("insertTas",bookmarkParam);
	     				 
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
     	    	
        		List<Post<? extends Resource>> storeTempWithNewHash = null; // FIXME getBookmarksByHashForUser.perform(userFromUrl,GroupingEntity.USER,userFromUrl,null,newHash,false, false, 0, 1);
        		 
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
   	                 bookmarkParam.setId(this.getCurrentContentIdFromIds(bookmarkParam));
   	                 System.out.println("bekomme aktuellen value/content_id aus ids table: "+ bookmarkParam.getId());
	            		
	            	 this.updateIds(bookmarkParam);
	            	 
	            	 post.setContentId(this.getCurrentContentIdFromIds(bookmarkParam));
	            	 bookmarkParam.setRequestedContentId(post.getContentId());
	            	 bookmarkParam.setUrl(((Bookmark)post.getResource()).getUrl());
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
	          	        
	                   
	                    this.insert("insertBookmark",bookmarkParam);
	                    System.out.println("insert Bookmark into bookmark table");
                        
	     		   /*
     			    * increment URL counter and insert hash
     			    */
	                    
	     			  this.insert("insertBookmarkInc",bookmarkParam);
	     			  System.out.println("zähle URL counter hoch plus hash plus url insert");
	     			  
	     			 for(Tag tag: tagliste){
	     				 
	     				bookmarkParam.setId(this.getCurrentTasIdFromIds(bookmarkParam));
		          	   	System.out.println("currentTasID:" +bookmarkParam.getId());
	     				 
		          	    this.updateIds(bookmarkParam);
		          	    bookmarkParam.setNewTasId(this.getCurrentTasIdFromIds(bookmarkParam));
	     				 
	     				 
	     				bookmarkParam.setTagName(tag.getName());
	     				this.insert("insertTas",bookmarkParam);
	     				 
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
	    		List<Post<? extends Resource>> storeTemp = null; // FIXME getBookmarksByHashForUser.perform(userFromUrl,GroupingEntity.USER,userFromUrl,null,hashFromUrl,false, false, 0, 1);
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
	            	 
   	                 bookmarkParam.setId(this.getCurrentContentIdFromIds(bookmarkParam));
   	                 System.out.println("bookmarkParam currentContentIDFromIds"+ bookmarkParam.getId());
	            		
	            	 this.updateIds(bookmarkParam);
	            	 
	            	 post.setContentId(this.getCurrentContentIdFromIds(bookmarkParam));
	            	 bookmarkParam.setRequestedContentId(post.getContentId());

	            	 bookmarkParam.setUrl(((Bookmark)post.getResource()).getUrl());
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
	          	        
	                   
	                    this.insert("insertBookmark",bookmarkParam);
	                    System.out.println("insert Bookmark into bookmark table");
                        
	     		   /*
     			    * increment URL counter and insert hash
     			    */
	                    
	     			  this.insert("insertBookmarkInc",bookmarkParam);
	     			  System.out.println("zähle URL counter hoch plus hash plus url insert");
     			  
	     			 for(Tag tag: tagliste){
	     				 
	     				bookmarkParam.setId(this.getCurrentTasIdFromIds(bookmarkParam));
		          	   	System.out.println("currentTasID:" +bookmarkParam.getId());
	     				 
		          	    this.updateIds(bookmarkParam);
		          	    bookmarkParam.setNewTasId(this.getCurrentTasIdFromIds(bookmarkParam));
	     				 
	     				 
	     				bookmarkParam.setTagName(tag.getName());
	     				this.insert("insertTas",bookmarkParam);
	     				 
	     				System.out.println("Tags in BookmarkObject heißen: "+ bookmarkParam.getTagName());
	     				System.out.println("TAS eingefügt");
	     			}
	     			 
     			      
	    		
	    				} /*end else*/
	    		
	    	     } /*end if*/
	    	
	    	} /*end else*/
	    	
        return true;
	}
}