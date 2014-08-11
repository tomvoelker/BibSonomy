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
import org.bibsonomy.recommender.connector.utilities.RecommendationUtilities;

import recommender.core.interfaces.filter.PrivacyFilter;
import recommender.core.interfaces.model.TagRecommendationEntity;

/**
 * 
 * @author fei, rja
 */
public class PostPrivacyFilter implements PrivacyFilter<TagRecommendationEntity> {

	/**
	 * The methods checks if the post wrapped in an entity can be forwarded
	 * to external services. If not, <code>null</code> is returned. 
	 * Otherwise, a copy of the post is returned where only the public fields are set. 
	 * Note that this is not necessarily a deep copy, i.e., the tags are not copied
	 * but just linked.
	 * 
	 * <p>We do white listing here, i.e., we explicitly state, which attributes
	 * to copy.</p>
	 * 
	 * @param post the entity to filter
	 * @return The wrapped post containing only public parts or <code>null</code>, if
	 * the post is not public at all.
	 */
	@Override
	public TagRecommendationEntity filterEntity(final TagRecommendationEntity post) {
		final Post<? extends Resource> existingPost = RecommendationUtilities.unwrapTagRecommendationEntity(post);
		
		// in case of this is not a BibSonomy model type we can't filter
		if (post == null) {
			return post;
		}
		
		final Set<Group> groups = existingPost.getGroups();
		
		if (groups == null || !groups.contains(GroupUtils.getPublicGroup())) {
			/*
			 * The post does not contain the public group -> no parts of it
			 * are public.
			 */
			return null;
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
			
			postCopy.setContentId(new Integer(post.getId()));
			
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
