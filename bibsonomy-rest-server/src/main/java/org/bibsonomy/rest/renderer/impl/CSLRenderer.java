package org.bibsonomy.rest.renderer.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.SortedSet;

import net.sf.json.JSONSerializer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.layout.csl.CslModelConverter;
import org.bibsonomy.layout.csl.model.Record;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedPost;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.sync.SynchronizationData;
import org.bibsonomy.model.sync.SynchronizationPost;
import org.bibsonomy.model.util.data.DataAccessor;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.UnsupportedMediaTypeException;
import org.bibsonomy.rest.renderer.Renderer;

/**
 * @author wla
  */
public class CSLRenderer implements Renderer {

	private static final Log LOGGER = LogFactory.getLog(CSLRenderer.class);
	/** used for sending errors via "error : ..." */
	public static final String ERROR_MESSAGE_KEY = "error";

	private static final String BEGIN = "{\n";
	private static final String END = "\n}";
	private static final String DELIMITER = ",\n";

	@Override
	public void serializePosts(final Writer writer, final List<? extends Post<? extends Resource>> posts, final ViewModel viewModel) throws InternServerException {
		final ListIterator<? extends Post<? extends Resource>> iter = posts.listIterator();
		try {
			writer.append(BEGIN);

			while (iter.hasNext()) {
				final Post<? extends Resource> post = iter.next();
				writer.append("\"" + ((BibTex) post.getResource()).getIntraHash() + post.getUser().getName() + "\":");
				serializePost(writer, post, viewModel);
				if (iter.hasNext()) {
					writer.append(DELIMITER);
				}
			}

			writer.append(END);
			writer.flush();

		} catch (final IOException ex) {
			LOGGER.error(ex);
		}
	}

	@Override
	public void serializePost(final Writer writer, final Post<? extends Resource> post, final ViewModel model) {
		final Record record = CslModelConverter.convertPost(post);
		try {
			final String string = JSONSerializer.toJSON(record, CslModelConverter.getJsonConfig()).toString();
			writer.append(string);
			writer.flush();
		} catch (final IOException ex) {
			LOGGER.error(ex);
		}
	}
	
	@Override
	public void serializeDocument(Writer writer, Document newDocument) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeUsers(final Writer writer, final List<User> users, final ViewModel viewModel) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeUser(final Writer writer, final User user, final ViewModel viewModel) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeTags(final Writer writer, final List<Tag> tags, final ViewModel viewModel) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeTag(final Writer writer, final Tag tag, final ViewModel viewModel) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeRecommendedTags(final Writer writer, final Collection<RecommendedTag> tags) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeRecommendedTag(final Writer writer, final RecommendedTag tag) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeGroups(final Writer writer, final List<Group> groups, final ViewModel viewModel) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeGroup(final Writer writer, final Group group, final ViewModel viewModel) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeError(final Writer writer, final String errorMessage) {
		final HashMap<String, String> errorMsg = new HashMap<String, String>();
		errorMsg.put(ERROR_MESSAGE_KEY, errorMessage);
		try {
			final String string = JSONSerializer.toJSON(errorMsg, CslModelConverter.getJsonConfig()).toString();
			writer.append(string);
			writer.flush();
		} catch (final IOException ex) {
			LOGGER.error(ex);
		}
	}

	@Override
	public void serializeOK(final Writer writer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void serializeFail(final Writer writer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void serializeResourceHash(final Writer writer, final String hash) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeUserId(final Writer writer, final String userId) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeURI(final Writer writer, final String uri) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeGroupId(final Writer writer, final String groupId) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeSynchronizationPosts(final Writer writer, final List<? extends SynchronizationPost> posts) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public void serializeSynchronizationData(final Writer writer, final SynchronizationData syncData) {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public SynchronizationData parseSynchronizationData(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public List<SynchronizationPost> parseSynchronizationPostList(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedMediaTypeException("CSL format supports only publication resources");
	}

	@Override
	public String parseError(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String parseResourceHash(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String parseUserId(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String parseGroupId(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String parseStat(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<User> parseUserList(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public User parseUser(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Post<? extends Resource>> parsePostList(final Reader reader, DataAccessor uploadedFileAcessor) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Post<? extends Resource> parsePost(final Reader reader, DataAccessor uploadedFileAcessor) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Post<? extends Resource> parseCommunityPost(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Group> parseGroupList(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Group parseGroup(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Tag> parseTagList(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Tag parseTag(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SortedSet<RecommendedTag> parseRecommendedTagList(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public RecommendedTag parseRecommendedTag(final Reader reader) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> parseReferences(final Reader reader) {
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

	@Override
	public void serializeRecommendedPosts(Writer writer, List<? extends RecommendedPost<? extends Resource>> posts, ViewModel viewModel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void serializeRecommendedPost(Writer writer, RecommendedPost<? extends Resource> post, ViewModel viewModel) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<RecommendedPost<? extends Resource>> parseRecommendedItemList(Reader reader, DataAccessor uploadedFileAcessor) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public RecommendedPost<? extends Resource> parseRecommendedItem(Reader reader, DataAccessor uploadedFileAccessor) throws BadRequestOrResponseException {
		throw new UnsupportedOperationException();
	}

}
