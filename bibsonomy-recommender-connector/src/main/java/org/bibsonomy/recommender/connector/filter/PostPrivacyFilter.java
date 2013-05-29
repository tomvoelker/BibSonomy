package org.bibsonomy.recommender.connector.filter;

import java.util.HashSet;
import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.recommender.connector.model.BibTexWrapper;
import org.bibsonomy.recommender.connector.model.BookmarkWrapper;
import org.bibsonomy.recommender.connector.model.GroupWrapper;
import org.bibsonomy.recommender.connector.model.PostWrapper;

import recommender.core.interfaces.filter.PrivacyFilter;
import recommender.core.interfaces.model.RecommendationGroup;
import recommender.core.interfaces.model.RecommendationResource;
import recommender.core.interfaces.model.TagRecommendationEntity;

public class PostPrivacyFilter implements PrivacyFilter {

	@Override
	public TagRecommendationEntity filterPost(TagRecommendationEntity post) {
		
		Set<Group> groups = new HashSet<Group>();
		for(RecommendationGroup group : post.getGroups()) {
			if(!(group instanceof GroupWrapper)) {
				return null;
			} else {
				groups.add(((GroupWrapper) group).getGroup());
			}
		}
		
		if (!groups.contains(GroupUtils.getPublicGroup())) {
			/*
			 * The post does not contain the public group -> no parts of it
			 * are public.
			 */
			// FIXME: THIS IS BROKEN! FOR PUBLIC POSTS, THE CONDITION ABOVE EVALUATES TO TRUE
			return null;
			// return post;
		}
		
		/*
		 * create a copy of the post which is returned
		 */
		/*
		 * post
		 */
		final PostWrapper<Resource> postCopy = new PostWrapper<Resource>(new Post<Resource>());
		postCopy.setUser(post.getUser());
		postCopy.setDate(post.getDate());
		postCopy.setContentId(post.getContentId());
		postCopy.setDescription(post.getDescription());
		postCopy.setGroups(post.getGroups());
		postCopy.setTags(post.getTags());
		/*
		 * resource
		 */
		final RecommendationResource resource = post.getResource();
		if (resource instanceof BibTexWrapper) {
			/*
			 * bibtex
			 */
			final BibTex bibtex = ((BibTexWrapper) resource).getBibtex();
			final BibTex bibtexCopy = new BibTex();
			
			bibtexCopy.setAbstract(bibtex.getAbstract());
			bibtexCopy.setAddress(bibtex.getAddress());
			bibtexCopy.setAnnote(bibtex.getAnnote());
			bibtexCopy.setAuthor(bibtex.getAuthor());
			bibtexCopy.setBibtexKey(bibtex.getBibtexKey());
			bibtexCopy.setBooktitle(bibtex.getBooktitle());
			bibtexCopy.setChapter(bibtex.getChapter());
			bibtexCopy.setCrossref(bibtex.getCrossref());
			bibtexCopy.setDay(bibtex.getDay());
			bibtexCopy.setEdition(bibtex.getEdition());
			bibtexCopy.setEditor(bibtex.getEditor());
			bibtexCopy.setEntrytype(bibtex.getEntrytype());
			bibtexCopy.setHowpublished(bibtex.getHowpublished());
			bibtexCopy.setInstitution(bibtex.getInstitution());
			bibtexCopy.setJournal(bibtex.getJournal());
			bibtexCopy.setMisc(bibtex.getMisc());
			bibtexCopy.setMonth(bibtex.getMonth());
			bibtexCopy.setNote(bibtex.getNote());
			bibtexCopy.setNumber(bibtex.getNumber());
			bibtexCopy.setOrganization(bibtex.getOrganization());
			bibtexCopy.setPages(bibtex.getPages());
			bibtexCopy.setPrivnote(bibtex.getPrivnote());
			bibtexCopy.setPublisher(bibtex.getPublisher());
			bibtexCopy.setSchool(bibtex.getSchool());
			bibtexCopy.setSeries(bibtex.getSeries());
			bibtexCopy.setTitle(bibtex.getTitle());
			bibtexCopy.setType(bibtex.getType());
			bibtexCopy.setUrl(bibtex.getUrl());
			bibtexCopy.setVolume(bibtex.getVolume());
			bibtexCopy.setYear(bibtex.getYear());
			
			postCopy.setResource(new BibTexWrapper(bibtexCopy));
		} else if (resource instanceof Bookmark) {
			/*
			 * bookmark
			 */
			final Bookmark bookmark = ((BookmarkWrapper) resource).getBookmark();
			final Bookmark bookmarkCopy = new Bookmark();
			
			bookmarkCopy.setTitle(bookmark.getTitle());
			bookmarkCopy.setUrl(bookmark.getUrl());
			
			postCopy.setResource(new BookmarkWrapper(bookmarkCopy));
		}
		
		
		/*
		 * new hashes
		 */
		post.getResource().recalculateHashes();
		postCopy.getResource().recalculateHashes();

		return postCopy;
	}

}
