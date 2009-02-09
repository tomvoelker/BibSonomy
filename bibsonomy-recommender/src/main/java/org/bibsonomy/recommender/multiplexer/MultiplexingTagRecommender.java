package org.bibsonomy.recommender.multiplexer;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.RecommendedTagComparator;
import org.bibsonomy.model.Resource;
import org.bibsonomy.recommender.DBAccess;
import org.bibsonomy.recommender.RecommenderConnector;
import org.bibsonomy.recommender.TagRecommender;
import org.bibsonomy.recommender.WebserviceRecommender;
import org.bibsonomy.recommender.multiplexer.strategy.RecommendationSelector;
import org.bibsonomy.recommender.multiplexer.strategy.SelectAll;
import org.bibsonomy.recommender.tags.simple.DummyTagRecommender;

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
	private List<RecommenderConnector> distRecommenders;    // recommenders with remote access
	
	private long queryTimeout = 100;                // timeout for querying distant recommenders
	private RecommendationSelector resultSelector;
	/**
	 * constructor.
	 */
	public MultiplexingTagRecommender() {
		localRecommenders = new ArrayList<TagRecommender>();
		distRecommenders  = new ArrayList<RecommenderConnector>();
		resultSelector    = new SelectAll();
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
	public boolean addRecommenderConnector(RecommenderConnector recommender) {
		log.info("adding local recommender: "+recommender.getInfo());
		getDistRecommenders().add(recommender);
		return true;
	}
	
	/** 
	 * @return false if none of the registered recommenders could be initialized
	 */
	public boolean connectRecommenders() {
		// connect to each recommender
		for(RecommenderConnector rec: getDistRecommenders()) {
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
		for(RecommenderConnector rec: getDistRecommenders()) {
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
	/** Simply adds recommendations at end of list. 
	 * 
	 * @see org.bibsonomy.recommender.TagRecommender#addRecommendedTags(java.util.SortedSet, org.bibsonomy.model.Post)
	 */	
	public void addRecommendedTags(SortedSet<RecommendedTag> recommendedTags,
			Post<? extends Resource> post) {
		 recommendedTags.addAll(getRecommendedTags(post));
	}

	public String getInfo() {
		return "Multiplexing recommender for querying several independent recommenders.";
	}

	public SortedSet<RecommendedTag> getRecommendedTags(
			Post<? extends Resource> post) {
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
			qid = DBAccess.addQuery(post.getUser().getName(), ts, post);
			
			// query remote recommenders
			for( RecommenderConnector con: getDistRecommenders() ) {
				// each recommender is identified by an unique id:
				Long sid = DBAccess.addRecommender(qid, con.getInfo(), con.getMeta());
				RecommenderDispatcher dispatcher = 
					new RecommenderDispatcher(con, post, qid, sid, null);
				dispatchers.add(dispatcher);
				dispatcher.start();
			};
			// query local recommenders
			for( TagRecommender rec: getLocalRecommenders() ) {
				// each recommender is identified by an unique id:
				Long sid = DBAccess.addRecommender(qid, rec.getInfo(), null);
				// add result to database
				addQueryResponse(qid, sid, 0, rec.getRecommendedTags(post));
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
				result = selectResult(qid);
			} catch (SQLException ex) {
				// TODO Auto-generated catch block
				log.error(ex.getMessage(), ex);
			}
		
		// all done.
		return result;
	}

	//------------------------------------------------------------------------
	// Implementation of distributed recommendation evaluation
	//------------------------------------------------------------------------
	private SortedSet<RecommendedTag> selectResult(Long qid) throws SQLException {
		// assure that no further results are added while evaluating 
		// collected responses
		// TODO current primitive synchronization prohibits parallel result selection
		synchronized(lockResults) {
			return resultSelector.selectResult(qid);
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
			DBAccess.addRecommendation(qid,recId,tags,queryTime);
		}
		return true;
	}

	public void setDistRecommenders(List<RecommenderConnector> distRecommenders) {
		if (getDistRecommenders()!=null) 
			disconnectRecommenders();
		this.distRecommenders = distRecommenders;
		connectRecommenders();
	}
	public List<RecommenderConnector> getDistRecommenders() {
		return distRecommenders;
	}

	public void setLocalRecommenders(List<TagRecommender> localRecommenders) {
		this.localRecommenders = localRecommenders;
	}
	public List<TagRecommender> getLocalRecommenders() {
		return localRecommenders;
	}

	public void setQueryTimeout(long queryTimeout) {
		this.queryTimeout = queryTimeout;
	}
	public long getQueryTimeout() {
		return queryTimeout;
	}

	public void setResultSelector(RecommendationSelector resultSelector) {
		this.resultSelector = resultSelector;
	}
	public RecommendationSelector getResultSelector() {
		return resultSelector;
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
			// TODO ImplementMe
			log.warn("run() not implemented.");
			// for query-time logging
			long time = System.currentTimeMillis();
			SortedSet<RecommendedTag> preset = null;
			// actually query the recommender
			if( (recommendedTags!=null) && (recommendedTags.size()>0) ) {
				preset = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
				preset.addAll(recommendedTags);
				recommender.addRecommendedTags(recommendedTags, post);
			} else
				recommendedTags = recommender.getRecommendedTags(post);
			// calculate query-time
			time = System.currentTimeMillis()-time;
			// add query result, if not timed out
			if( !abort ) {
				try {
					addQueryResponse(qid, recId, time, recommendedTags);
				} catch (SQLException ex) {
					// TODO Auto-generated catch block
					log.error(ex.getMessage(), ex);
				}
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
}
