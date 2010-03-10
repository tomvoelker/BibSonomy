package org.bibsonomy.recommender.tags.multiplexer;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.TagRecommenderConnector;
import org.bibsonomy.recommender.tags.database.DBLogic;
import org.bibsonomy.recommender.tags.multiplexer.modifiers.PostModifier;
import org.bibsonomy.recommender.tags.multiplexer.modifiers.RecommendedTagModifier;
import org.bibsonomy.recommender.tags.multiplexer.strategy.RecommendationSelector;
import org.bibsonomy.recommender.tags.multiplexer.strategy.SelectAll;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * Class for querying several recommenders. 
 * Each recommendation request is sent to all registered recommenders. Thereby each 
 * query to an recommendation is identified by an unique transaction id which is used 
 * to manage asynchronous events. Responses of the recommenders are collected using 
 * the consumer/producer schema.  
 * The overall result is chosen in selectResult(). Request, all received responses 
 * as well as timing and individual recommender meta information is stored using 
 * class DBAccess.
 *   
 * @author fei
 */
public class MultiplexingTagRecommender implements TagRecommender {
	private static final Log log = LogFactory.getLog(MultiplexingTagRecommender.class);
	Object lockResults = new Object();              // while selecting a result,
	// no further recommender answers 
	// should be added to database
	private List<TagRecommender> localRecommenders;         // recommenders with object reference
	private List<TagRecommenderConnector> distRecommenders;    // recommenders with remote access

	/** timeout for querying distant recommender */
	private int queryTimeout = 100;                
	/** result selection strategy */
	private RecommendationSelector resultSelector;
	/** result selector's id (as stored in the db) */
	private long selectorID;

	private PostPrivacyFilter postPrivacyFilter;
	
	private List<PostModifier> postModifiers;
	
	/** before storing recommended tags, all these filters are applied */
	private List<RecommendedTagModifier> tagModifiers;

	/** indicates that post identifier was not given */
	public static int UNKNOWN_POSTID = -1;

	/** FIXME: reuse AbstractTagRecommender */
	private static final int DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND = 5;
	/**
	 * The maximal number of tags the recommender shall return on a call to
	 * {@link #getRecommendedTags(Post)}.
	 */
	private int numberOfTagsToRecommend = DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND;
	
	/** map recommender systems to their corresponding setting ids */
	private  ConcurrentHashMap<TagRecommender,Long> recommenderLookup;
	
	/** we speed up the multiplexer by caching query results */
	private RecommendedTagResultManager resultCache;
	
	/** flag indicating, whether an instance was correctly initialized */
	private boolean initialized = false;

	private DBLogic dbLogic;
	
	/** debug variable counting the number of open query threads */
	private static int queryThreadCounter = 0;
	/** debug variable counting the number of open feedback threads */
	private static int feedbackThreadCounter = 0;
	
	//------------------------------------------------------------------------
	// Instance initialization
	//------------------------------------------------------------------------
	/**
	 * constructor.
	 */
	public MultiplexingTagRecommender() {
		localRecommenders = new ArrayList<TagRecommender>();
		distRecommenders  = new ArrayList<TagRecommenderConnector>();
		resultSelector    = new SelectAll();
		postPrivacyFilter = new PostPrivacyFilter();
		resultCache       = new RecommendedTagResultManager();
		postModifiers     = new LinkedList<PostModifier>();
		tagModifiers      = new LinkedList<RecommendedTagModifier>();
	}
	
	
	
	/**
	 * post-instance init method: this method has to be called when all 
	 * necessary properties (e.g. dbLogic) are set
	 * 
	 * IMPORTANT: this init method has to be set in the spring bean definition
	 * <bean id="..." class="..." init-method="init"/>
	 * @throws SQLException 
	 */
	public void init() {
		//
		// 0. Initialize data structures 
		//
		recommenderLookup = new ConcurrentHashMap<TagRecommender, Long>();
		
		//
		// 1. Store all registered recommender systems in the db
		//    and add their setting ids to the recommender lookup table
		//
		for( TagRecommenderConnector con: getDistRecommenders() ) {
			// each recommender is identified by an unique id:
			registerRecommender(con, con.getId(), con.getInfo(), con.getMeta());
		}
		for( TagRecommender rec: getLocalRecommenders() ) {
			// each recommender is identified by an unique id
			registerRecommender(
					rec, 
					rec.getClass().getCanonicalName(), 
					rec.getInfo(), 
					null);
		};
		
		//
		// 2. Store the result selection strategy
		//
		registerResultSelector(getResultSelector());
		
		// all done.
		initialized = true;
	}
	

