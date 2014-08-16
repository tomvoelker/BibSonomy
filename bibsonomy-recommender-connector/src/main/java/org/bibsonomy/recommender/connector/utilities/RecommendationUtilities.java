package org.bibsonomy.recommender.connector.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.comparators.RecommendedTagComparator;
import org.bibsonomy.recommender.connector.model.RecommendationPost;

import recommender.core.interfaces.model.RecommendationItem;
import recommender.impl.model.RecommendedItem;

/**
 * This class provides procedures for the conversion of BibSonomy recommended
 * tags to the library internal representation and vice versa.
 * 
 * @author lukas
 *
 */
public class RecommendationUtilities {

	/**
	 * this method converts recommended tags from the library to BibSonomy's representation.
	 * 
	 * @param tags the library's recommended tag representations
	 * @return the BibSonomy's recommended tag representations
	 */
	public static SortedSet<RecommendedTag> getRecommendedTags(final SortedSet<recommender.impl.model.RecommendedTag> tags) {
		SortedSet<RecommendedTag> bibRecTags = new TreeSet<RecommendedTag>(new RecommendedTagComparator());
		for(recommender.impl.model.RecommendedTag tag : tags) {
			RecommendedTag toAdd = new RecommendedTag(tag.getName(), tag.getScore(), tag.getConfidence());
			bibRecTags.add(toAdd);
		}
		return bibRecTags;
	}
	
	/**
	 * this method converts BibSonomy's recommended tags to the library's internal representation.
	 * 
	 * @param tags the BibSonomy recommended tag representations
	 * @return the library's internal representations
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static SortedSet<recommender.impl.model.RecommendedTag> getRecommendedTagsFromBibRecTags(final SortedSet<RecommendedTag> tags) {
		SortedSet<recommender.impl.model.RecommendedTag> recTags = new TreeSet<recommender.impl.model.RecommendedTag>(new recommender.core.util.RecommendationResultComparator());
		for(RecommendedTag tag : tags) {
			recommender.impl.model.RecommendedTag toAdd = new recommender.impl.model.RecommendedTag(tag.getName(), tag.getScore(), tag.getConfidence());
			recTags.add(toAdd);
		}
		return recTags;
	}
	
	/**
	 * Wraps a list of {@link Post}s to a list of {@link RecommendationItem}s.
	 * 
	 * @param posts the posts to wrap
	 * @return a list of {@link RecommendationItem}s containing the posts
	 */
	public static List<RecommendationItem> wrapPostList(final List<Post<? extends Resource>> posts) {
		final List<RecommendationItem> items = new ArrayList<RecommendationItem>();
		for(Post<? extends Resource> post : posts) {
			items.add(new RecommendationPost(post));
		}
		return items;
	}
	
	/**
	 * Helper method for unwrapping {@link RecommendedItem}s containing BibSonomy {@link Post}s.
	 * 
	 * @param resourceType the type of the corresponding resources to unwrap
	 * @param items a set of all items to unwrap
	 * 
	 * @return a list of all unwrapped posts
	 */
	@SuppressWarnings({ "unchecked" })
	public static <T extends Resource> List<Post<T>> unwrapRecommendedItems(final Class<T> resourceType, final SortedSet<RecommendedItem> items) {
		final List<Post<T>> posts = new ArrayList<Post<T>>();
		for(RecommendedItem item : items) {
			if(item.getItem() != null && item.getItem() instanceof RecommendationPost
					&& ((RecommendationPost) item.getItem()).getPost() != null
					&& ((RecommendationPost) item.getItem()).getPost().getResource() != null
					&& resourceType.isAssignableFrom(((RecommendationPost) item.getItem()).getPost().getResource().getClass())) {
				posts.add((Post<T>)((RecommendationPost) item.getItem()).getPost()); 
			}
		}
		return posts; 
	}
}
