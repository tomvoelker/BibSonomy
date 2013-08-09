package org.bibsonomy.recommender.connector.filter;

import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.bibsonomy.recommender.connector.model.UserWrapper;

import recommender.core.interfaces.filter.PrivacyFilter;
import recommender.core.interfaces.model.TagRecommendationEntity;

public class PostPrivacyFilter implements PrivacyFilter<TagRecommendationEntity> {

	@SuppressWarnings("unchecked")
	@Override
	public TagRecommendationEntity filterEntity(TagRecommendationEntity post) {
		
		final Set<Group> groups;
		if(post instanceof PostWrapper<?>) {
			groups = ((PostWrapper<Resource>) post).getPost().getGroups();
		} else {
			groups = null;
		}
		
		if (groups == null || !groups.contains(GroupUtils.getPublicGroup())) {
			/*
			 * The post does not contain the public group -> no parts of it
			 * are public.
			 */
			// FIXME: THIS IS BROKEN! FOR PUBLIC POSTS, THE CONDITION ABOVE EVALUATES TO TRUE
			return null;
			// return post;
		}
		
		Post<Resource> existingPost = null;
		
		if(post instanceof PostWrapper<?>) {
			existingPost = ((PostWrapper<Resource>) post).getPost();
		}
		
		/*
		 * resource
		 */
		if (existingPost.getResource() instanceof BibTex) {
			/*
			 * create a copy of the post which is returned
			 */
			final Post<BibTex> postCopy = new Post<BibTex>();
			if (post.getUser() instanceof UserWrapper) {
				postCopy.setUser(((UserWrapper) post.getUser()).getUser());
			}
			postCopy.setContentId(Integer.parseInt(post.getId()));
			
			/*
			 * bibtex
			 */
			final BibTex bibtex = (BibTex) existingPost.getResource();
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
			
			postCopy.setResource(bibtexCopy);
			
			/*
			 * new hashes
			 */
			existingPost.getResource().recalculateHashes();
			postCopy.getResource().recalculateHashes();

			return new PostWrapper<BibTex>(postCopy);
		} else if (existingPost.getResource() instanceof Bookmark) {
			/*
			 * create a copy of the post which is returned
			 */
			final Post<Bookmark> postCopy = new Post<Bookmark>();
			if (post.getUser() instanceof UserWrapper) {
				postCopy.setUser(((UserWrapper) post.getUser()).getUser());
			}
			postCopy.setContentId(Integer.parseInt(post.getId()));
			
			/*
			 * bookmark
			 */
			final Bookmark bookmark = (Bookmark) existingPost.getResource();
			final Bookmark bookmarkCopy = new Bookmark();
			
			bookmarkCopy.setTitle(bookmark.getTitle());
			bookmarkCopy.setUrl(bookmark.getUrl());
			
			postCopy.setResource(bookmarkCopy);
			
			/*
			 * new hashes
			 */
			existingPost.getResource().recalculateHashes();
			postCopy.getResource().recalculateHashes();

			return new PostWrapper<Bookmark>(postCopy);
		}
		
		return null;
		
	}

}