	//------------------------------------------------------------------------
	// Implementation of recommender registration
	//------------------------------------------------------------------------
	/**
	 * Adds recommender.
	 * @param recommender
	 * @return true on success, false otherwise
	 */
	public boolean addRecommender(TagRecommender recommender){
		log.info("adding local recommender: "+recommender.getInfo());
		getLocalRecommenders().add(recommender);
		if( initialized ) {
			registerRecommender(
					recommender,
					recommender.getClass().getCanonicalName(), 
					recommender.getInfo(), 
					null);
		}
		return true;
	}
	
	/**
	 * Adds distant recommender.
	 * @param recommender
	 * @return true on success, false otherwise
	 */
	public boolean addRecommenderConnector(TagRecommenderConnector recommender) {
		log.info("adding local recommender: "+recommender.getInfo());
		getDistRecommenders().add(recommender);
		if( initialized ) {
			registerRecommender(
					recommender,
					recommender.getClass().getCanonicalName(), 
					recommender.getInfo(), 
					null);
		}
		return true;
	}

	/** 
	 * @return false if none of the registered recommenders could be initialized
	 */
	public boolean connectRecommenders() {
		// connect to each recommender
		for(TagRecommenderConnector rec: getDistRecommenders()) {
			try {
				log.info("connecting to "+rec.getInfo());
				if( !rec.connect() );
			} catch( Exception e ) {
				// TODO remove rec from list
			}
		}
		return true;
	}
	/** 
	 * @return false if one of the registered recommenders could not be disconnected
	 */
	public boolean disconnectRecommenders() {
		// disconnect from each recommender
		for(TagRecommenderConnector rec: getDistRecommenders()) {
			try {
				log.info("disconnecting from "+rec.getInfo());
				if( !rec.disconnect() );
			} catch( Exception e ) {
				// TODO remove rec from list
			}
		}
		return true;
	}

