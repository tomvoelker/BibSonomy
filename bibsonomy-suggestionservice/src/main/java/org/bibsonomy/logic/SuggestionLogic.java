/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.suggesttree.SuggestTree;
import net.sourceforge.suggesttree.SuggestTree.Node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.Suggestion;


/**
 * FIXME: load from slave database
 * FIXME: update per cron from database
 * FIXME: prefix currently can only matched against the first word
 * 
 * TitleSuggestionLogic provides the data for the post autocompletion.
 * 
 * @author nilsraabe
 */
public class SuggestionLogic {
	private static final Log log = LogFactory.getLog(SuggestionLogic.class);
	
	private static final int TOP_K 	= 20; // TODO: how many?

	private SuggestTree publicationTree;
	private SuggestTree bookmarkTree;
	
	private String sourceFilePath;
	
	/**
	 * inits the data structure
	 * @throws Exception
	 */
	public void init() throws Exception {
		publicationTree = buildTree("publication");
		bookmarkTree = buildTree("bookmark");
	}
	
	/**
	 * Setup the suggest tree.
	 * 
	 * Attention:
	 * The path in project.properties (titleSuggestion.sourceFilePath) must be set correctly.
	 * 
	 * @param file
	 * 		name of the source file (source file should be in the titleSuggestion.sourceFilePath folder)
	 * @return
	 * 		returns the prepared suggest tree
	 */
	private SuggestTree buildTree(String file) {
		log.info("building " + file + " tree");
		
		final SuggestTree tree = new SuggestTree(TOP_K);
		
		File sourceFile = new File(sourceFilePath + "/" + file + "_title.txt");
		
		if (!sourceFile.exists()) {
			log.warn("Source File '" + file + "_title.txt" + "' NOT found in path : " + sourceFilePath + "/  - Cannot build tree !");
			return tree;
		} 
		
		try {
			final BufferedReader publicationReader = new BufferedReader(new FileReader(sourceFilePath + "/" + file + "_title.txt"));
			String title = null;
			
			while ((title = publicationReader.readLine()) != null) {

				// FIXME rating is almost nonexistent in live system
				
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
	public SortedSet<Suggestion> getPublicationSuggestion(String prefix) {
		return getSuggestion(this.publicationTree, prefix);
	}

	private SortedSet<Suggestion> getSuggestion(final SuggestTree tree, String prefix) {
		final Node node = tree.getSuggestions(prefix);
		final SortedSet<Suggestion> suggestion = new TreeSet<Suggestion>();
		
		if (node == null) {
			return suggestion;
		}
		
		for (int i = 0; i < node.size(); i++) {
			suggestion.add(new Suggestion(node.getSuggestion(i), node.getWeight(i)));
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
	public SortedSet<Suggestion> getBookmarkSuggestion(String prefix) {
		return getSuggestion(this.bookmarkTree, prefix);
	}
	
	/**
	 * get all publication and bookmark suggestions to a given prefix.
	 * 
	 * @param prefix
	 * 		  The user input whereupon the autocompletion recommends.
	 * @return
	 * 		  A list of pairs whereby the String represents the recommendation and the Integer the rating.
	 */
	public SortedSet<Suggestion> getPostSuggestion(final String prefix) {
		final SortedSet<Suggestion> suggestion = new TreeSet<Suggestion>();
		
		suggestion.addAll(this.getPublicationSuggestion(prefix));
		suggestion.addAll(this.getBookmarkSuggestion(prefix));
		
		return suggestion;
	}
	
	/**
	 * Set the location whereby the .txt files of publication and bookmark titles are.
	 * (BTW: Text Files are generated by the file batch_suggestion_title.java)
	 * 
	 * @param sourceFilePath the titlePath to set
	 */
	public void setSourceFilePath(String sourceFilePath) {
		this.sourceFilePath = sourceFilePath;
	}
}