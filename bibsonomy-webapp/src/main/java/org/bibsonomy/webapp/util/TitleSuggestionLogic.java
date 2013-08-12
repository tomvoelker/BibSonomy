package org.bibsonomy.webapp.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

	private SuggestTree bibtexTree;

	private SuggestTree bookmarkTree;
	
	private String sourceFilePath;
		
	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("Startup SuggestTreeLogic ...");
		
		List<Pair<String, Integer>> bibtexTitleList 	= new ArrayList();
		List<Pair<String, Integer>> bookmarkTitleList 	= new ArrayList();
						
		/*
		 * Read and store the bibtex title
		 */
		try {
			BufferedReader bibtexReader = new BufferedReader(new FileReader( sourceFilePath + "/bibtex_title.txt"));
			String title = null;
						
			while ((title = bibtexReader.readLine()) != null) {
				int rating = Integer.parseInt(bibtexReader.readLine());
				bibtexTitleList.add(new Pair<String, Integer>(title, rating));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e);
		}
				
		/*
		 * Read and store the bookmark title
		 */
		try {
			BufferedReader bookmarkReader = new BufferedReader(new FileReader(sourceFilePath + "/bookmark_title.txt"));
			String title = null;
						
			while ((title = bookmarkReader.readLine()) != null) {
				int rating = Integer.parseInt(bookmarkReader.readLine());
				bookmarkTitleList.add(new Pair<String, Integer>(title, rating));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e);
		}

		log.info("SuggestTreeLogic: Reading complete - now filling tree !");
		
		bibtexTree 		= new SuggestTree(bibtexTitleList.size());
		bookmarkTree 	= new SuggestTree(bookmarkTitleList.size());

		for(Pair<String, Integer> temp : bibtexTitleList) {
			bibtexTree.put(temp.getFirst(), temp.getSecond());
		}
		
		log.info("SuggestTreeLogic: Bibtex tree ready !");
		
		for(Pair<String, Integer> temp : bookmarkTitleList) {
			bookmarkTree.put(temp.getFirst(), temp.getSecond());
		}		
				
		log.info("SuggestTreeLogic: Bookmark tree ready !");
		
		log.info("SuggestTreeLogic: finished startup");
		
	}

	
	/**
	 * Get all bibtex suggestions to a given prefix.
	 * 
	 * @param prefix
	 * 		  The user input whereupon the autocompletion recommends.
	 * @return
	 * 		  A list of pairs whereby the String represents the recommendation and the Integer the rating.
	 */
	public List<Pair<String, Integer>> getBibtexSuggestion(String prefix) {
		Node node = bibtexTree.getSuggestions(prefix);
		
		if(node == null) {
			return new ArrayList<Pair<String, Integer>>();
		}
		
		List<Pair<String, Integer>> suggestion = new ArrayList();
		
		for(int i = 0; i < node.size(); i++) {
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
		Node node = bookmarkTree.getSuggestions(prefix);
		
		if(node == null) {
			return new ArrayList<Pair<String, Integer>>();
		}
		
		List<Pair<String, Integer>> suggestion = new ArrayList();
		
		for(int i = 0; i < node.size(); i++) {
			suggestion.add(new Pair<String, Integer>(node.getSuggestion(i), node.getWeight(i)));
		}
		
		return suggestion;
	}
	
	/**
	 * Get all bibtex and bookmark suggestions to a given prefix.
	 * 
	 * @param prefix
	 * 		  The user input whereupon the autocompletion recommends.
	 * @return
	 * 		  A list of pairs whereby the String represents the recommendation and the Integer the rating.
	 */
	public List<Pair<String, Integer>> getPostSuggestion(String prefix) {
		
		Node node = null;
		
		List<Pair<String, Integer>> suggestion = new ArrayList();
		
		/*
		 * Get Bookmark suggestions
		 */
		
		node = bookmarkTree.getSuggestions(prefix);
		
		if(node != null) {
			for(int i = 0; i < node.size(); i++) {
				suggestion.add(new Pair<String, Integer>(node.getSuggestion(i), node.getWeight(i)));
			}
		}
		
		/*
		 * Get Bibtex suggestion
		 */
		
		node = bibtexTree.getSuggestions(prefix);
		
		if(node != null) {
			for(int i = 0; i < node.size(); i++) {
				suggestion.add(new Pair<String, Integer>(node.getSuggestion(i), node.getWeight(i)));
			}
		}
		
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

class PairComperator implements Comparator<Pair<String, Integer>>{
	 
	@Override
	public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
		
		if(o1.getSecond() < o2.getSecond()) {
			return -1;
		}
		
		if(o1.getSecond() > o2.getSecond()) {
			return 1;
		}
		
		return 0;
	}
} 
