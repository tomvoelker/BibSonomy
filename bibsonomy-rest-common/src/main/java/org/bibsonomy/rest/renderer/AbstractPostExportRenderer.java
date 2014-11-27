/**
 * BibSonomy-Rest-Common - Common things for the REST-client and server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.rest.renderer;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
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
import org.bibsonomy.rest.exceptions.UnsupportedMediaTypeException;

/**
 * abstract renderer implementation for exporting posts
 * 
 * @author dzo
 */
public abstract class AbstractPostExportRenderer implements Renderer {
	private static final Log log = LogFactory.getLog(AbstractPostExportRenderer.class);
	
	/** the new line charater */
	protected static final char NEW_LINE = '\n';
	
	/**
	 * @return the format this PostExportRenderer renders
	 */
	protected abstract RenderingFormat getFormat();

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeDocument(java.io.Writer, org.bibsonomy.model.Document)
	 */
	@Override
	public void serializeDocument(Writer writer, Document document) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeUsers(java.io.Writer, java.util.List, org.bibsonomy.rest.ViewModel)
	 */
	@Override
	public void serializeUsers(Writer writer, List<User> users, ViewModel viewModel) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeUser(java.io.Writer, org.bibsonomy.model.User, org.bibsonomy.rest.ViewModel)
	 */
	@Override
	public void serializeUser(Writer writer, User user, ViewModel viewModel) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeTags(java.io.Writer, java.util.List, org.bibsonomy.rest.ViewModel)
	 */
	@Override
	public void serializeTags(Writer writer, List<Tag> tags, ViewModel viewModel) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeTag(java.io.Writer, org.bibsonomy.model.Tag, org.bibsonomy.rest.ViewModel)
	 */
	@Override
	public void serializeTag(Writer writer, Tag tag, ViewModel viewModel) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeGroups(java.io.Writer, java.util.List, org.bibsonomy.rest.ViewModel)
	 */
	@Override
	public void serializeGroups(Writer writer, List<Group> groups, ViewModel viewModel) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeGroup(java.io.Writer, org.bibsonomy.model.Group, org.bibsonomy.rest.ViewModel)
	 */
	@Override
	public void serializeGroup(Writer writer, Group group, ViewModel viewModel) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeError(java.io.Writer, java.lang.String)
	 */
	@Override
	public void serializeError(Writer writer, String errorMessage) {
		try {
			writer.append(errorMessage);
		} catch (IOException e) {
			log.error("cannot serialize error message '" + errorMessage + "'");
			throw new InternServerException(e.toString());
		}
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeOK(java.io.Writer)
	 */
	@Override
	public void serializeOK(Writer writer) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeFail(java.io.Writer)
	 */
	@Override
	public void serializeFail(Writer writer) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeResourceHash(java.io.Writer, java.lang.String)
	 */
	@Override
	public void serializeResourceHash(Writer writer, String hash) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeUserId(java.io.Writer, java.lang.String)
	 */
	@Override
	public void serializeUserId(Writer writer, String userId) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeURI(java.io.Writer, java.lang.String)
	 */
	@Override
	public void serializeURI(Writer writer, String uri) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeGroupId(java.io.Writer, java.lang.String)
	 */
	@Override
	public void serializeGroupId(Writer writer, String groupId) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeSynchronizationPosts(java.io.Writer, java.util.List)
	 */
	@Override
	public void serializeSynchronizationPosts(Writer writer, List<? extends SynchronizationPost> posts) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeSynchronizationData(java.io.Writer, org.bibsonomy.model.sync.SynchronizationData)
	 */
	@Override
	public void serializeSynchronizationData(Writer writer, SynchronizationData syncData) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#serializeReference(java.io.Writer, java.lang.String)
	 */
	@Override
	public void serializeReference(Writer writer, String referenceHash) {
		this.handleUnsupportedMediaType();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseSynchronizationData(java.io.Reader)
	 */
	@Override
	public SynchronizationData parseSynchronizationData(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseSynchronizationPostList(java.io.Reader)
	 */
	@Override
	public List<SynchronizationPost> parseSynchronizationPostList(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseError(java.io.Reader)
	 */
	@Override
	public String parseError(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseResourceHash(java.io.Reader)
	 */
	@Override
	public String parseResourceHash(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseUserId(java.io.Reader)
	 */
	@Override
	public String parseUserId(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseGroupId(java.io.Reader)
	 */
	@Override
	public String parseGroupId(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseStat(java.io.Reader)
	 */
	@Override
	public String parseStat(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseUserList(java.io.Reader)
	 */
	@Override
	public List<User> parseUserList(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseUser(java.io.Reader)
	 */
	@Override
	public User parseUser(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parsePostList(java.io.Reader, org.bibsonomy.model.util.data.DataAccessor)
	 */
	@Override
	public List<Post<? extends Resource>> parsePostList(Reader reader, DataAccessor uploadedFileAcessor) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parsePost(java.io.Reader, org.bibsonomy.model.util.data.DataAccessor)
	 */
	@Override
	public Post<? extends Resource> parsePost(Reader reader, DataAccessor uploadedFileAccessor) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseDocument(java.io.Reader, org.bibsonomy.model.util.data.DataAccessor)
	 */
	@Override
	public Document parseDocument(Reader reader, DataAccessor uploadFileAccessor) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseCommunityPost(java.io.Reader)
	 */
	@Override
	public Post<? extends Resource> parseCommunityPost(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseGroupList(java.io.Reader)
	 */
	@Override
	public List<Group> parseGroupList(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseGroup(java.io.Reader)
	 */
	@Override
	public Group parseGroup(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseTagList(java.io.Reader)
	 */
	@Override
	public List<Tag> parseTagList(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseTag(java.io.Reader)
	 */
	@Override
	public Tag parseTag(Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.rest.renderer.Renderer#parseReferences(java.io.Reader)
	 */
	@Override
	public Set<String> parseReferences(Reader reader) {
		throw new UnsupportedOperationException();
	}

	protected final void handleUnsupportedMediaType() {
		throw new UnsupportedMediaTypeException(this.getFormat().getSubtype() + " format supports only publication resources");
	}
}
