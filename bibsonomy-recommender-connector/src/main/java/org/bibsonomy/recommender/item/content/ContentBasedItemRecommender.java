package org.bibsonomy.recommender.item.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.recommender.item.AbstractItemRecommender;

import recommender.core.interfaces.model.ItemRecommendationEntity;
import recommender.core.interfaces.model.RecommendationItem;
import recommender.core.interfaces.model.RecommendationTag;
import recommender.core.util.RecommendationResultComparator;
import recommender.impl.model.RecommendedItem;

/**
 * This recommender realizes a mix of Collaborative Filtering and Content-Based recommender approaches.
 * At first the items of the similar users are collected. Then those are compared to the items of the requesting user using the cosine
 * similarity over an inverted index data structure using the tf-idf weighting measurement.
 * 
 * The score of the recommendations is equal to the computed cosine similarity.
 * 
 * @author lukas
 *
 */
public class ContentBasedItemRecommender extends AbstractItemRecommender {

	private static final String INFO = "This recommender takes similar users and evaluates their resources by similarity and returns the ones most similar.";
	
	protected static final String TOKEN_DELIMITER = " ";
	
	protected static final int DEFAULT_MAXIMUM_ITEMS_TO_EVALUATE = 40;

	protected int maxItemsToEvaluate = DEFAULT_MAXIMUM_ITEMS_TO_EVALUATE;
	
	@Override
	public String getInfo() {
		return INFO;
	}

	/*
	 * (non-Javadoc)
	 * @see recommender.impl.item.AbstractItemRecommender#addRecommendedItemsInternal(java.util.Collection, recommender.core.interfaces.model.ItemRecommendationEntity)
	 */
	@Override
	protected void addRecommendedItemsInternal(
			Collection<RecommendedItem> recommendations,
			ItemRecommendationEntity entity) {
		
		final List<RecommendationItem> requestingUserItems = this.dbAccess.getItemsForUser(maxItemsToEvaluate, null); // FIXME (refactor) entity.getUserName() 
		
		final Set<String> requestingUserTitles = calculateRequestingUserTitleSet(requestingUserItems);
		
		List<RecommendationItem> userItems = new ArrayList<RecommendationItem>();
		
		userItems.addAll(this.dbAccess.getItemsForContentBasedFiltering(maxItemsToEvaluate, entity));
		
		final List<RecommendedItem> results = this.calculateSimilarItems(userItems, requestingUserItems, requestingUserTitles);
		
		recommendations.addAll(results);
		
	}

	/**
	 * This method creates a set with titles of the requesting user items.
	 * The set is needed to avoid recommendation of known items to the user.
	 * 
	 * @param requestingUserItems the items of the requesting user
	 */
	protected Set<String> calculateRequestingUserTitleSet(List<RecommendationItem> requestingUserItems) {
		final Set<String> requestingUserTitles = new HashSet<String>();
		for(RecommendationItem item : requestingUserItems) {
			requestingUserTitles.add(item.getTitle());
		}
		return requestingUserTitles;
	}

	/**
	 * calculates the similarity of the items and scores them with their cosine sim.
	 * 
	 * @param userItems the items of the similar users
	 * @param requestingUserItems the items of the requesting user
	 * @param requestingUserTitles a set of all titles belonging to the requested user
	 * 
	 * @return the weighted items in descending order
	 */
	public List<RecommendedItem> calculateSimilarItems(
			final List<RecommendationItem> userItems,
			final List<RecommendationItem> requestingUserItems, final Set<String> requestingUserTitles) {
		
		final List<RecommendedItem> results = new ArrayList<RecommendedItem>();
		
		final Map<String, List<IndexEntry>> invertedIndex = new HashMap<String, List<IndexEntry>>();
		final Map<String, Double> idfs = new HashMap<String, Double>();
		final Map<String, Document> saveDocuments = new HashMap<String, Document>();
		calculateInvertedIndex(requestingUserItems, userItems, invertedIndex, idfs, saveDocuments);
		
		//calculate similarity of each item to all items of the requesting user
		for(RecommendationItem toCheck : userItems) {
			
			double similarity = calculateSimilarity(toCheck, requestingUserItems, 
					requestingUserTitles, invertedIndex, idfs, saveDocuments);
			
			if(similarity != 0) {
				RecommendedItem recItem = new RecommendedItem(toCheck);
				recItem.setScore(similarity);
				
				results.add(recItem);
			}
			
		}
		
		//sort the results descending by its scores
		Collections.sort(results, new RecommendationResultComparator<RecommendedItem>());
		
		//we want to present only a fix number of different items with different themes -> title
		final List<String> addedTitles = new ArrayList<String>();
		Iterator<RecommendedItem> it = results.iterator();
		int index = 1;
		while(it.hasNext()) {
			RecommendedItem item = it.next();
			if(addedTitles.contains(item.getTitle())) {
				it.remove();
				continue;
			}
			else if(index > this.numberOfItemsToRecommend) {
				it.remove();
				continue;
			}
			index++;
			addedTitles.add(item.getTitle());
		}
		
		return results;
		
	}
	
