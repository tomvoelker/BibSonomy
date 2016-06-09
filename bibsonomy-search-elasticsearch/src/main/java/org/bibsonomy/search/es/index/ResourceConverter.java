/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search.es.index;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;

/**
 * abstract class to convert the model to the ES mapping
 *
 * @author dzo
 * @param <R> 
 */
public abstract class ResourceConverter<R extends Resource> implements org.bibsonomy.search.util.ResourceConverter<R, Map<String, Object>> {
	
	private final URI systemURI;
	
	/**
	 * @param systemURI
	 */
	public ResourceConverter(final URI systemURI) {
		this.systemURI = systemURI;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Post<R> convert(final Map<String, Object> source, Set<String> allowdUsersForDoc) {
		final Post<R> post = new Post<>();
		
		if (source.containsKey(Fields.SYSTEM_URL)) {
			final String systemUrl = source.get(Fields.SYSTEM_URL).toString();
			post.setSystemUrl(systemUrl);
		}
		
		post.setDate(parseDate(source, Fields.DATE));
		post.setChangeDate(parseDate(source, Fields.CHANGE_DATE));
		final String userName = (String) source.get(Fields.USER_NAME);
		final boolean loadDocuments = allowdUsersForDoc.contains(userName);
		fillUser(post, userName);
		post.setDescription((String) source.get(Fields.DESCRIPTION));
		
		post.setGroups(convertToGroups((List<String>) source.get(Fields.GROUPS)));
		
		// FIXME: hidden tags TODODZO
		post.setTags(onlyConvertTags(source));
		
		this.convertPostInternal(source, post);
		final R resource = this.createNewResource();
		
		resource.setInterHash((String) source.get(Fields.Resource.INTERHASH));
		resource.setIntraHash((String) source.get(Fields.Resource.INTRAHASH));
		resource.setTitle((String) source.get(Fields.Resource.TITLE));
		
		this.convertResourceInternal(resource, source, loadDocuments);
		
		post.setResource(resource);
		return post;
	}

	/**
	 * @param post
	 * @param userName
	 */
	protected void fillUser(final Post<R> post, final String userName) {
		post.setUser(new User(userName));
	}
	
	/**
	 * only convert the tags
	 * @param source
	 * @return the tags of the es document
	 */
	public Set<Tag> onlyConvertTags(final Map<String, Object> source) {
		return convertToTags((List<String>) source.get(Fields.TAGS));
	}
	
	/**
	 * @param object
	 * @return
	 */
	private static Set<Tag> convertToTags(List<String> tagsStringList) {
		final Set<Tag> tags = new HashSet<>();
		
		for (String tagString : tagsStringList) {
			tags.add(new Tag(tagString));
		}
		
		return tags;
	}

	/**
	 * @param resource
	 * @param source
	 * @param loadDocuments 
	 */
	protected abstract void convertResourceInternal(R resource, Map<String, Object> source, boolean loadDocuments);

	/**
	 * @return a new instance of a resource
	 */
	protected abstract R createNewResource();

	/**
	 * @param object
	 * @return
	 */
	private static Set<Group> convertToGroups(List<String> list) {
		final Set<Group> groups = new HashSet<>();
		
		for (final String groupString : list) {
			groups.add(new Group(groupString));
		}
		
		return groups;
	}

	/**
	 * @param source
	 * @param key
	 * @return the date
	 */
	protected static Date parseDate(Map<String, Object> source, String key) {
		final String dateAsString = (String) source.get(key);
		return ElasticsearchUtils.parseDate(dateAsString);
	}

	/**
	 * @param source
	 * @param post
	 */
	protected void convertPostInternal(Map<String, Object> source, Post<R> post) {
		// noop
	}

	@Override
	public Map<String, Object> convert(final Post<R> post) {
		final Map<String, Object> jsonDocument = new HashMap<>();
		
		jsonDocument.put(Fields.DATE, ElasticsearchUtils.dateToString(post.getDate()));
		jsonDocument.put(Fields.CHANGE_DATE, ElasticsearchUtils.dateToString(post.getChangeDate()));
		
		jsonDocument.put(Fields.DESCRIPTION, post.getDescription());
		
		fillIndexDocumentUser(post, jsonDocument);
		
		jsonDocument.put(Fields.GROUPS, convertGroups(post.getGroups()));
		
		jsonDocument.put(Fields.TAGS, convertTags(post.getTags()));
		jsonDocument.put(Fields.SYSTEM_URL, this.systemURI);
		
		this.convertResourceInternal(jsonDocument, post.getResource());
		this.convertPostInternal(post, jsonDocument);
		return jsonDocument;
	}

	/**
	 * @param post
	 * @param jsonDocument
	 */
	protected void fillIndexDocumentUser(final Post<R> post, final Map<String, Object> jsonDocument) {
		jsonDocument.put(Fields.USER_NAME, post.getUser().getName());
	}

	/**
	 * @param groups
	 * @return
	 */
	private static List<String> convertGroups(final Set<Group> groups) {
		final List<String> groupsAsString = new LinkedList<>();
		
		for (final Group group : groups) {
			groupsAsString.add(group.getName());
		}
		
		return groupsAsString;
	}

	/**
	 * @param set
	 * @return
	 */
	private static Set<String> convertTags(final Set<Tag> tags) {
		final Set<String> tagsAsString = new HashSet<>();
		
		for (final Tag tag : tags) {
			tagsAsString.add(tag.getName());
		}
		
		return tagsAsString;
	}

	/**
	 * @param post
	 * @param jsonDocument
	 */
	protected void convertPostInternal(Post<R> post, Map<String, Object> jsonDocument) {
		// noop
	}

	/**
	 * @param jsonDocument
	 * @param resource
	 */
	protected void convertResourceInternal(Map<String, Object> jsonDocument, R resource) {
		jsonDocument.put(Fields.Resource.TITLE, resource.getTitle());
		jsonDocument.put(Fields.Resource.INTRAHASH, resource.getIntraHash());
		jsonDocument.put(Fields.Resource.INTERHASH, resource.getInterHash());
		this.convertResource(jsonDocument, resource);
	}

	/**
	 * @param jsonDocument
	 * @param resource
	 */
	protected abstract void convertResource(Map<String, Object> jsonDocument, R resource);
}
