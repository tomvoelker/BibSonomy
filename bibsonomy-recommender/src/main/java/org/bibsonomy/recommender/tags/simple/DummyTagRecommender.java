package org.bibsonomy.recommender.tags.simple;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.tags.TagRecommenderConnector;
import org.bibsonomy.services.recommender.TagRecommender;

/**
 * Dummy recommender for simulating different latency periods.
 * 
 * @author fei
 * @version $Id$
 */
public class DummyTagRecommender implements TagRecommender, TagRecommenderConnector {
	private static final Logger log = Logger.getLogger(DummyTagRecommender.class);
	private boolean init = false;
	
	/**  
	 * Do nothing.
	 * @see org.bibsonomy.services.recommender.TagRecommender#addRecommendedTags(java.util.Collection, org.bibsonomy.model.Post)
	 */
	public void addRecommendedTags(Collection<RecommendedTag> recommendedTags, Post<? extends Resource> post) {
		log.info("Dummy recommender: addRecommendedTags.");
		long wait = (long)(Math.random()*1000); 

		// create informative recommendation:
		for( int i=0; i<(int)(10*Math.random()); i++) {
			double score = Math.random();
			double confidence = Math.random();
			DecimalFormat df = new DecimalFormat( "0.00" );
			String re = "Dummy("+df.format(score)+","+df.format(confidence)+"["+wait+"])";
			recommendedTags.add(new RecommendedTag(re, score, confidence));
		};
		
		try {
			Thread.sleep(wait);
		} catch (InterruptedException e) {
			// nothing to do.
		}
		
	}

	public String getInfo() {
		return "Dummy recommender which does nothing at all.";
	}

	public SortedSet<RecommendedTag> getRecommendedTags(Post<? extends Resource> post) {
		final SortedSet<RecommendedTag> recommendedTags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		addRecommendedTags(recommendedTags, post);
		return recommendedTags;
	}

	//------------------------------------------------------------------------
	// RecommenderConnector interface implementation
	//------------------------------------------------------------------------
	public boolean connect() throws Exception {
		// TODO Auto-generated method stub
		if( init )
			log.info("connected!");
		else
			log.warn("recommender wasn't initialized prior to connection");
		
		return true;
	}

	public boolean disconnect() throws Exception {
		// TODO Auto-generated method stub
		log.info("disconnected!");
		return true;
	}

	public boolean initialize(Properties props) throws Exception {
		// TODO Auto-generated method stub
		log.info("initialized!");
		return true;
	}

	public byte[] getMeta() {
		// TODO Auto-generated method stub
		return null;
	}
}
