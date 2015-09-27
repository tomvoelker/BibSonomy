package org.bibsonomy.search.es.index;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.ESConstants.Fields;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 * @param <R> 
 */
public abstract class ResourceConverter<R extends Resource> implements org.bibsonomy.search.util.ResourceConverter<R, Map<String, Object>> {
	
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
		
		jsonDocument.put(Fields.GROUPS, post.getGroups());
		jsonDocument.put(Fields.TAGS, post.getTags());
		jsonDocument.put(Fields.SYSTEM_URL, ""); // FIXME: projectHome
		
		this.convertResourceInternal(jsonDocument, post.getResource());
		this.convertPostInternal(post, jsonDocument);
		return jsonDocument;
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
