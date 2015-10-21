package org.bibsonomy.search.es.index;

import java.net.URI;
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
import org.bibsonomy.search.es.ESConstants.Fields;

/**
 * TODO: add documentation to this class
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

	@Override
	public Post<R> convert(final Map<String, Object> source) {
		// TODO: implement me
		final Post<R> post = new Post<>();
		
		if (source.containsKey(Fields.SYSTEM_URL)) {
			final String systemUrl = source.get(Fields.SYSTEM_URL).toString();
			post.setSystemUrl(systemUrl);
		}
		
		this.convertPostInternal(source, post);
		return post;
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
		
		jsonDocument.put(Fields.CONTENT_ID, post.getContentId());
		jsonDocument.put(Fields.DATE, post.getDate());
		jsonDocument.put(Fields.CHANGE_DATE, post.getChangeDate());
		
		jsonDocument.put(Fields.DESCRIPTION, post.getDescription());
		
		jsonDocument.put(Fields.USER_NAME, post.getUser().getName());
		
		jsonDocument.put(Fields.GROUPS, convertGroups(post.getGroups()));
		
		jsonDocument.put(Fields.TAGS, convertTags(post.getTags()));
		jsonDocument.put(Fields.SYSTEM_URL, this.systemURI);
		
		this.convertResourceInternal(jsonDocument, post.getResource());
		this.convertPostInternal(post, jsonDocument);
		return jsonDocument;
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
