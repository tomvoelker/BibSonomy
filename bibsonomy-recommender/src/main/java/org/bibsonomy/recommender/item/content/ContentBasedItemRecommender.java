/**
 * BibSonomy Recommendation - Tag and resource recommender.
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
package org.bibsonomy.recommender.item.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.recommender.item.AbstractItemRecommender;
import org.bibsonomy.recommender.item.model.RecommendationUser;
import org.bibsonomy.recommender.item.model.RecommendedPost;

import recommender.core.util.RecommendationResultComparator;

/**
 * This recommender realizes a mix of Collaborative Filtering and Content-Based recommender approaches.
 * At first the items of the similar users are collected. Then those are compared to the items of the requesting
 * user using the cosine similarity over an inverted index data structure using the tf-idf weighting measurement.
 * 
 * The score of the recommendations is equal to the computed cosine similarity.
 * 
 * @author lukas
 * @param <R> 
 *
 */
public class ContentBasedItemRecommender<R extends Resource> extends AbstractItemRecommender<R> {

	private static final String INFO = "This recommender takes similar users and evaluates their resources by similarity and returns the ones most similar.";
	
	/** the delimiter to used to spilt string into tokens */
	protected static final String TOKEN_DELIMITER = " ";
	
	/** the default value of posts to evaluate */
	protected static final int DEFAULT_MAXIMUM_ITEMS_TO_EVALUATE = 40;
	
	/** the max items to calculate similar posts */
	protected int maxItemsToEvaluate = DEFAULT_MAXIMUM_ITEMS_TO_EVALUATE;
	
