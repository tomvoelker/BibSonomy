/**
 * BibSonomy-Recommendation-Connector - Connector for the recommender framework for tag and resource recommendation
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
package org.bibsonomy.recommender.tag.filter;

import java.util.Set;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.GroupUtils;

import recommender.core.interfaces.filter.PrivacyFilter;

/**
 * 
 * @author fei, rja
 */
public class PostPrivacyFilter implements PrivacyFilter<Post<? extends Resource>> {

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
	public Post<? extends Resource> filterEntity(final Post<? extends Resource> post) {
		// in case of this is not a BibSonomy model type we can't filter
		if (post == null) {
			return post;
		}
		
		final Set<Group> groups = post.getGroups();
		
		if (groups == null || !groups.contains(GroupUtils.buildPublicGroup())) {
			/*
			 * The post does not contain the public group -> no parts of it
			 * are public.
			 */
			return null;
		}
		
		/*
		 * resource
		 */
		if (post.getResource() instanceof BibTex) {
			/*
			 * create a copy of the post which is returned
			 */
			final Post<BibTex> postCopy = new Post<BibTex>();
			postCopy.setUser(post.getUser());
			
			postCopy.setContentId(post.getContentId());
			
			/*
			 * bibtex
			 */
			final BibTex bibtex = (BibTex) post.getResource();
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
			post.getResource().recalculateHashes();
			postCopy.getResource().recalculateHashes();

			return postCopy;
		} else if (post.getResource() instanceof Bookmark) {
			/*
			 * create a copy of the post which is returned
			 */
			final Post<Bookmark> postCopy = new Post<Bookmark>();
			postCopy.setUser(post.getUser());
			postCopy.setContentId(post.getContentId());
			
			/*
			 * bookmark
			 */
			final Bookmark bookmark = (Bookmark) post.getResource();
			final Bookmark bookmarkCopy = new Bookmark();
			
			bookmarkCopy.setTitle(bookmark.getTitle());
			bookmarkCopy.setUrl(bookmark.getUrl());
			
			postCopy.setResource(bookmarkCopy);
			
			/*
			 * new hashes
			 */
			post.getResource().recalculateHashes();
			postCopy.getResource().recalculateHashes();

			return postCopy;
		}
		
		return null;
	}

}
