package org.bibsonomy.rest.renderer.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.JabrefLayout;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.services.URLGenerator;

/**
 * @author rja
 * @version $Id$
 */
public class JabrefLayoutRenderer implements Renderer {

	/*
	 * FIXME: proper initialization!
	 * (e.g., missing UrlGenerator)
	 */
	private final org.bibsonomy.layout.jabref.JabrefLayoutRenderer renderer; 
	
	/**
	 * @param urlGenerator - the class to generate proper URLs
	 */
	public JabrefLayoutRenderer(final URLGenerator urlGenerator) {
		super();
		this.renderer = new org.bibsonomy.layout.jabref.JabrefLayoutRenderer();
		this.renderer.setUrlGenerator(urlGenerator);
	}

	@Override
	public String parseError(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public Group parseGroup(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public String parseGroupId(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public List<Group> parseGroupList(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public Post<? extends Resource> parsePost(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public List<Post<? extends Resource>> parsePostList(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public RecommendedTag parseRecommendedTag(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public SortedSet<RecommendedTag> parseRecommendedTagList(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public Set<String> parseReferences(final Reader reader) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public String parseResourceHash(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public Post<? extends Resource> parseCommunityPost(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public String parseStat(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public Tag parseTag(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public List<Tag> parseTagList(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public User parseUser(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public String parseUserId(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public List<User> parseUserList(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void serializeError(final Writer writer, final String errorMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serializeFail(final Writer writer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serializeGroup(final Writer writer, final Group group, final ViewModel viewModel) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void serializeGroupId(final Writer writer, final String groupId) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void serializeGroups(final Writer writer, final List<Group> groups, final ViewModel viewModel) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void serializeOK(final Writer writer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void serializePost(final Writer writer, final Post<? extends Resource> post, final ViewModel model) {
		serializePosts(writer, Collections.singletonList(post), model);
		
	}

	@Override
	public void serializePosts(final Writer writer, final List<? extends Post<? extends Resource>> posts, final ViewModel viewModel) throws InternServerException {
		/*
		 * FIXME: proper layout selection
		 */
		final boolean embeddedLayout = true;
		try {
			final JabrefLayout layout = renderer.getLayout("simplehtml", null);
			writer.append(renderer.renderLayout(layout, posts, embeddedLayout));
		} catch (final LayoutRenderingException ex) {
			throw new InternServerException(ex);
		} catch (final IOException ex) {
			throw new InternServerException(ex);
		}
	}

	@Override
	public void serializeRecommendedTag(final Writer writer, final RecommendedTag tag) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void serializeRecommendedTags(final Writer writer, final Collection<RecommendedTag> tags) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void serializeResourceHash(final Writer writer, final String hash) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void serializeTag(final Writer writer, final Tag tag, final ViewModel viewModel) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void serializeTags(final Writer writer, final List<Tag> tags, final ViewModel viewModel) {
		throw new UnsupportedOperationException();	
	}

	@Override
	public void serializeURI(final Writer writer, final String uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void serializeUser(final Writer writer, final User user, final ViewModel viewModel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void serializeUserId(final Writer writer, final String userId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void serializeUsers(final Writer writer, final List<User> users, final ViewModel viewModel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void serializeSynchronizationPosts(final Writer writer, final List<? extends SynchronizationPost> posts) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SynchronizationPost> parseSynchronizationPostList(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void serializeSynchronizationData(final Writer writer, final SynchronizationData syncData) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SynchronizationData parseSynchronizationData(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

}
