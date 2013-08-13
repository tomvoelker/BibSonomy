package org.bibsonomy.webapp.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.Pair;
import org.bibsonomy.services.recommender.SuggestTree;
import org.bibsonomy.services.recommender.SuggestTree.Node;
import org.springframework.beans.factory.InitializingBean;

/**
 * TitleSuggestionLogic provides the data for the post autocompletion. 
 * 
 * @author nilsraabe
 * @version $Id$
 */
public class TitleSuggestionLogic implements InitializingBean {
	private static final Log log = LogFactory.getLog(TitleSuggestionLogic.class);
	
	private static final int TOP_K = 20; // TODO: how many?

	private SuggestTree publicationTree;
	private SuggestTree bookmarkTree;
	
	private String sourceFilePath;
		
	@Override
	public void afterPropertiesSet() throws Exception {
		publicationTree = buildTree("publication");
		bookmarkTree = buildTree("bookmark");
	}

	private SuggestTree buildTree(String file) {
		log.info("building " + file + " tree");
		final SuggestTree tree = new SuggestTree(TOP_K);
		try {
			final BufferedReader publicationReader = new BufferedReader(new FileReader(sourceFilePath + "/" + file + "_title.txt"));
			String title = null;
			
			while ((title = publicationReader.readLine()) != null) {
				int rating = Integer.parseInt(publicationReader.readLine());
				tree.put(title, rating);
			}
			publicationReader.close();
		} catch (IOException e) {
			log.error("error while reading publication titles for suggestion tree", e);
		}
		log.info("finished building " + file + " tree done.");
		return tree;
	}

	
	/**
	 * Get all publication suggestions to a given prefix.
	 * 
	 * @param prefix
	 * 		  The user input whereupon the autocompletion recommends.
	 * @return
	 * 		  A list of pairs whereby the String represents the recommendation and the Integer the rating.
	 */
	public List<Pair<String, Integer>> getPublicationSuggestion(String prefix) {
		return getSuggestion(this.publicationTree, prefix);
	}


	private List<Pair<String, Integer>> getSuggestion(final SuggestTree tree, String prefix) {
		final Node node = tree.getSuggestions(prefix);		
		final List<Pair<String, Integer>> suggestion = new LinkedList<Pair<String, Integer>>();
		if (node == null) {
			return suggestion;
		}
		
		for (int i = 0; i < node.size(); i++) {
			suggestion.add(new Pair<String, Integer>(node.getSuggestion(i), node.getWeight(i)));
		}
		
		return suggestion;
	}
	
	/**
	 * Get all bookmark suggestions to a given prefix.
	 * 
	 * @param prefix
	 * 		  The user input whereupon the autocompletion recommends.
	 * @return
	 * 		  A list of pairs whereby the String represents the recommendation and the Integer the rating.
	 */
	public List<Pair<String, Integer>> getBookmarkSuggestion(String prefix) {
		return getSuggestion(this.bookmarkTree, prefix);
	}
	
	/**
	 * Get all publication and bookmark suggestions to a given prefix.
	 * 
	 * @param prefix
	 * 		  The user input whereupon the autocompletion recommends.
	 * @return
	 * 		  A list of pairs whereby the String represents the recommendation and the Integer the rating.
	 */
	public List<Pair<String, Integer>> getPostSuggestion(final String prefix) {
		final List<Pair<String, Integer>> suggestion = new LinkedList<Pair<String,Integer>>();
		
		suggestion.addAll(this.getPublicationSuggestion(prefix));
		suggestion.addAll(this.getBookmarkSuggestion(prefix));
		
		Collections.sort(suggestion, new PairComperator());
		
		return suggestion;
	}
	
	/**
	 * @param sourceFilePath the titlePath to set
	 */
	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}
}
// TODO: cleanup using Integer#compare method!
class PairComperator implements Comparator<Pair<String, Integer>>{
	 
	@Override
	public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
		if(o1.getSecond() < o2.getSecond()) {
			return -1;
		}
		
		if(o1.getSecond() > o2.getSecond()) {
			return 1;
		}
		
		// returning 0 is dangerous (for example if someone uses a TreeSet)
		return System.identityHashCode(o1.getFirst()) - System.identityHashCode(o2.getFirst());
	}
} 