	//------------------------------------------------------------------------
	// TagRecommender interface implementation
	//------------------------------------------------------------------------
	/**
	 * Extends TagRecommender's interface with a parameter which is used to map
	 * recommender queries to posts in BibSonomy:
	 *   When the postBookmark-Form is displayed, a random postID is generated and
	 *   passed to the recommender via a hidden field.
	 *   After storing the post, the postBookmarkController calls updateQuery()
	 *   with the corresponding username, date, postID and Hash.
	 *
	 * @param recommendedTags 
	 * @param post The post for which tag recommendations are requested.
	 * @param postID ID for mapping posts to recommender queries
	 */
	public void addRecommendedTags(
			Collection<RecommendedTag> recommendedTags, 
			Post<? extends Resource> post, 
			int postID) {

		log.debug("["+postID+"]querying["+localRecommenders+", "+distRecommenders+"]");

		// id identifying this query
		Long qid = null;

		// list for managing pending recommenders
		List<RecommenderDispatcher> dispatchers = new ArrayList<RecommenderDispatcher>();
		// query's time stamp
		Timestamp ts = new Timestamp(System.currentTimeMillis());

		try {
			// each set of queries is identified by an unique id:
			qid = dbLogic.addQuery(post.getUser().getName(), ts, post, postID, getQueryTimeout());

			// add query to cache
			resultCache.startQuery(qid);
			
			/*
			 * TODO: the filteredPost is null, when it is non public!
			 * Thus: check for null posts and ignore them for suggestion.
			 * (or: ignore the remote recommender, see below)
			 * 
			 * TODO: local recommender should get the unfiltered posts, such that
			 * we get some suggestions for sure.
			 * 
			 *  For the challenge, we just put all participants recommender into
			 *  the distRecommender list, such that they don't get private posts.
			 * 
			 */
			final Post<? extends Resource> filteredPost = postPrivacyFilter.filterPost(post);
			if (filteredPost != null) {
				// apply post modifiers
				for( PostModifier pm : getPostModifiers() )
					pm.alterPost(filteredPost);
				// query remote recommender
				for( TagRecommenderConnector con: getDistRecommenders() ) {
					// each recommender is identified by an unique id:
					Long sid = recommenderLookup.get(con);
					if( sid!=null ) {
						dbLogic.addRecommenderToQuery(qid, sid);
						//dbLogic.addRecommender(qid, con.getId(), con.getInfo(), con.getMeta());
						RecommenderDispatcher dispatcher = 
							new RecommenderDispatcher(con, filteredPost, qid, sid, null);
						dispatchers.add(dispatcher);
						dispatcher.start();
					} else {
						// TODO: this is only for initial debugging
						log.fatal("("+qid+")Didn't find recommender id - THIS SHOULD NEVER HAPPEN");
					}
				}
			}


			/*
			 * query local recommender
			 * 
			 * they get the unfiltered post, since we trust them
			 */
			for( TagRecommender rec: getLocalRecommenders() ) {
				// each recommender is identified by an unique id:
				Long sid = recommenderLookup.get(rec);
				if( sid!=null ) {
					dbLogic.addRecommenderToQuery(qid, sid);
					// dbLogic.addRecommender(qid, rec.getClass().getCanonicalName().toString(), rec.getInfo(), null);
					// query recommender
					// FIXME: local recommender are also aborted when timeout is reached,
					//        so their might be no recommendations at all
					RecommenderDispatcher dispatcher = 
						new RecommenderDispatcher(rec, post, qid, sid, null);
					dispatchers.add(dispatcher);
					dispatcher.start();
				} else {
					// TODO: this is only for initial debugging
					log.fatal("("+qid+")Didn't find recommender id - THIS SHOULD NEVER HAPPEN");
				}
			};

		} catch (SQLException ex) {
			log.error("("+qid+")"+ex.getMessage(), ex);
		}
		// wait for recommender systems' answers
		long startSleep = System.currentTimeMillis();
		try {
			Thread.sleep(getQueryTimeout()); 
		} catch (InterruptedException e) {
			log.debug("Sleep was interrupted");
		}
		// stop monitoring this query in the result cache
		resultCache.stopQuery(qid);

		// tell dispatchers that they are late
		for( RecommenderDispatcher disp: dispatchers ) {
			disp.abortQuery();
		};
		log.debug("("+qid+")Waited for "+(System.currentTimeMillis()-startSleep)+" ms");

		if( qid!=null ) {
			try {
				selectResult(qid, recommendedTags);
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				log.error("("+qid+")"+ex.getMessage(), ex);
			}
		};
		log.debug("("+qid+") Running threads: "+queryThreadCounter+" query threads and "+feedbackThreadCounter+" feedback threads");
	}

	/** Simply adds recommendations to the given collection of recommended tags. 
	 * 
	 * @see org.bibsonomy.services.recommender.TagRecommender#addRecommendedTags(java.util.Collection, org.bibsonomy.model.Post)
	 */	
	public void addRecommendedTags(Collection<RecommendedTag> recommendedTags, Post<? extends Resource> post) {
		addRecommendedTags(recommendedTags, post, UNKNOWN_POSTID);
	}

	public SortedSet<RecommendedTag> getRecommendedTags(Post<? extends Resource> post) {
		return getRecommendedTags(post, UNKNOWN_POSTID);
	};

	/**
	 * Extends TagRecommender's interface with a parameter which is used to map
	 * recommender queries to posts in BibSonomy:
	 *   When the postBookmark-Form is displayed, a random postID is generated and
	 *   passed to the recommender via a hidden field.
	 *   After storing the post, the postBookmarkController calls updateQuery()
	 *   with the corresponding username, date, postID and Hash.
	 *   
	 *   FIXME: is this method still needed? The post ID should be set inside 
	 *   the post (using contentID) ...
	 *   
	 * @param post The post for which tag recommendations are requested.
	 * @param postID ID for mapping posts to recommender queries
	 * @return Set of recommended Tags.
	 */
	public SortedSet<RecommendedTag> getRecommendedTags(Post<? extends Resource> post, int postID) {
		final SortedSet<RecommendedTag> recommendedTags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		addRecommendedTags(recommendedTags, post, postID);
		return recommendedTags;
	}

