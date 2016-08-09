/**
 * BibSonomy-Rest-Server - The REST-server.
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.renderer.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.LayoutRenderingException;
import org.bibsonomy.layout.jabref.AbstractJabRefLayout;
import org.bibsonomy.layout.jabref.JabRefConfig;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.util.data.DataAccessor;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.Renderer;
import org.bibsonomy.services.URLGenerator;

/**
 * @author rja
 */
public class JabrefLayoutRenderer implements Renderer {
	
	public static final String LAYOUT_SIMPLEHTML = "simplehtml";
	
	private static final Log log = LogFactory.getLog(JabrefLayoutRenderer.class);
	/*
	 * FIXME: proper initialization!
	 * (e.g., missing UrlGenerator)
	 */
	private final org.bibsonomy.layout.jabref.JabrefLayoutRenderer renderer; 
	
	private final AbstractJabRefLayout layout;
	
	/**
	 * @param urlGenerator - the class to generate proper URLs
	 * @param layout - the jabrefLayout used by the renderer
	 * @throws Exception 
	 */
	public JabrefLayoutRenderer(final URLGenerator urlGenerator, final String layout) throws Exception {
		super();
		final JabRefConfig config = new JabRefConfig();
		config.setDefaultLayoutFilePath("org/bibsonomy/layout/jabref");
		this.renderer = new org.bibsonomy.layout.jabref.JabrefLayoutRenderer(config);
		this.renderer.setUrlGenerator(urlGenerator);
		
		try {
			this.layout = this.renderer.getLayout(layout, null);
		} catch (Exception ex) {
			log.error(ex);
			throw new InternServerException(ex);
		}
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
	public Post<? extends Resource> parsePost(final Reader reader, DataAccessor uploadedFileAcessor) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Post<? extends Resource>> parsePostList(final Reader reader, DataAccessor uploadedFileAcessor) throws BadRequestOrResponseException {
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
		final boolean embeddedLayout = true;
		try {
			writer.append(renderer.renderLayout(layout, posts, embeddedLayout));
			writer.flush();
		} catch (final LayoutRenderingException ex) {
			throw new InternServerException(ex);
		} catch (final IOException ex) {
			throw new InternServerException(ex);
		}
	}
	
	@Override
	public void serializeDocument(Writer writer, Document document) {
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

	@Override
	public void serializeReference(final Writer writer, final String referenceHash) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Document parseDocument(Reader reader, DataAccessor uploadFileAccessor) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}
}
