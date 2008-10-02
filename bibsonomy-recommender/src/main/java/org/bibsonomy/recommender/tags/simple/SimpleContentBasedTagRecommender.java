package org.bibsonomy.recommender.tags.simple;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.recommender.TagRecommender;
import org.bibsonomy.recommender.tags.simple.termprocessing.TermProcessingIterator;

/**
 * @author rja
 * @version $Id$
 */
public class SimpleContentBasedTagRecommender implements TagRecommender {

	/** Simply adds recommendations at end of list. 
	 * 
	 * @see org.bibsonomy.recommender.TagRecommender#addRecommendedTags(java.util.List, org.bibsonomy.model.Post)
	 */
	public void addRecommendedTags(List<Tag> recommendedTags, Post<? extends Resource> post) {
		recommendedTags.addAll(getRecommendedTags(post));
	}

	public String getInfo() {
		return "Simple content based recommender which extracts tags from title, description, URL.";
	}

	public List<Tag> getRecommendedTags(Post<? extends Resource> post) {
		final List<Tag> extracted = new LinkedList<Tag>();
		/*
		 * extract tags from title using Jens' Termprocessor.
		 */
		final Iterator<String> extractor = buildTagExtractionIterator(post.getResource().getTitle());
		/*
		 * add all extracted tags
		 */
		while(extractor.hasNext() == true) {
			extracted.add(new Tag(extractor.next()));
		}
		return extracted;
	}

	private Iterator<String> buildTagExtractionIterator(final String title) {
		final Scanner s = new Scanner(title);
		s.useDelimiter("([\\|/\\\\ \t;!,\\-:\\)\\(\\]\\[\\}\\{]+)|(\\.[\\t ]+)");
		return new TermProcessingIterator(s);
	}
	
}
