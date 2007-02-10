package org.bibsonomy.database.managers;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.database.AbstractDatabaseManager;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.util.DatabaseUtils;
import org.bibsonomy.util.ExceptionUtils;
import org.bibsonomy.model.BibTex;

/**
 * Used to retrieve BibTexs from the database.
 * 
 * @author Christian Schenk
 * @author mgr
 */
public class BibTexDatabaseManager extends AbstractDatabaseManager {

	private static final int MAX_WAIT_TIMEOUT = 60; // in seconds
	private static Random generator = new Random(); // FIXME
	private final DatabaseManager db;

	/**
	 * Reduce visibility so only the {@link DatabaseManager} can instantiate
	 * this class.
	 */
	BibTexDatabaseManager(final DatabaseManager db) {
		this.db = db;
	}

	/**
	 * <em>/bibtex/023847123ffa8976a969786f876f78e68</em><br/><br/>
	 * 
	 * Prepares a query which retrieves all publications whose hash
	 * requestedSimHash is equal to a given hash. Only public posts are
	 * retrieved.
	 */
	public List<BibTex> getBibTexByHash(final BibTexParam param) {
		/********TODO write some Expectation values for all methods**********/
		return this.bibtexList("getBibTexByHash", param);
	}

	/**
	 * Returns the number of publications for a given hash.
	 */
	public Integer getBibTexByHashCount(final BibTexParam param) {
		return (Integer) this.queryForObject("getBibTexByHashCount", param);
	}

	/**
	 * <em>/tag/EinTag</em>, <em>/viewable/EineGruppe/EinTag</em><br/><br/>
	 * 
	 * On the <em>/tag</em> page only public entries are shown (groupType must
	 * be set to public) which have all of the given tags attached. On the
	 * <em>/viewable/</em> page only posts are shown which are set viewable to
	 * the given group and which have all of the given tags attached.
	 */
	public List<BibTex> getBibTexByTagNames(final BibTexParam param) {
		return this.bibtexList("getBibTexByTagNames", param);
	}

	/**
	 * <em>/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all publications for a given
	 * user name (requestedUser) and given tags.<br/>
	 * 
	 * Additionally the group to be shown can be restricted. The queries are
	 * built in a way, that not only public posts are retrieved, but also
	 * friends or private or other groups, depending upon if currUser us allowed
	 * to see them.
	 */
	public List<BibTex> getBibTexByTagNamesForUser(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForUser(this.db, param);
		return this.bibtexList("getBibTexByTagNamesForUser", param);
	}

	/**
	 * <em>/concept/user/MaxMustermann/EinTag</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all publications for a given
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
	public List<BibTex> getBibTexByConceptForUser(final BibTexParam param) {
		DatabaseUtils.setGroups(this.db, param);
		return this.bibtexList("getBibTexByConceptForUser", param);
	}

	/**
	 * <em>/friends</em><br/><br/>
	 * 
	 * Prepares queries which show all posts of users which have userName as
	 * their friend.
	 */
	public List<BibTex> getBibTexByUserFriends(final BibTexParam param) {
		// groupType must be set to friends
		param.setGroupType(ConstantID.GROUP_FRIENDS);
		return this.bibtexList("getBibTexByUserFriends", param);
	}

	/**
	 * This method prepares a query which retrieves all publications the user
	 * has in his download list. The result is shown on the page
	 * <em>/download</em>. Since every user can only see his <em>own</em>
	 * download page, we use userName as restriction for the user name and not
	 * requestedUserName.
	 */
	public List<BibTex> getBibTexByDownload(final BibTexParam param) {
		return this.bibtexList("getBibTexByDownload", param);
	}

	/**
	 * This method prepares queries which retrieve all publications for the home
	 * page of BibSonomy. These are typically the X last posted entries. Only
	 * public posts are shown.
	 */
	public List<BibTex> getBibTexForHomePage(final BibTexParam param) {
		param.setGroupType(ConstantID.GROUP_FRIENDS);
		return this.bibtexList("getBibTexForHomePage", param);
	}

