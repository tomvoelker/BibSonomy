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

import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.TagRecommenderConnector;
import org.bibsonomy.recommender.tags.database.DBLogic;
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
	private static final Logger log = Logger.getLogger(MultiplexingTagRecommender.class);
	Object lockResults = new Object();              // while selecting a result,
	// no further recommender answers 
	// should be added to database
	private List<TagRecommender> localRecommenders;         // recommenders with object reference
	private List<TagRecommenderConnector> distRecommenders;    // recommenders with remote access

	private int queryTimeout = 100;                // timeout for querying distant recommenders
	private RecommendationSelector resultSelector;


	private PostPrivacyFilter postPrivacyFilter;
	
	private List<PostModifier> postModifiers;

	/** indicates that post identifier was not given */
	public static int UNKNOWN_POSTID = -1;

	/** FIXME: reuse AbstractTagRecommender */
	private static final int DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND = 5;
	/**
	 * The maximal number of tags the recommender shall return on a call to
	 * {@link #getRecommendedTags(Post)}.
	 */
	private int numberOfTagsToRecommend = DEFAULT_NUMBER_OF_TAGS_TO_RECOMMEND;

	private DBLogic dbLogic;

	/**
	 * constructor.
	 */
	public MultiplexingTagRecommender() {
		localRecommenders = new ArrayList<TagRecommender>();
		distRecommenders  = new ArrayList<TagRecommenderConnector>();
		resultSelector    = new SelectAll();
		postPrivacyFilter = new PostPrivacyFilter();
		setPostModifiers(new LinkedList<PostModifier>());
	}
	/**
	 * destructor
	 */
	protected void finalize() {
		disconnectRecommenders();
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
	 * @return Set of recommended Tags.
	 */
	public void addRecommendedTags(
			Collection<RecommendedTag> recommendedTags, 
			Post<? extends Resource> post, 
			int postID) {

		log.debug("querying["+localRecommenders+", "+distRecommenders+"]");

		// SortedSet holding recommenders results
		SortedSet<RecommendedTag> result = null;
		// id identifying this query
		Long qid = null;

		// list for managing pending recommenders
		List<RecommenderDispatcher> dispatchers = new ArrayList<RecommenderDispatcher>();
		// query's time stamp
		Timestamp ts = new Timestamp(System.currentTimeMillis());

		try {
			// each set of queries is identified by an unique id:
			qid = dbLogic.addQuery(post.getUser().getName(), ts, post, postID, getQueryTimeout());

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
			final Post<? extends Resource> filteredPost = postPrivacyFilter.filterPost(post);
			if (filteredPost != null) {
				// apply post modifiers
				for( PostModifier pm : getPostModifiers() )
					pm.alterPost(filteredPost);
				// query remote recommenders
				for( TagRecommenderConnector con: getDistRecommenders() ) {
					// each recommender is identified by an unique id:
					Long sid = dbLogic.addRecommender(qid, con.getId(), con.getInfo(), con.getMeta());
					RecommenderDispatcher dispatcher = 
						new RecommenderDispatcher(con, filteredPost, qid, sid, null);
					dispatchers.add(dispatcher);
					dispatcher.start();
				}
			}


			/*
			 * query local recommenders
			 * 
			 * they get the unfiltered post, since we trust them
			 */
			for( TagRecommender rec: getLocalRecommenders() ) {
				// each recommender is identified by an unique id:
				Long sid = dbLogic.addRecommender(qid, rec.getClass().getCanonicalName().toString(), rec.getInfo(), null);
				// query recommender
				// FIXME: local recommenders are also aborded when timout is reached,
				//        so their might be now recommendations at all
				RecommenderDispatcher dispatcher = 
					new RecommenderDispatcher(rec, post, qid, sid, null);
				dispatchers.add(dispatcher);
				dispatcher.start();
			};

		} catch (SQLException ex) {
			// TODO Auto-generated catch block
			log.error(ex.getMessage(), ex);
		}
		// wait for recommenders answers
		try {
			// TODO ImplementMe
			Thread.sleep(getQueryTimeout()); 
		} catch (InterruptedException e) {
		}
		// tell dispatchers that they are late
		for( RecommenderDispatcher disp: dispatchers ) {
			disp.abortQuery();
		};

		if( qid!=null )
			try {
				selectResult(qid, recommendedTags);
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				log.error(ex.getMessage(), ex);
			}
	}

	/** Simply adds recommendations to the given collection of recommended tags. 
	 * 
	 * @see org.bibsonomy.recommender.tags.TagRecommender#addRecommendedTags(java.util.SortedSet, org.bibsonomy.model.Post)
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
		// assure that no further results are added while evaluating 
		// collected responses
		// TODO current primitive synchronization prohibits parallel result selection
		synchronized(lockResults) {
			Long rid = dbLogic.addResultSelector(qid, 
					resultSelector.getInfo(), 
					resultSelector.getMeta()
			);
			resultSelector.selectResult(qid, recommendedTags);
			dbLogic.storeRecommendation(qid, rid, recommendedTags);
		}

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
	private synchronized boolean addQueryResponse(
			Long qid, Long recId, long queryTime,
			SortedSet<RecommendedTag> tags) throws SQLException {
		// avoid conflicts with selectResult
		synchronized(lockResults) {
			dbLogic.addRecommendation(qid,recId,tags,queryTime);
		}
		return true;
	}

	public void setDistRecommenders(List<TagRecommenderConnector> distRecommenders) {
		if (getDistRecommenders()!=null) 
			disconnectRecommenders();
		this.distRecommenders = distRecommenders;
		connectRecommenders();
	}
	public List<TagRecommenderConnector> getDistRecommenders() {
		return distRecommenders;
	}

	public void setLocalRecommenders(List<TagRecommender> localRecommenders) {
		this.localRecommenders = localRecommenders;
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
	/**
	 * Threaded class for dispatching and collecting a single recommender query.
	 * 
	 * @author fei
	 */
	public class RecommenderDispatcher extends Thread {
		private Long qid;                         // unique id identifying set of queries
		private Long recId;                       // unique id identifying recommender
		private byte[] recMeta;                   // recommender specific meta information 
		private TagRecommender recommender;
		private boolean abort = false;
		Post<? extends Resource> post;
		SortedSet<RecommendedTag> recommendedTags;

		/**
		 * Constructor for creating a query dispatcher.
		 * @param recommender Recommender whos query should be dispatched
		 * @param post user's post to query the recommender for
		 * @param qid unique id identifying set of queries
		 * @param recId 
		 * @param recommendedTags previously recommended tags
		 */
		public RecommenderDispatcher(TagRecommender recommender,
				Post<? extends Resource> post,
				Long qid, Long recId,
				SortedSet<RecommendedTag> recommendedTags ) {
			this.recommender = recommender;
			this.post = post;
			this.qid = qid;
			this.recId = recId;
			this.recommendedTags = recommendedTags;
			log.debug(System.getProperty("file.encoding"));
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
				if( (recommendedTags!=null) && (recommendedTags.size()>0) ) {
					preset = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
					preset.addAll(recommendedTags);
					recommender.addRecommendedTags(recommendedTags, post);
				} else
					recommendedTags = recommender.getRecommendedTags(post);
				// calculate query-time
			} catch( Exception e ) {
				log.error("Error querying recommender " + recommender.getInfo(), e);
			}
			time = System.currentTimeMillis()-time;
			// add query result
			try {
				addQueryResponse(qid, recId, time, recommendedTags);
			} catch (SQLException ex) {
				log.error("Error storing recommender query response.", ex);
			}
			if( !abort ) {
				log.info("run finished in time " + time);
			} else {
				log.info("Recommender " + recommender.getInfo() + " timed out (" + time + ")");
			}

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
		 * @param qid unique id identifying set of queries
		 * @param recId 
		 * @param recommendedTags previously recommended tags
		 */
		public FeedbackDispatcher(TagRecommender recommender,
				Post<? extends Resource> post) {
			this.recommender = recommender;
			this.post = post;
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


}