	public String getInfo() {
		return "Multiplexing recommender for querying several independent recommenders.";
	}

	//------------------------------------------------------------------------
	// Implementation of distributed recommendation evaluation
	//------------------------------------------------------------------------
	/**
	 * After querying all recommenders, the final result is composed here.
	 * @throws SQLException
	 */
	private void selectResult(Long qid, Collection<RecommendedTag> recommendedTags) throws SQLException {
		log.debug("("+qid+")starting result selection");
		
		// select result
		resultSelector.selectResult(qid, resultCache, recommendedTags);
		dbLogic.storeRecommendation(qid, selectorID, recommendedTags);

		// trim number of recommended tags if it exceeds numberOfTagsToRecommend
		if( recommendedTags.size()>getNumberOfTagsToRecommend() ) {
			Iterator<RecommendedTag> itr = recommendedTags.iterator();
			int pos = 0;
			while(itr.hasNext()) {
				itr.next(); 
				pos++;
				if( pos>getNumberOfTagsToRecommend() )
					itr.remove();
			};
		}
		
		// remove query from result cache
		resultCache.releaseQuery(qid);
		log.debug("("+qid+")Released query from result cache ("+resultCache.getNrOfCachedQueries()+" remaining).");
	}
	
	/**
	 * Publish individual (asynchronous) recommender's response.
	 * 
	 * @param id unique id identifying recommender (e.g. it's hashCode)
	 * @param queryTime time from call to recommender's response 
	 * @param tags recommender's result. If null, recommender timed out. 
	 * @return true on success, false otherwise
	 * @throws SQLException 
	 */
	private boolean addQueryResponse(
			Long qid, Long sid, long queryTime,
			SortedSet<RecommendedTag> tags) throws SQLException {
		// filter out invalid recommendations
		for( RecommendedTagModifier filter : getTagModifiers() )
			filter.alterTags(tags);
		
		// put result to resultCache (if query is still active)
		if( queryTime<=getQueryTimeout() )
			resultCache.addResult(qid, sid, tags);
		
		// store result in the database
		dbLogic.addRecommendation(qid,sid,tags,queryTime);
			
		return true;
	}

	public void setDistRecommenders(List<TagRecommenderConnector> distRecommenders) {
		if (getDistRecommenders()!=null) 
			disconnectRecommenders();
		this.distRecommenders = distRecommenders;
		if( initialized )
			init();
		connectRecommenders();
	}
	
	public List<TagRecommenderConnector> getDistRecommenders() {
		return distRecommenders;
	}

	public void setLocalRecommenders(List<TagRecommender> localRecommenders) {
		this.localRecommenders = localRecommenders;
		if (initialized) {
			init();
		}	
	}
	