	/**
	 * computes the inverted index over the corpus of all given documents
	 * 
	 * @param requestingUserItems the items of the requesting user
	 * @param useritems the items of the similar users
	 * @param invertedIndex the inverted index 
	 * @param idfs a map which maps tokens to idfs
	 * @param saveDocuments  a list of documents with their lengths
	 */
	public void calculateInvertedIndex(final List<RecommendationItem> requestingUserItems, final List<RecommendationItem> useritems, 
			final Map<String, List<IndexEntry>> invertedIndex, final Map<String, Double> idfs, final Map<String, Document> saveDocuments) {
		
		//calculate the inverted index entries for all documents including tf
		for(RecommendationItem item : requestingUserItems) {	
			Document doc = new Document(item);
			saveDocuments.put(doc.getItem().getId(), doc);
			final Map<String, IndexEntry> documentEntries = calculateIndexEntries(doc);
			for(String key : documentEntries.keySet()) {
				if(invertedIndex.containsKey(key)) {
					invertedIndex.get(key).add(documentEntries.get(key));
				} else {
					invertedIndex.put(key, new ArrayList<IndexEntry>());
					invertedIndex.get(key).add(documentEntries.get(key));
				}
			}
		}
		for(RecommendationItem item : useritems) {
			Document doc = new Document(item);
			saveDocuments.put(doc.getItem().getId(), doc);
			final Map<String, IndexEntry> documentEntries = calculateIndexEntries(doc);
			for(String key : documentEntries.keySet()) {
				if(invertedIndex.containsKey(key)) {
					invertedIndex.get(key).add(documentEntries.get(key));
				} else {
					invertedIndex.put(key, new ArrayList<IndexEntry>());
					invertedIndex.get(key).add(documentEntries.get(key));
				}
			}
		}
		
		//compute idf weights and add those to the final index
		final int size = invertedIndex.keySet().size()+1;
		for(String key : invertedIndex.keySet()) {
			idfs.put(key, Math.log(size/(double)invertedIndex.get(key).size()));
		}
		
		//compute vector length for each vector
		for(String key : invertedIndex.keySet()) {
			for(IndexEntry entry : invertedIndex.get(key)) {
				entry.getDoc().setLength(entry.getDoc().getLength() + Math.pow(entry.getTf()*idfs.get(key), 2));
			}
		}
		
		for(String key : saveDocuments.keySet()) {
			saveDocuments.get(key).setLength(Math.sqrt(saveDocuments.get(key).getLength()));
		}
		
	}
	
	/**
	 * calculates a list of index entries which shall be added to the inverted index
	 * 
	 * @param doc the document to calculate the list for
	 *  
	 * @return a map which maps tokens to index entries, which shall be added to the inverted index
	 */
	protected Map<String, IndexEntry> calculateIndexEntries(Document doc) {
		
		final Map<String, Integer> alreadyAdded = new HashMap<String, Integer>();		
		
		//for each token add a new entry or increase the entries tf
		for(String token : this.calculateTokens(doc.getItem())) {
			if(alreadyAdded.keySet().contains(token)) {
				alreadyAdded.put(token, alreadyAdded.get(token)+1) ;
			} else {
				alreadyAdded.put(token, 1);
			}
		}
		
		int max = 0;
		for(String key : alreadyAdded.keySet()) {
			if(alreadyAdded.get(key) > max) {
				max = alreadyAdded.get(key);
			}
		}
		
		//retrieve correct tfs by normalizing
		final HashMap<String, IndexEntry> results = new HashMap<String, IndexEntry>();
		for(String key : alreadyAdded.keySet()) {
			results.put(key, new IndexEntry(doc, alreadyAdded.get(key)/(double)max));
		}
		
		return results;
	}
	