	/**
	 * This method prepares queries which retrieve all publications for the
	 * <em>/popular</em> page of BibSonomy. The lists are retrieved from two
	 * separate temporary tables which are filled by an external script.
	 */
	public List<BibTex> getBibTexPopular(final BibTexParam param) {
		return this.bibtexList("getBibTexPopular", param);
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
	public List<BibTex> getBibTexSearch(final BibTexParam param) {
		return this.bibtexList("getBibTexSearch", param);
	}

	/**
	 * Returns the number of publications for a given search.
	 */
	public Integer getBibTexSearchCount(final BibTexParam param) {
		return (Integer) this.queryForObject("getBibTexSearchCount", param);
	}

	/**
	 * <em>/viewable/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries to retrieve posts which are set viewable to group.
	 */
	public List<BibTex> getBibTexViewable(final BibTexParam param) {
		return this.bibtexList("getBibTexViewable", param);
	}

	/**
	 * Prepares a query which returns all duplicate BibTex posts of the
	 * requested user. Duplicates are BibTex posts which have the same simhash1,
	 * but a different simhash0 (the latter is always true within the posts of a
	 * single user).
	 */
	public List<BibTex> getBibTexDuplicate(final BibTexParam param) {
		DatabaseUtils.setGroups(this.db, param);
		return this.bibtexList("getBibTexDuplicate", param);
	}

	/**
	 * Returns the number of duplicates (i.e. BibTex posts) of a given user.
	 */
	public Integer getBibTexDuplicateCount(final BibTexParam param) {
		return (Integer) this.queryForObject("getBibTexDuplicateCount", param);
	}

	/**
	 * <em>/group/EineGruppe</em><br/><br/>
	 * 
	 * Prepares queries which show all publications of all users belonging to
	 * the group. This is an aggregated view of all posts of the group members.<br/>
	 * Full viewable-for checking is done, i.e. everybody sees everything he is
	 * allowed to see.<br/>
	 * 
	 * See also
	 * http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199 page
	 * 92, formula (9) for formal semantics of this query.
	 */
	public List<BibTex> getBibTexForGroup(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForGroup(this.db, param);
		return this.bibtexList("getBibTexForGroup", param);
	}

	/**
	 * Returns the number of publications belonging to the group.<br/><br/>
	 * 
	 * TODO: these are just approximations - users own private/friends bookmarks
	 * and friends bookmarks are not included (same for publications)
	 */
	public Integer getBibTexForGroupCount(final BibTexParam param) {
		DatabaseUtils.setGroups(this.db, param);
		return (Integer) this.queryForObject("getBibTexForGroupCount", param);
	}

	/**
	 * <em>/group/EineGruppe/EinTag+NochEinTag</em><br/><br/>
	 * 
	 * Does basically the same as getBibTexForGroup with the additionaly
	 * possibility to restrict the tags the posts have to have.
	 */
	public List<BibTex> getBibTexForGroupByTag(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForGroup(this.db, param);
		return this.bibtexList("getBibTexForGroupByTag", param);
	}

	/**
	 * <em>/user/MaxMustermann</em><br/><br/>
	 * 
	 * This method prepares queries which retrieve all publications for a given
	 * user name (requestedUserName). Additionally the group to be shown can be
	 * restricted. The queries are built in a way, that not only public posts
	 * are retrieved, but also friends or private or other groups, depending
	 * upon if userName is allowed to see them.
	 */
	public List<BibTex> getBibTexForUser(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForUser(this.db, param);
		return this.bibtexList("getBibTexForUser", param);
	}

	/**
	 * Returns the number of publications for a given user.
	 */
	public Integer getBibTexForUserCount(final BibTexParam param) {
		DatabaseUtils.prepareGetPostForUser(this.db, param);
		return (Integer) this.queryForObject("getBibTexForUserCount", param);
	}

	/**
	 * <em>/bibtex/023847123ffa8976a969786f876f78e68/MaxMustermann</em><br/><br/>
	 * 
	 * Prepares a query which retrieves all bibtex posts whose hash no. requSim
	 * is equal to requBibtex and they're owned by requUser. Full group checking
	 * is done.<br/>
	 * 
	 * Additionally, if requUser = currUser, the document table is joined so
	 * that we can present the user a link to the uploaded document.
	 */
	public List<BibTex> getBibTexByHashForUser(final BibTexParam param) {
		DatabaseUtils.setGroups(this.db, param);
		return this.bibtexList("getBibTexByHashForUser", param);
	}

	/********get a content_id by a given user and a given hash***********/
	public Integer getContentIdByUserAndHash(final BibTex bibtex){
		return (Integer)this.queryForObject("getContentIdByUserAndHash", bibtex);
	}

	/**********modify update to select, return is list of String**************/
	public String getBibTexSimHashsByContentId(final BibTex param) {
		// TODO not tested
		return (String)this.queryForObject("getBibTexSimHashsByContentId", param);
	}

	/**
	 * Inserts a publication into the database.
	 */
	public void insertBibTex(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTex", param);
	}

	public void insertBibTexLog(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTexLog", param);
	}

	/**
	 * Inserts a BibTex-hash into the database.
	 */
	public void insertBibTexHash(final BibTexParam param) {
		// TODO not tested
		if (param.getHash() == null || param.getHash().equals("")) {
			throw new RuntimeException("Hash must be set");
		}
		this.insert("insertBibTexHash", param);
	}

	public void insertBibTexHash1Inc(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTexHashInc", param);
	}
	
	public void insertBibTexHash2Inc(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTexHash2Inc", param);
	}
	
	public void insertBibTexHash3Inc(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTexHash3Inc", param);
	}
	
	public void insertBibTexHash4Inc(final BibTex param) {
		// TODO not tested
		this.insert("insertBibTexHash4Inc", param);
	}

	public void updateBibTexHash1Dec(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexHashDec", param);
	}

	public void updateBibTexHash2Dec(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexHash2Dec", param);
	}

	public void updateBibTexHash3Dec(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexHash3Dec", param);
	}

	public void updateBibTexHash4Dec(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexHash4Dec", param);
	}

	public void updateBibTexLog(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexLog", param);
	}

	public void updateBibTexDocument(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexDocument", param);
	}

	public void updateBibTexCollected(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexCollected", param);
	}

	public void updateBibTexExtended(final BibTex param) {
		// TODO not tested
		this.update("updateBibTexExtended", param);
	}

	public void updateBibTexUrl(final BibTexParam param) {
		// TODO not tested
		this.update("updateBibTexUrl", param);
	}

	public void deleteBibTexByContentId(final BibTex param) {
		// TODO not tested
		this.update("deleteBibTexByContentId", param);
	}

	public void deleteBibTexDocumentByContentId(final BibTexParam param) {
		// TODO not tested
		this.update("deleteBibTexDocumentByContentId", param);
	}

	public void deleteBibTexCollectedByContentId(final BibTexParam param) {
		// TODO not tested
		this.update("deleteBibTexCollectedByContentId", param);
	}

	public void deleteBibTexExtendedByContentId(final BibTexParam param) {
		// TODO not tested
		this.update("deleteBibTexExtendedByContentId", param);
	}

	public void deleteBibTexUrlByContentId(final BibTexParam param) {
		// TODO not tested
		this.update("deleteBibTexUrlByContentId", param);
	}

	public int updateBibtex(GenericParam<BibTex> param, GenericParam<BibTex> duplicateParam, String currUser, boolean overwrite, String oldhash) {
	    /*****TODO  Parameter should be reduce****************/
		boolean isToDeleted = false;
  	    boolean setToDeleted = false;
  	    boolean isToInserted = false;
  	    boolean setToInserted = false;
		
		boolean spammer = this.db.getGeneral().isSpammer(param);
		/******** counter for succesfull bibtex inserts*************/
		int bibSuccessCounter = 0; 
		int oldcontentid =ConstantID.IDS_UNDEFINED_CONTENT_ID.getId();
		boolean success;

		/* iterate over all complete bibtex objects */
		for (BibTex bibtex: param.getResources()) {
//			param.setResource(bibtex); FIXME !!!
			int wait = 1;
			success = false;
			
			while (wait < MAX_WAIT_TIMEOUT && !success) {
				
				try {
					/* TODO:
					 * rja, 2006-01-16, I changed user name from currUser to bib.getUser(), because
					 * otherwise group-copy does not work. On the other hand this means, that I can
					 * overwrite existing articles in the group (under which circumstances??)
					 */
					/* *************************************
					 * duplicate checks
					 * *************************************/
					
					/************give me a bibtex to given hash and user name**************/
					int contentId=getContentIdByUserAndHash(bibtex); // FIXME
					if (contentId == ConstantID.IDS_UNDEFINED_CONTENT_ID.getId()) {
						
						 /*******the bibtex entry does NOT exist for that user ---> set toIns**/
						 
						setToInserted=true;
						
						// this is for doing a "move" operation, which is only done, if target does not exist
						// check, if old hash is available and if we treat bibtex of currUser
						
						/*************not an optimal solution***************/
						if (!"".equals(oldhash) && currUser.equals(bibtex.getUserName())) {
							bibtex.setContentId(contentId);
							setToDeleted=true;
						}

					} else { 
						/*
						 * the bibtex entry EXISTS for that user
						 */
						if (overwrite) {
							// overwrite it --> set content id for delete
							bibtex.setContentId(contentId);
							setToDeleted=true;
							setToInserted=true;
						} else {
							/* put duplicates into warning list */
							/*******duplicates should be a list of bibtex objects******/
							if (duplicateParam != null) ((List<BibTex>) duplicateParam).add(bibtex);
							/* "remove" bibtex entry so that it is not inserted */
							setToDeleted=false;
							setToInserted=false;
						}
					}
					
					if (isToDeleted) {
						/* *************************************
						 * DELETE
						 * *************************************/
						oldcontentid = bibtex.getContentId();
						/**********get simhashes by contentId**************/
						String hashes=getBibTexSimHashsByContentId(bibtex);
						/*******? bibtex.getHash();*******/
						if(hashes==null){
							/*
							 * TODO: this is not good, since we should immediately proceed to the user with an error message!
							 * The error message doesn't help, too, since the user gets back to the broken entry and tries to
							 * re-enter it again (which will not work, since oldhash just does not exists any longer)
							 * Solution would be: 
							 * - find the entry, the user wants to edit (difficult)
							 * - send him to the home page, together with an error message
							 * - something else
							 */
//							throw new BibtexException("Entry not found in table!");
							ExceptionUtils.logErrorAndThrowRuntimeException(log, null, "Entry not found in table!");
						}

						/*** log Bibtex*****/
						insertBibTexLog(bibtex);
						/** decrement hash counter with all different sinhashes **/
						/********not the optimal solution***************/
						updateBibTexHash1Dec(bibtex);
						updateBibTexHash2Dec(bibtex);
						updateBibTexHash3Dec(bibtex);
						updateBibTexHash4Dec(bibtex);
						
						/***** delete tags for this item*********/
						 this.db.getTag().deleteTags(param); 
						 
						// TODO delete all to the content related questions
						/**UpdateQuestion.update(oldcontentid);*********/
						/******* delete bibtex **********/
						 deleteBibTexByContentId(bibtex);
					} /*** delete***********/
					if (isToInserted) {
						/* *************************************
						 * INSERT
						 * *************************************/
						
						/*** create unique content_id ***/
						bibtex.setContentId(param.getNewContentId());
						
					//	bibtex.setSpammer(spammer);
						
						/* insert into bibtex table */
					    insertBibIntoDB(param,spammer);
						
						// insert tags, tas, tagtag, tagtagrelations into database
						this.db.getTag().insertTags(param);
						
						/*******relationman.insertRelations(bib.getTag(), bib.getUser());*****************/
						
						/****count successful inserted bibtex entries and add them to a list**************/
						bibSuccessCounter++;
															
						/** update documents and collector table, if item has been moved **/
						if (isToDeleted) {

							/** save content_id to log_bibtex **/
							updateBibTexLog(bibtex);
							
							/*** Update content_id to linked document, if id has changed****/
							updateBibTexDocument(bibtex);
							/****** Update content_id in collector table, if id has changed****/
							updateBibTexCollected(bibtex);
							/******* update content_id in extended_fields table, if id has changed****/
							updateBibTexExtended(bibtex);
						}

						
					} /***********insert *************/
					/*****TODO***stop transaction***********/
					success = true;
					
				} catch (SQLException e) {
					wait = wait * 2;
					log.fatal("Could not insert bibtex objects, will wait at most " + wait + " seconds. Error was: " + e);
					try {
						Thread.sleep(generator.nextInt(wait));
					} catch (InterruptedException i) {
					}
				} // catch SQLException (wait ...)
				
			} // while loop wait
			if (!success && wait >= MAX_WAIT_TIMEOUT) {
				log.fatal("Could not insert bibtex objects, waiting too long! ");
//				throw new SQLException("retry/wait timeout");
						}
		
		}
		return bibSuccessCounter;
		}
	
	
	private void insertBibIntoDB(GenericParam<BibTex> param, boolean spammer) throws SQLException {
		
		
		  /**take care of spammers******/
		 
		
		BibTex bibtex= null; //param.getResource(); FIXME !!!
		param.setGroupId(ResourceUtils.getGroupId(param.getGroupId(),true));
		/****insert a bibtex object***************/
		insertBibTex(bibtex);
		/*********counter(increments) for different simhash(0-3)****************/
		insertBibTexHash1Inc(bibtex);
		insertBibTexHash2Inc(bibtex);
		insertBibTexHash3Inc(bibtex);
		insertBibTexHash4Inc(bibtex);

	}
	
	/******TODO delete bibtex entries*******/
	/********public void deleteBibtex(String currUser, GenericParam<BibTex> param) throws SQLException {
		BibTex bibtex;
		param.setResource(bibtex);
		int oldcontentid=this.db.getBibTex().getContentIdByUserAndHash(bibtex);
		// get content_id and check, if content_id exists
		if (oldcontentid != Bibtex.UNDEFINED_CONTENT_ID) {
			
			
			// log Bibtex
			insertBibTexLog(bibtex);
			
			// delete tags for this item
			deleteTags(param);
			
			// delete all related questions
			UpdateQuestion.update(conn, oldcontentid);
			
			// get hashes
			
			stmtP_select_hashes.setInt (1, oldcontentid);
			rst = stmtP_select_hashes.executeQuery();
			
			if (!rst.next()) {
				throw new SQLException ("could not find hash in bibtex table");
			}
			
			/* decrement hash counter */
		/*	updateBibTexHash1Dec(bibtex);
			updateBibTexHash2Dec(bibtex);
			updateBibTexHash3Dec(bibtex);
			updateBibTexHash4Dec(bibtex);
			
			
			
			// delete bibtex
			stmtP_delete_bibtex.setInt(1, oldcontentid);
			stmtP_delete_bibtex.executeUpdate();
			
			// delete link to related document
			stmtP_delete_doc.setInt(1, oldcontentid);
			stmtP_delete_doc.executeUpdate();
			
			// delete id in collector table
			stmtP_delete_collected.setInt(1, oldcontentid);
			stmtP_delete_collected.executeUpdate();
			
			// delete id in collector table
			stmtP_delete_extended.setInt(1, oldcontentid);
			stmtP_delete_extended.executeUpdate();*/
			
        /*******commit transaction************/			
	//	}
//}	
}