	public List<TagRecommender> getLocalRecommenders() {
		return localRecommenders;
	}

	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}
	
	public int getQueryTimeout() {
		return queryTimeout;
	}

	public void setResultSelector(RecommendationSelector resultSelector) {
		this.resultSelector = resultSelector;
		if( initialized )
			this.registerResultSelector(resultSelector);
	}
	
	public RecommendationSelector getResultSelector() {
		return resultSelector;
	}
	
	/**
	 * Get id which indicates that a recommender was not associated with a post.
	 * @return UNKNOWN_POSTID
	 */
	public static int getUnknownPID() {
		return UNKNOWN_POSTID;
	}
	
	public void setNumberOfTagsToRecommend(int numberOfTagsToRecommend) {
		this.numberOfTagsToRecommend = numberOfTagsToRecommend;
	}
	
	public int getNumberOfTagsToRecommend() {
		return numberOfTagsToRecommend;
	}
	
	//------------------------------------------------------------------------
	// Implementation of dispatching recommendation queries
	//------------------------------------------------------------------------
	public static synchronized void incQueryCounter() {
		MultiplexingTagRecommender.queryThreadCounter++;
	}
	
	public static synchronized void decQueryCounter() {
		MultiplexingTagRecommender.queryThreadCounter--;
	}
	
	public static synchronized void incFeedbackCounter() {
		MultiplexingTagRecommender.feedbackThreadCounter++;
	}
	
	public static synchronized void decFeedbackCounter() {
		MultiplexingTagRecommender.feedbackThreadCounter--;
	}
	
	/**
	 * Threaded class for dispatching and collecting a single recommender query.
	 * 
	 * @author fei
	 */
	public class RecommenderDispatcher extends Thread {
		/** unique id identifying set of queries */
		private Long qid;                        
		/** unique id identifying recommender */
		private Long sid;
		/** recommender specific meta information */
//		private byte[] recMeta; TODO: remove field
		private TagRecommender recommender;
		private boolean abort = false;
		Post<? extends Resource> post;
		SortedSet<RecommendedTag> recommendedTags;

		/**
		 * Constructor for creating a query dispatcher.
		 * @param recommender Recommender whos query should be dispatched
		 * @param post user's post to query the recommender for
		 * @param qid unique id identifying set of queries
		 * @param sid 
		 * @param recommendedTags previously recommended tags
		 */
		public RecommenderDispatcher(TagRecommender recommender,
				Post<? extends Resource> post,
				Long qid, Long sid,
				SortedSet<RecommendedTag> recommendedTags ) {
			this.recommender = recommender;
			this.post = post;
			this.qid = qid;
			this.sid = sid;
			this.recommendedTags = recommendedTags;
			
			MultiplexingTagRecommender.incQueryCounter();
		}
		
		

		/**
		 * Get managed recommender's info.
		 * @return recommender's info text
		 */
		public String getInfo() {
			// just return recommenders info
			return recommender.getInfo();
		}

		/**
		 * Dispatch and collect query.
		 */
		@Override
		public void run() {
			// for query-time logging
			long time = System.currentTimeMillis();
			SortedSet<RecommendedTag> preset = null;
			// actually query the recommender
			try {
				if( (recommendedTags!=null) && (recommendedTags.size()>0) ) {
					preset = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
					preset.addAll(recommendedTags);
					recommender.addRecommendedTags(recommendedTags, post);
				} else
					recommendedTags = recommender.getRecommendedTags(post);
				// calculate query-time
			} catch( Exception e ) {
				log.error("("+qid+")Error querying recommender " + recommender.getInfo(), e);
			}
			time = System.currentTimeMillis()-time;
			// add query result
			try {
				addQueryResponse(qid, sid, time, recommendedTags);
			} catch (SQLException ex) {
				log.error("("+qid+")Error storing recommender query response.", ex);
			}
			if( !abort ) {
				log.info("("+qid+")run finished in time " + time);
			} else {
				log.info("("+qid+")Recommender " + recommender.getInfo() + " timed out (" + time + ")");
			}
			
			MultiplexingTagRecommender.decQueryCounter();
		}
		/**
		 * Tell dispatcher that he timed out.
		 */
		public void abortQuery() {
			abort = true;
		}
	}
	/**
	 * Threaded class for dispatching and collecting a single recommender query.
	 * 
	 * @author fei
	 */
	public class FeedbackDispatcher extends Thread {
		private TagRecommender recommender;
		private boolean abort = false;
		Post<? extends Resource> post;

		/**
		 * Constructor for creating a query dispatcher.
		 * @param recommender Recommender whos query should be dispatched
		 * @param post user's post to query the recommender for
		 */
		public FeedbackDispatcher(TagRecommender recommender, Post<? extends Resource> post) {
			this.recommender = recommender;
			this.post = post;
			MultiplexingTagRecommender.incFeedbackCounter();
		}

		/**
		 * Get managed recommender's info.
		 * @return recommender's info text
		 */
		public String getInfo() {
			// just return recommenders info
			return recommender.getInfo();
		}

		/**
		 * Dispatch and collect query.
		 */
		public void run() {
			// for query-time logging
			long time = System.currentTimeMillis();
			SortedSet<RecommendedTag> preset = null;
			// actually query the recommender
			try {
				recommender.setFeedback(this.post);
			} catch( Exception e ) {
				log.error("Error setting feedback for recommender " + recommender.getInfo(), e);
			}
			time = System.currentTimeMillis()-time;
			if( !abort ) {
				log.info("run finished in time " + time);
			} else {
				log.info("Setting feedback for recommender " + recommender.getInfo() + " timed out (" + time + ")");
			}
			MultiplexingTagRecommender.decFeedbackCounter();
		}

		/**
		 * Tell dispatcher that he timed out.
		 */
		public void abortQuery() {
			abort = true;
		}
	}
	
	
	public DBLogic getDbLogic() {
		return this.dbLogic;
	}
	public void setDbLogic(DBLogic dbLogic) {
		this.dbLogic = dbLogic;
	}
	@Override
	public void setFeedback(Post<? extends Resource> post) {
		try {
			dbLogic.connectWithPost(post, post.getContentId());
		} catch (SQLException e) {
			throw new RuntimeException("Could not connect post: " + e);
		}
		
		
		/*
		 * TODO: the filteredPost is null, when it is non public!
		 * Thus: check for null posts and ignore them for suggestion.
		 * (or: ignore the remote recommenders, see below)
		 * 
		 * TODO: local recommenders should get the unfiltered posts, such that
		 * we get some suggestions for sure.
		 * 
		 *  For the challenge, we just put all participants recommenders into
		 *  the distRecommender list, such that they don't get private posts.
		 * 
		 */
		// list for managing pending recommenders
		List<FeedbackDispatcher> dispatchers = new ArrayList<FeedbackDispatcher>();
		final Post<? extends Resource> filteredPost = postPrivacyFilter.filterPost(post);
		if (filteredPost != null) {
			// apply post modifiers
			for( PostModifier pm : getPostModifiers() )
				pm.alterPost(filteredPost);
			// send feedback to remote recommenders
			for( TagRecommenderConnector con: getDistRecommenders() ) {
				FeedbackDispatcher dispatcher = 
					new FeedbackDispatcher(con, post);
				dispatchers.add(dispatcher);
				dispatcher.start();
			}
		}


		/*
		 * set feedback for local recommenders
		 * 
		 * they get the unfiltered post, since we trust them
		 */
		for( TagRecommender rec: getLocalRecommenders() ) {
			// query recommender
			// FIXME: local recommenders are also aborded when timout is reached,
			//        so their might be now recommendations at all
			FeedbackDispatcher dispatcher = 
				new FeedbackDispatcher(rec, post);
			dispatchers.add(dispatcher);
			dispatcher.start();
		};
	}
	
	public void setPostModifiers(List<PostModifier> postModifiers) {
		this.postModifiers = postModifiers;
	}
	
	public List<PostModifier> getPostModifiers() {
		return postModifiers;
	}
	
	//------------------------------------------------------------------------
	// Private helper functions
	//------------------------------------------------------------------------	
	/**
	 * register recommender system in all relevant data structures
	 * 
	 * @param reco the tag recommender
	 * @param id it's id
	 * @param descr a short description
	 * @param meta meta information
	 */
	private void registerRecommender(TagRecommender reco, String id, String descr, byte[] meta) {
		Long sid=null;
		try {
			sid = dbLogic.insertRecommenderSetting(id, descr, meta);
		} catch (SQLException ex) {
			log.fatal("Couldn't store recommender setting.", ex);
		}
		if( sid!=null )
			recommenderLookup.put(reco, sid);
	}

	/**
	 * register result selection strategy
	 * 
	 * @param selector the selection strategy
	 */
	void registerResultSelector(RecommendationSelector selector) {
		try {
			selectorID = dbLogic.insertSelectorSetting(
					resultSelector.getInfo(), 
					resultSelector.getMeta()
			);
		} catch (SQLException ex) {
			log.fatal("Could not store result selection strategy", ex);
		}
	}
	
	public void setTagModifiers(List<RecommendedTagModifier> tagModifiers) {
		this.tagModifiers = tagModifiers;
	}
	
	public List<RecommendedTagModifier> getTagModifiers() {
		return tagModifiers;
	}
	
	/**
	 * destructor
	 */
	@Override
	protected void finalize() {
		disconnectRecommenders();
	}
}
