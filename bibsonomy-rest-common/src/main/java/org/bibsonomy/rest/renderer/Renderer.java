package org.bibsonomy.rest.renderer;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;

/**
 * This interface should be implemented by classes that intend to add additional
 * rendering capabilities to the system.<br/>
 * 
 * Note that it also includes funtionality to read the data, that has been
 * rendered with it.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public interface Renderer {

	/**
	 * Serializes a {@link List} of {@link Post}s.
	 * 
	 * @param writer
	 *            a {@link Writer} to use.
	 * @param posts
	 *            a {@link List} of {@link Post} objects.
	 * @param viewModel
	 *            the {@link ViewModel} encapsulates additional information,
	 */
	public void serializePosts(Writer writer, List<? extends Post<? extends Resource>> posts, ViewModel viewModel) throws InternServerException;

	/**
	 * Serializes one {@link Post}.
	 * 
	 * @param writer
	 *            a {@link Writer} to use.
	 * @param post
	 *            one {@link Post} object.
	 * @param viewModel
	 *            the {@link ViewModel} encapsulates additional information,
	 */
	public void serializePost(Writer writer, Post<? extends Resource> post, ViewModel model);

	/**
	 * Serializes a {@link List} of {@link User}s.
	 * 
	 * @param writer
	 *            a {@link Writer} to use.
	 * @param users
	 *            a {@link List} of {@link User} objects.
	 * @param viewModel
	 *            the {@link ViewModel} encapsulates additional information,
	 */
	public void serializeUsers(Writer writer, List<User> users, ViewModel viewModel);

	/**
	 * Serializes one {@link User}.
	 * 
	 * @param writer
	 *            a {@link Writer} to use.
	 * @param user
	 *            one {@link User} object.
	 * @param viewModel
	 *            the {@link ViewModel} encapsulates additional information,
	 */
	public void serializeUser(Writer writer, User user, ViewModel viewModel);

	/**
	 * Serializes a {@link List} of {@link Tag}s.
	 * 
	 * @param writer
	 *            a {@link Writer} to use.
	 * @param tags
	 *            a {@link List} of {@link Tag} objects.
	 * @param viewModel
	 *            the {@link ViewModel} encapsulates additional information,
	 */
	public void serializeTags(Writer writer, List<Tag> tags, ViewModel viewModel);

	/**
	 * Serializes a {@link Tag}'s details, including {@link List} of subtags,
	 * {@link List} of supertags and {@link List} of correlated tags
	 * 
	 * @param writer
	 *            a {@link Writer} to use.
	 * @param tag
	 *            one {@link Tag} object.
	 * @param viewModel
	 *            the {@link ViewModel} encapsulates additional information,
	 */
	public void serializeTag(Writer writer, Tag tag, ViewModel viewModel);

	/**
	 * Serializes a list of {@link Group}s.
	 * 
	 * @param writer
	 *            a {@link Writer} to use.
	 * @param groups
	 *            a {@link List} of {@link Group} objects.
	 * @param viewModel
	 *            the {@link ViewModel} encapsulates additional information,
	 */
	public void serializeGroups(Writer writer, List<Group> groups, ViewModel viewModel);

	/**
	 * Serializes one {@link Group}.
	 * 
	 * @param writer
	 *            a {@link Writer} to use.
	 * @param group
	 *            one {@link Group} object.
	 * @param viewModel
	 *            the {@link ViewModel} encapsulates additional information,
	 */
	public void serializeGroup(Writer writer, Group group, ViewModel viewModel);

	/**
	 * Serializes an errormessage.
	 * 
	 * @param writer
	 *            the {@link Writer} to use.
	 * @param errorMessage
	 *            the error message to send.
	 */
	public void serializeError(Writer writer, String errorMessage);

	/**
	 * Reads a List of {@link User}s from a {@link Reader}.
	 * 
	 * @param reader
	 *            the {@link Reader} to use.
	 * @return a {@link List} of {@link User} objects.
	 * @throws BadRequestOrResponseException
	 *             if the document within the reader is errorenous.
	 */
	public List<User> parseUserList(Reader reader) throws BadRequestOrResponseException;

	/**
	 * Reads one {@link User} from a {@link Reader}.
	 * 
	 * @param reader
	 *            the {@link Reader} to use.
	 * @return one {@link User} object.
	 * @throws BadRequestOrResponseException
	 *             if the document within the reader is errorenous.
	 */
	public User parseUser(Reader reader) throws BadRequestOrResponseException;

	/**
	 * Reads a List of {@link Post}s from a {@link Reader}.
	 * 
	 * @param reader
	 *            the {@link Reader} to use.
	 * @return a {@link List} of {@link Post} objects.
	 * @throws BadRequestOrResponseException
	 *             if the document within the reader is errorenous.
	 */
	public List<Post<? extends Resource>> parsePostList(Reader reader) throws BadRequestOrResponseException;

	/**
	 * Reads one {@link Post} from a {@link Reader}.
	 * 
	 * @param reader
	 *            the {@link Reader} to use.
	 * @return one {@link Post} object.
	 * @throws BadRequestOrResponseException
	 *             if the document within the reader is errorenous.
	 */
	public Post<? extends Resource> parsePost(Reader reader) throws BadRequestOrResponseException;

	/**
	 * Reads a List of {@link Group}s from a {@link Reader}.
	 * 
	 * @param reader
	 *            the {@link Reader} to use.
	 * @return a {@link List} of {@link Group} objects.
	 * @throws BadRequestOrResponseException
	 *             if the document within the reader is errorenous.
	 */
	public List<Group> parseGroupList(Reader reader) throws BadRequestOrResponseException;

	/**
	 * Reads one {@link Group} from a {@link Reader}.
	 * 
	 * @param reader
	 *            the {@link Reader} to use.
	 * @return one {@link Group} object.
	 * @throws BadRequestOrResponseException
	 *             if the document within the reader is errorenous.
	 */
	public Group parseGroup(Reader reader) throws BadRequestOrResponseException;

	/**
	 * Reads a List of {@link Tag}s from a {@link Reader}.
	 * 
	 * @param reader
	 *            the {@link Reader} to use.
	 * @return a {@link List} of {@link Tag} objects.
	 * @throws BadRequestOrResponseException
	 *             if the document within the reader is errorenous.
	 */
	public List<Tag> parseTagList(Reader reader) throws BadRequestOrResponseException;
	
	/**
	 * Reads one {@link Tag} from a {@link Reader}.
	 * 
	 * @param reader
	 *            the {@link Reader} to use.
	 * @return one {@link Post} object.
	 * @throws BadRequestOrResponseException
	 *             if the document within the reader is errorenous.
	 */
	public Tag parseTag(Reader reader) throws BadRequestOrResponseException;
}