	/**
	 * calculates the sum of similarities of the item toCheck to the items of the requesting user
	 * 
	 * @param toCheck the item to calculate the similarity to the requesting user's items for
	 * @param requestingUserTitles a set of all titles belonging to the requesting user
	 * @param idfs a map which maps tokens to their idfs
	 * @param saveDocuments a list of documents with their lengths
	 * @param requestingUserItems the list of items belonging to the requesting user
	 * 
	 * @return a similarity value -> the bigger the better
	 */
	protected double calculateSimilarity(final RecommendationItem toCheck, final List<RecommendationItem> requestedUserItems, final Set<String> requestingUserTitles,
			final Map<String, List<IndexEntry>> invertedIndex, final Map<String, Double> idfs, final Map<String, Document> saveDocuments) {
		
		// avoid recommendation of known items
		if(requestingUserTitles.contains(toCheck.getTitle())) {
			return 0.0;
		}
		
		double similarity = 0.0;
		
		final Map<String, Double> similarities = new HashMap<String, Double>();
		
		//Maps tokens to it's weights
		final Map<String, Double> tokens = new HashMap<String, Double>();
		int max = 1;
		for(String token : this.calculateTokens(toCheck)) {
			token = token.toLowerCase();
			if(tokens.keySet().contains(token)) {
				double newWeight = tokens.get(token);
				tokens.put(token, newWeight+1);
				if(newWeight+1 > max) {
					max++;
				}
			} else {
				tokens.put(token, 1.0);
			}
		}
		
		
		for(String token : tokens.keySet()) {
			tokens.put(token, tokens.get(token)/(double)max);
		}
		
		
		double calcSim = 0.0;
		for(String token : tokens.keySet()) {
			for(IndexEntry entry : invertedIndex.get(token)) {
				
				// if the document doesn't belong to the requesting user skip it since we dan't want to measure similarity
				// of the items of the requesting user
				if(!requestedUserItems.contains(entry.getDoc().getItem())) {
					continue;
				}
				
				calcSim = (entry.getTf()*idfs.get(token)) * (tokens.get(token)*idfs.get(token));
				
				if(similarities.containsKey(entry.getDoc().getItem().getId()) && !entry.getDoc().getItem().getId().equals(toCheck.getId())) {
					similarities.put(entry.getDoc().getItem().getId(), calcSim);
				} else if(!similarities.containsKey(entry.getDoc().getItem().getId()) && !entry.getDoc().getItem().getId().equals(toCheck.getId())) {
					similarities.put(entry.getDoc().getItem().getId(), calcSim);
				}
				
			}
		}
		
		//prevent division by zero if no item containing a token was found
		if(similarities.keySet().size() == 0) {
			return 0.0;
		}
		
		final double toCheckLength = saveDocuments.get(toCheck.getId()).getLength();
		for(String item : similarities.keySet()) {
			similarities.put(item, similarities.get(item)/(saveDocuments.get(item).getLength()*toCheckLength));
		}
		
		for(String key : similarities.keySet()) {
			similarity += similarities.get(key);
		}
		similarity /= similarities.keySet().size();
		
		return similarity;
	}

	/**
	 * Helper method for extracting tokens out of an item.
	 * 
	 * @param item the item to extract the tokens out of
	 * @return a list of tokens
	 */
	protected List<String> calculateTokens(RecommendationItem item) {
		final ArrayList<String> tokens = new ArrayList<String>();
		
		for(RecommendationTag tag : item.getTags()) {
			tokens.add(tag.getName().toLowerCase());
		}
		for(String titleToken : item.getTitle().split(TOKEN_DELIMITER)) {
			tokens.add(titleToken.toLowerCase());
		}
		
		return tokens;
	}
	
	@Override
	protected void setFeedbackInternal(ItemRecommendationEntity entity, RecommendedItem item) {
		/*
		 * no special feedback handling
		 */
	}

	
	/**
	 * private class as inverted index helper
	 * 
	 * @author lukas
	 *
	 */
	protected class IndexEntry {
		
		private Document doc;
		private double tf;
		
		public IndexEntry(Document doc, double tf) {
			this.doc = doc;
			this.tf = tf;
		}
		
		public Document getDoc() {
			return doc;
		}
		
		public double getTf() {
			return tf;
		}
		
	}
	
	/**
	 * 
	 * 
	 * @author lukas
	 *
	 */
	protected class Document {
		
		private double length;
		private RecommendationItem item;
		
		public Document(RecommendationItem item) {
			this.item = item;
			this.length = 0.0;
		}
		
		public RecommendationItem getItem() {
			return item;
		}
		
		public double getLength() {
			return length;
		}
		
		public void setLength(double length) {
			this.length = length;
		}
		
	}
	
	public int getMaxItemsToEvaluate() {
		return maxItemsToEvaluate;
	}
	public void setMaxItemsToEvaluate(int maxItemsToEvaluate) {
		this.maxItemsToEvaluate = maxItemsToEvaluate;
	}
}