	@Override
	public String getInfo() {
		return INFO;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.AbstractItemRecommender#addRecommendedItemsInternal(java.util.Collection, org.bibsonomy.recommender.item.model.RecommendationUser)
	 */
	@Override
	protected void addRecommendedItemsInternal(Collection<RecommendedPost<R>> recommendations, RecommendationUser entity) {
		final List<Post<? extends Resource>> requestingUserItems = this.dbAccess.getItemsForUser(maxItemsToEvaluate, entity.getUserName());
		final Set<String> requestingUserTitles = calculateRequestingUserTitleSet(requestingUserItems);
		
		final List<Post<R>> userItems = this.dbAccess.getItemsForContentBasedFiltering(maxItemsToEvaluate, entity);
		
		final List<RecommendedPost<R>> results = this.calculateSimilarItems(userItems, requestingUserItems, requestingUserTitles);
		recommendations.addAll(results);
	}

	/**
	 * This method creates a set with titles of the requesting user items.
	 * The set is needed to avoid recommendation of known items to the user.
	 * 
	 * @param requestingUserItems the items of the requesting user
	 * @return a set of titles of the requesting user
	 */
	protected Set<String> calculateRequestingUserTitleSet(List<Post<? extends Resource>> requestingUserItems) {
		final Set<String> requestingUserTitles = new HashSet<String>();
		for (final Post<? extends Resource> item : requestingUserItems) {
			requestingUserTitles.add(item.getResource().getTitle());
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
	public List<RecommendedPost<R>> calculateSimilarItems(final List<Post<R>> userItems, final List<Post<?>> requestingUserItems, final Set<String> requestingUserTitles) {
		final List<RecommendedPost<R>> results = new ArrayList<RecommendedPost<R>>(); // TODO: use tree set
		
		final Map<String, Document<? extends Resource>> saveDocuments = new HashMap<String, Document<? extends Resource>>();
		
		final Map<String, List<IndexEntry<? extends Resource>>> invertedIndex = calculateInvertedIndex(requestingUserItems, userItems, saveDocuments);
		final Map<String, Double> idfs = calculateIdfs(invertedIndex);
		
		// calculate similarity of each item to all items of the requesting user
		for (final Post<R> toCheck : userItems) {
			final double similarity = calculateSimilarity(toCheck, requestingUserItems, requestingUserTitles, invertedIndex, idfs, saveDocuments);
			
			if (similarity != 0) {
				RecommendedPost<R> recItem = new RecommendedPost<R>();
				recItem.setPost(toCheck);
				recItem.setScore(similarity);
				results.add(recItem);
			}
		}
		
		// sort the results descending by its scores
		Collections.sort(results, new RecommendationResultComparator<RecommendedPost<R>>());
		
		// we want to present only a fix number of different items with different themes -> title
		final List<String> addedTitles = new ArrayList<String>();
		Iterator<RecommendedPost<R>> it = results.iterator();
		int index = 1;
		while (it.hasNext()) {
			RecommendedPost<R> item = it.next();
			// FIXME: do not use title
			if (addedTitles.contains(item.getPost().getResource().getTitle())) {
				it.remove();
				continue;
			} else if (index > this.numberOfItemsToRecommend) {
				it.remove();
				continue;
			}
			index++;
			addedTitles.add(item.getTitle());
		}
		
		return results;
	}
	
	/**
	 * @param invertedIndex
	 * @return
	 */
	private static Map<String, Double> calculateIdfs(Map<String, List<IndexEntry<? extends Resource>>> invertedIndex) {
		final Map<String, Double> idfs = new HashMap<String, Double>();
		// compute idf weights and add those to the final index
		final int size = invertedIndex.keySet().size() + 1; // TODO: + 1?
		for (final String key : invertedIndex.keySet()) {
			idfs.put(key, Double.valueOf(Math.log(size / (double) invertedIndex.get(key).size())));
		}
		
		// compute vector length for each vector
		for (final String key : invertedIndex.keySet()) {
			for (IndexEntry<? extends Resource> entry : invertedIndex.get(key)) {
				entry.getDoc().setLength(entry.getDoc().getLength() + Math.pow(entry.getTf() * idfs.get(key).doubleValue(), 2));
			}
		}
		
		return idfs;
	}

	/**
	 * computes the inverted index over the corpus of all given documents
	 * 
	 * @param requestingUserItems the items of the requesting user
	 * @param userItems the items of the similar users
	 * @param saveDocuments  a list of documents with their lengths
	 * @return the inverted index
	 */
	public Map<String, List<IndexEntry<? extends Resource>>> calculateInvertedIndex(final List<Post<?>> requestingUserItems, final List<Post<R>> userItems, final Map<String, Document<? extends Resource>> saveDocuments) {
		final Map<String, List<IndexEntry<? extends Resource>>> invertedIndex = new HashMap<String, List<IndexEntry<? extends Resource>>>();
		
		for (final Post<? extends Resource> item : requestingUserItems) {
			final Document<? extends Resource> doc = createDoc(item);
			saveDocuments.put(String.valueOf(doc.getItem().getContentId()), doc); // TODO: remove valueOf
			final Map<String, IndexEntry<? extends Resource>> documentEntries = calculateIndexEntries(doc);
			for (String key : documentEntries.keySet()) {
				if (invertedIndex.containsKey(key)) {
					invertedIndex.get(key).add(documentEntries.get(key));
				} else {
					invertedIndex.put(key, new ArrayList<IndexEntry<? extends Resource>>());
					invertedIndex.get(key).add(documentEntries.get(key));
				}
			}
		}
		
		for (final Post<? extends Resource> item : userItems) {
			final Document<? extends Resource> doc = createDoc(item);
			saveDocuments.put(String.valueOf(doc.getItem().getContentId()), doc); // TODO: remove valueOf
			final Map<String, IndexEntry<? extends Resource>> documentEntries = calculateIndexEntries(doc);
			for (String key : documentEntries.keySet()) {
				if (invertedIndex.containsKey(key)) {
					invertedIndex.get(key).add(documentEntries.get(key));
				} else {
					invertedIndex.put(key, new ArrayList<IndexEntry<? extends Resource>>());
					invertedIndex.get(key).add(documentEntries.get(key));
				}
			}
		}
		
		for (String key : saveDocuments.keySet()) {
			saveDocuments.get(key).setLength(Math.sqrt(saveDocuments.get(key).getLength()));
		}
		
		return invertedIndex;
	}

	/**
	 * @param item
	 * @return
	 */
	private <T extends Resource> Document<T> createDoc(Post<T> item) {
		return new Document<T>(item);
	}

	/**
	 * calculates a list of index entries which shall be added to the inverted index
	 * 
	 * @param doc the document to calculate the list for
	 *  
	 * @return a map which maps tokens to index entries, which shall be added to the inverted index
	 */
	protected <T extends Resource> Map<String, IndexEntry<? extends Resource>> calculateIndexEntries(final Document<T> doc) {
		final Map<String, Integer> alreadyAdded = new HashMap<String, Integer>();
		
		// for each token add a new entry or increase the entries tf
		for (String token : this.calculateTokens(doc.getItem())) {
			if (alreadyAdded.keySet().contains(token)) {
				alreadyAdded.put(token, Integer.valueOf(alreadyAdded.get(token).intValue() + 1));
			} else {
				alreadyAdded.put(token, Integer.valueOf(1));
			}
		}
		
		int max = 0;
		for (String key : alreadyAdded.keySet()) {
			int toCheck = alreadyAdded.get(key).intValue();
			if (toCheck > max) {
				max = toCheck;
			}
		}
		
		// set correct tfs by normalizing
		final Map<String, IndexEntry<? extends Resource>> results = new HashMap<String, IndexEntry<? extends Resource>>();
		for (String key : alreadyAdded.keySet()) {
			results.put(key, new IndexEntry<T>(doc, alreadyAdded.get(key).doubleValue() / max));
		}
		
		return results;
	}
	
	/**
	 * calculates the sum of similarities of the item toCheck to the items of the requesting user
	 * 
	 * @param toCheck the item to calculate the similarity to the requesting user's items for
	 * @param postsOfUser 
	 * @param requestingUserTitles a set of all titles belonging to the requesting user
	 * @param invertedIndex 
	 * @param idfs a map which maps tokens to their idfs
	 * @param saveDocuments a list of documents with their lengths
	 * 
	 * @return a similarity value -> the bigger the better
	 */
	protected double calculateSimilarity(final Post<R> toCheck, final List<Post<?>> postsOfUser, final Set<String> requestingUserTitles, final Map<String, List<IndexEntry<? extends Resource>>> invertedIndex, final Map<String, Double> idfs, final Map<String, Document<? extends Resource>> saveDocuments) {
		// FIXME: do not use the title to check for duplicates use the intrahash
		// avoid recommendation of known items
		// FIXME: even returning a similarity here has no effect that the post is not recommended (?)
		if (requestingUserTitles.contains(toCheck.getResource().getTitle())) {
			return 0.0;
		}
		
		double similarity = 0.0;
		
		final Map<Integer, Double> similarities = new HashMap<Integer, Double>();
		
		// maps tokens to it's weights
		final Map<String, Double> tokens = new HashMap<String, Double>();
		int max = 1;
		for (String token : this.calculateTokens(toCheck)) {
			token = token.toLowerCase();
			if (tokens.keySet().contains(token)) {
				double newWeight = tokens.get(token).doubleValue();
				newWeight += 1;
				tokens.put(token, Double.valueOf(newWeight));
				if (newWeight > max) {
					max++; // TODO: check!
				}
			} else {
				tokens.put(token, Double.valueOf(1));
			}
		}
		
		for (final Entry<String, Double> tokenEntry : tokens.entrySet()) {
			tokens.put(tokenEntry.getKey(), Double.valueOf(tokenEntry.getValue().doubleValue() / max));
		}
		
		for (final Entry<String, Double> tokenEntry : tokens.entrySet()) {
			final String token = tokenEntry.getKey();
			final double value = tokenEntry.getValue().doubleValue();
			
			for (final IndexEntry<? extends Resource> entry : invertedIndex.get(token)) {
				// if the document doesn't belong to the requesting user skip it since we dan't want to measure similarity
				// of the items of the requesting user
				// FIXME: post does not override equals!
				final Post<? extends Resource> post = entry.getDoc().getItem();
				if (!postsOfUser.contains(post)) {
					continue;
				}
				
				final double idf = idfs.get(token).doubleValue();
				final Double calcSim = Double.valueOf((entry.getTf() * idf) * (value * idf));
				
				if (similarities.containsKey(entry.getDoc().getItem().getContentId()) && !entry.getDoc().getItem().getContentId().equals(toCheck.getContentId())) {
					similarities.put(entry.getDoc().getItem().getContentId(), calcSim);
				} else if (!similarities.containsKey(entry.getDoc().getItem().getContentId()) && !entry.getDoc().getItem().getContentId().equals(toCheck.getContentId())) {
					similarities.put(entry.getDoc().getItem().getContentId(), calcSim);
				}
			}
		}
		
		// prevent division by zero if no item containing a token was found
		if (similarities.size() == 0) {
			return 0.0;
		}
		
		final double toCheckLength = saveDocuments.get(String.valueOf(toCheck.getContentId())).getLength();
		for (final Integer item : similarities.keySet()) {
			similarities.put(item, similarities.get(item)/(saveDocuments.get(String.valueOf(item)).getLength()*toCheckLength));
		}
		
		for (final Integer key : similarities.keySet()) {
			similarity += similarities.get(key);
		}
		
		similarity /= similarities.size();
		
		return similarity;
	}

	/**
	 * Helper method for extracting tokens out of an item.
	 * 
	 * @param item the item to extract the tokens out of
	 * @return a list of tokens
	 */
	protected List<String> calculateTokens(Post<? extends Resource> item) {
		final List<String> tokens = new ArrayList<String>();
		
		for (Tag tag : item.getTags()) {
			tokens.add(tag.getName().toLowerCase());
		}
		for (String titleToken : item.getResource().getTitle().split(TOKEN_DELIMITER)) {
			tokens.add(titleToken.toLowerCase());
		}
		
		return tokens;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.recommender.item.AbstractItemRecommender#setFeedbackInternal(org.bibsonomy.recommender.item.model.RecommendationUser, org.bibsonomy.recommender.item.model.RecommendedPost)
	 */
	@Override
	protected void setFeedbackInternal(RecommendationUser entity, RecommendedPost<R> item) {
		// no feedback handling
	}

	
	/**
	 * private class as inverted index helper
	 * 
	 * @author lukas
	 * @param <R> 
	 *
	 */
	protected static class IndexEntry<R extends Resource> {
		
		private Document<R> doc;
		private double tf;
		
		public IndexEntry(Document<R> doc, double tf) {
			this.doc = doc;
			this.tf = tf;
		}
		
		public Document<R> getDoc() {
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
	 * @param <R> 
	 *
	 */
	protected static class Document<R extends Resource> {
		
		private double length;
		private Post<R> item;
		
		public Document(Post<R> item) {
			this.item = item;
			this.length = 0.0;
		}
		
		public Post<R> getItem() {
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
