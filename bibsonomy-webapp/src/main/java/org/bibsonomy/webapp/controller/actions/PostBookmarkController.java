package org.bibsonomy.webapp.controller.actions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.tagparser.TagString3Lexer;
import org.bibsonomy.model.util.tagparser.TagString3Parser;
import org.bibsonomy.recommender.tags.TagRecommender;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.controller.SingleResourceListController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PostBookmarkValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author fba
 * @version $Id$
 */
public class PostBookmarkController extends SingleResourceListController implements MinimalisticController<EditBookmarkCommand>, ErrorAware {
	private static final String SYS_RELEVANT_FOR = "sys:relevantFor:";
	private static final Log log = LogFactory.getLog(PostBookmarkController.class);
	private Errors errors = null;

	private static final Group public_group = GroupUtils.getPublicGroup();
	private static final Group private_group = GroupUtils.getPrivateGroup();

	/**
	 * Provides tag recommendations to the user.
	 */
	private TagRecommender tagRecommender = null;

	/**
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	public EditBookmarkCommand instantiateCommand() {
		final EditBookmarkCommand command = new EditBookmarkCommand();
		/*
		 * initialize lists
		 */
		command.setGroups(new ArrayList<String>());
		command.setRelevantGroups(new ArrayList<String>());
		command.setRelevantTagSets(new HashMap<String, Map<String, List<String>>>());
		command.setRecommendedTags(new TreeSet<RecommendedTag>());
		command.setCopytags(new ArrayList<Tag>());
		/*
		 * initialize post & resource
		 */
		command.setPost(new Post<Bookmark>());
		command.getPost().setResource(new Bookmark());
		command.setAbstractGrouping("public");

		/*
		 * set default values.
		 */
		command.getPost().getResource().setUrl("http://");
		return command;
	}

	/**
	 * Main method which does the postBookmark-procedure.
	 * 
	 * FIXME: for:group is missing FIXME: if the bookmark is scrapable the
	 * bibtex-string will be put into the request (or use scraping service)
	 * FIXME: check for other features from BookmarkShowHandler
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(java.lang.Object)
	 */
	public View workOn(EditBookmarkCommand command) {
		final RequestWrapperContext context = command.getContext();
		/*
		 * FIXME: i18n
		 */
		command.setPageTitle("post a new bookmark");

		/*
		 * only users which are logged in might post bookmarks -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			/*
			 * FIXME: We need to add the ?referer= parameter such that the user
			 * is send back to this controller after login. This is not so
			 * simple, because we cannot access the query path and for POST
			 * requests we would need to build the parameters by ourselves.
			 */
			return new ExtendedRedirectView("/login");
		}

		/*
		 * FIXME: spammers must have a valid captcha, so add captcha to JSP
		 * and checking here.
		 */
		
		final User loginUser = context.getLoginUser();
		final Post<Bookmark> post = command.getPost();

		/*
		 * set the user of the post to the loginUser (the recommender might need
		 * the user name)
		 */
		post.setUser(loginUser);

		/*
		 * initialize groups
		 */
		initPostGroups(command, post);
		/*
		 * initialize relevantFor-tags 
		 */
		initRelevantForTags(command, post);

		/*
		 * decide, what to do
		 */
		final String intraHashToUpdate = command.getIntraHashToUpdate();
		if (ValidationUtils.present(intraHashToUpdate)) {

			log.debug("intra hash to update found -> handling update of existing post");
			return handleUpdatePost(command, context, loginUser, post, intraHashToUpdate);

		}

		log.debug("no intra hash given -> new post");
		return handleCreatePost(command, context, loginUser, post);

	}


	/**
	 * This methods does everything which needs to be done before proceeding to the 
	 * {@link Views#POST_BOOKMARK} view. This includes:
	 * <ul>
	 * <li>initializing the group tag sets</li>
	 * <li>getting the recommended tags</li>
	 * <li>getting the tag cloud of the user</li>
	 * </ul>
	 * Thus, never return the view directly, but use this method!
	 * 
	 * TODO: candidate for refactoring (e.g., abstract class PostController)
	 * 
	 * @param command -
	 *            the command the controller is working on (and which is also
	 *            handed over to the view).
	 * @param loginUser -
	 *            the login user.
	 * @return The {@link Views#POST_BOOKMARK} view.
	 */
	private View getPostBookmarkView(final EditBookmarkCommand command, final User loginUser) {
		/*
		 * initialize tag sets for groups
		 */
		initGroupTagSets(loginUser);

		/*
		 * get the recommended tags for the post from the command
		 */
		// 2009/01/29,fei: now done via ajax request "/ajax/getBookmarkRecommendedTags"
		//                 {@see GetBookmarkRecommendedTagsController}
		// if (tagRecommender != null)	command.setRecommendedTags(tagRecommender.getRecommendedTags(command.getPost()));

		/*
		 * get the tag cloud of the user (this must be done before any error
		 * checking, because the user must have this) FIXME: get ALL tags of the
		 * user!
		 */
		this.setTags(command, Resource.class, GroupingEntity.USER, loginUser.getName(), null, null, null, null, 0, 20000, null);
		/*
		 * return the view
		 */
		return Views.POST_BOOKMARK;
	}

	/**
	 * Handles the update of an existing post with the given intra hash.
	 * 
	 * @param command
	 * @param context
	 * @param loginUser
	 * @param post
	 * @param intraHashToUpdate
	 * @return
	 */
	private View handleUpdatePost(EditBookmarkCommand command, final RequestWrapperContext context, final User loginUser, final Post<Bookmark> post, final String intraHashToUpdate) {
		/*
		 * we're editing an existing post
		 */
		if (!context.isValidCkey()) {
			log.debug("no valid ckey found -> assuming first call, populating form");
			/*
			 * ckey is invalid, so this is probably the first call --> get post
			 * from DB
			 */
			final Post<Bookmark> dbPost = (Post<Bookmark>) logic.getPostDetails(intraHashToUpdate, loginUser.getName());
			if (dbPost == null) {
				/*
				 * invalid intra hash: post could not be found
				 */
				errors.reject("errors.post.notfound");
				return Views.ERROR;
			}
			/*
			 * put post into command
			 */
			populateCommandWithPost(command, dbPost);
			/*
			 * returning to view
			 */
			return getPostBookmarkView(command, loginUser);
		}
		log.debug("ckey given, so parse tags, validate post, update post");
		/*
		 * ckey is given, so user is already editing the post -> parse tags
		 */
		try {
			post.getTags().addAll(parse(command.getTags()));
		} catch (final Exception e) {
			log.warn("error parsing tags", e);
			errors.rejectValue("tags", "error.field.valid.tags.parseerror");
		}
		/*
		 * validate post
		 */
		org.springframework.validation.ValidationUtils.invokeValidator(getValidator(), command, errors);
		/*
		 * check, if the URL has changed
		 */
		post.getResource().recalculateHashes();
		if (!intraHashToUpdate.equals(post.getResource().getIntraHash())) {
			/*
			 * URL has changed -> check, if new URL already bookmarked
			 */
			final Post<Bookmark> dbPost = (Post<Bookmark>) logic.getPostDetails(post.getResource().getIntraHash(), loginUser.getName());
			if (dbPost != null) {
				log.debug("user already owns this URL ... handling update");
				/*
				 * post exists -> warn user
				 */
				errors.rejectValue("post.resource.url", "error.field.valid.url.alreadybookmarked");
			}
		}
		/*
		 * return to form until validation passes
		 */
		if (errors.hasErrors()) {
			log.debug("returning to view because of errors: " + errors.getErrorCount());
			log.debug("post is " + post.getResource());
			return getPostBookmarkView(command, loginUser);
		}
		/*
		 * the post to update has the given intra hash
		 */
		post.getResource().setIntraHash(command.getIntraHashToUpdate());
		/*
		 * create post list for update
		 */
		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(post);
		/*
		 * update date TODO: don't we want to keep the posting date unchanged
		 * and only update the date? --> actually, this does currently not work,
		 * since the DBLogic doesn't set the date and thus we get a NPE from the
		 * database
		 */
		post.setDate(new Date());
		/*
		 * update post in DB
		 */
		final List<String> updatePosts = logic.updatePosts(posts, PostUpdateOperation.UPDATE_ALL);
		if (updatePosts == null || updatePosts.isEmpty()) {
			/*
			 * show error page
			 */
			errors.reject("Could not update post");
			log.warn("could not update post");
			return Views.ERROR;
		}
		/*
		 * leave if and reach final redirect
		 */
		return finalRedirect(command.isJump(), loginUser.getName(), post.getResource().getUrl());
	}

	/**
	 * Create the final redirect after successful creating / updating a post. if
	 * jump is <code>true</code>, we redirect to the given postUrl. Otherwise
	 * we redirect to the user's page.
	 * 
	 * @param jump -
	 *            indicates whether to jump to postUrl (<code>true</code>)
	 *            or to the user's page.
	 * @param userName -
	 *            the name of the loginUser
	 * @param postUrl -
	 *            the URL of the post
	 * @return
	 */
	private View finalRedirect(final boolean jump, final String userName, final String postUrl) {
		/*
		 * if bookmark was posted by bookmarklet (jump = true) -> redirect to
		 * URL user is coming from
		 */
		if (!jump) {
			try {
				return new ExtendedRedirectView("/user/" + URLEncoder.encode(userName, "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				log.error("Could not encode redirect URL.", ex);
				errors.reject("error encoding URL");
				return Views.ERROR;
			}
		}
		return new ExtendedRedirectView(postUrl);
	}

	private View handleCreatePost(EditBookmarkCommand command, final RequestWrapperContext context, final User loginUser, final Post<Bookmark> post) {
		/*
		 * no intra hash given --> user posts a new entry (which might already
		 * exist!)
		 */

		/*
		 * check, if post already exists
		 */
		post.getResource().recalculateHashes();
		final Post<Bookmark> dbPost = (Post<Bookmark>) logic.getPostDetails(post.getResource().getIntraHash(), loginUser.getName());
		if (dbPost != null) {
			log.debug("user already owns this URL ... handling update");
			/*
			 * TODO: here we could check, if the user has marked a checkbox
			 * which says "I want to update this post" and only then set the
			 * intraHashToUpdate
			 */
			/*
			 * post exists -> warn user
			 */
			errors.rejectValue("post.resource.url", "error.field.valid.url.alreadybookmarked");
			/*
			 * the next time this will be handled as an update TODO: this does
			 * not make sense, since we don't show the user the existing post
			 * (actually: again, we can't exchange the post ... :-( )
			 */
			command.setIntraHashToUpdate(post.getResource().getIntraHash());
			/*
			 * exchange posts
			 */
			command.setDiffPost(post);
			populateCommandWithPost(command, dbPost);
			/*
			 * in any case: return to view
			 */
			return getPostBookmarkView(command, loginUser);
		}
		log.debug("wow, post is completely new! So ... return until no errors and then store it");
		/*
		 * post is completely new -> validate!
		 */
		org.springframework.validation.ValidationUtils.invokeValidator(getValidator(), command, errors);
		/*
		 * parse the tags
		 */
		try {
			post.getTags().addAll(parse(command.getTags()));
		} catch (final Exception e) {
			log.warn("error parsing tags", e);
			errors.rejectValue("tags", "error.field.valid.tags.parseerror");
		}

		/*
		 * return to form until validation passes
		 */
		if (errors.hasErrors()) {
			log.debug("returning to view because of errors: " + errors.getErrorCount());
			log.debug("post is " + post.getResource());
			return getPostBookmarkView(command, loginUser);
		}

		/*
		 * check credentials to fight CSRF attacks We do this that late to not
		 * cause the error message pop up on the first call to the controller.
		 * Otherwise, the form would be empty and the hidden ckey field not
		 * sent.
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return getPostBookmarkView(command, loginUser);
		}

		/*
		 * TODO: why is the content id set?
		 */
		// post.setContentId(null);
		post.setDate(new Date());

		/*
		 * create list for posting
		 */
		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(post);
		/*
		 * new post -> create
		 */
		log.debug("finally: creating a new post in the DB");
		final String createPosts = logic.createPosts(posts).get(0);
		log.debug("created post: " + createPosts);

		return finalRedirect(command.isJump(), loginUser.getName(), post.getResource().getUrl());
	}

	/**
	 * Populates the command with the given post. Ensures, that fields which
	 * depend on the post (like the tag string, or the groups) in the command
	 * are correctly filled.
	 * 
	 * @param command
	 * @param dbPost
	 */
	private void populateCommandWithPost(final EditBookmarkCommand command, final Post<Bookmark> dbPost) {
		/*
		 * put post into command
		 */
		command.setPost(dbPost);
		/*
		 * populate "relevant for" groups in command
		 */
		initCommandRelevantForGroups(command, dbPost.getTags());
		/*
		 * populate groups in command
		 */
		initCommandGroups(command, dbPost.getGroups());		
		/*
		 * create tag string for view input field
		 * (NOTE: this needs to be done after initializing the relevantFor groups, because there
		 * the relevantFor tags are removed from the post)
		 * 
		 */
		command.setTags(getSimpleTagString(dbPost.getTags()));

	}

	private void initCommandRelevantForGroups(final EditBookmarkCommand command, final Set<Tag> tags) {
		log.debug("got tags " + tags + " from post");
		final List<String> relevantGroups = command.getRelevantGroups();
		
		final Iterator<Tag> iterator = tags.iterator();
		while (iterator.hasNext()) {
			final Tag tag = iterator.next();
			final String name = tag.getName();
			log.debug("handling tag " + tag);
			if (name.startsWith(SYS_RELEVANT_FOR)) {
				relevantGroups.add(name.substring(SYS_RELEVANT_FOR.length()));
				/*
				 * removing the tag from the post such that it is not shown in the tag input form
				 */
				log.debug("removing tag from post");
				iterator.remove();
			}
		}
		log.debug("relevant groups: " + relevantGroups);
		log.debug("posts's tags: " + tags);
	}
	
	
	private void initRelevantForTags(final EditBookmarkCommand command, final Post<Bookmark> post) {
		log.debug("initializing post's tags from command's relevantFor groups");
		final List<String> relevantGroups = command.getRelevantGroups();
		log.debug("got relevant groups " + relevantGroups + " from command");
		final Set<Tag> tags = post.getTags();
		log.debug("got tags " + tags + " from post");
		for (final String relevantGroup : relevantGroups) {
			tags.add(new Tag(SYS_RELEVANT_FOR + relevantGroup));
		}
		log.debug("post's tags are now: " + tags);
		
	}
	
	/**
	 * Copy the groups from the command into the post (make proper groups from
	 * them)
	 * 
	 * TODO: candidate for refactoring (e.g., abstract class PostController)
	 * 
	 * @param command -
	 *            contains the groups as represented by the form fields.
	 * @param post -
	 *            the post whose groups should be populated from the command.
	 * @see #initCommandGroups(EditBookmarkCommand, Post)
	 */
	private void initPostGroups(final EditBookmarkCommand command, final Post<Bookmark> post) {
		log.debug("initializing post's groups from command");
		/*
		 * we can avoid some checks here, because they're done in the validator
		 * ...
		 */
		final Set<Group> postGroups = post.getGroups();
		final String abstractGrouping = command.getAbstractGrouping();
		if ("other".equals(abstractGrouping)) {
			log.debug("found 'other' grouping");
			/*
			 * copy groups into post
			 */
			final List<String> groups = command.getGroups();
			log.debug("groups in command: " + groups);
			for (final String groupname : groups) {
				postGroups.add(new Group(groupname));
			}
			log.debug("groups in post: " + postGroups);
		} else {
			log.debug("public or private post");
			/*
			 * if the post is private or public --> remove all groups and add
			 * one (private or public)
			 */
			postGroups.clear();
			postGroups.add(new Group(abstractGrouping));
		}
	}

	/**
	 * Populates the helper attributes of the command with the groups from the
	 * post.
	 * 
	 * TODO: candidate for refactoring (e.g., abstract class PostController)
	 * 
	 * @param command -
	 *            the command whose groups should be populated from the post.
	 * @param post -
	 *            the post which contains the groups.
	 * @see #initPostGroups(EditBookmarkCommand, Post)
	 */
	private void initCommandGroups(final EditBookmarkCommand command, Set<Group> groups) {
		log.debug("groups from post: " + groups);
		final List<String> commandGroups = command.getGroups();
		commandGroups.clear();
		if (groups.contains(private_group)) {
			/*
			 * only private
			 */
			command.setAbstractGrouping(private_group.getName());
		} else if (groups.contains(public_group)) {
			/*
			 * only public
			 */
			command.setAbstractGrouping(public_group.getName());
		} else {
			/*
			 * other
			 */
			command.setAbstractGrouping("other");
			/*
			 * copy groups into command
			 */
			for (final Group group : groups) {
				commandGroups.add(group.getName());
			}
		}
		log.debug("abstractGrouping: " + command.getAbstractGrouping());
		log.debug("commandGroups: " + command.getGroups());
	}

	/**
	 * Gets the tagsets for each group from the DB and stores them in the users
	 * group list.
	 * 
	 * TODO: candidate for refactoring (e.g., abstract class PostController)
	 * 
	 * @param loginUser
	 */
	private void initGroupTagSets(final User loginUser) {
		/*
		 * Get tagsets for each group and add them to the loginUser object. Why
		 * into the loginUser? Because there we already have the groups the user
		 * is member of.
		 */
		final List<Group> usersGroups = loginUser.getGroups();
		final ArrayList<Group> groupsWithTagSets = new ArrayList<Group>();
		for (final Group group : usersGroups) {
			if (group.getName() != null) {
				groupsWithTagSets.add(this.logic.getGroupDetails(group.getName()));
			}
		}
		loginUser.setGroups(groupsWithTagSets);

	}

	/**
	 * Concatenates the tags into one tag string to show them to the user.
	 * 
	 * TODO: candidate for refactoring (e.g., abstract class PostController)
	 * 
	 * @param tags
	 * @return A string representing the tags, separated by space
	 */
	private String getSimpleTagString(final Set<Tag> tags) {
		final StringBuffer s = new StringBuffer();
		for (final Tag tt : tags) {
			s.append(tt.getName() + " ");
		}
		return s.toString();
	}

	/**
	 * Parses the incoming tag string into a set of tags.
	 * 
	 * TODO: candidate for refactoring (e.g., abstract class PostController)
	 * 
	 * @param tagString
	 * @return
	 * @throws RecognitionException
	 */
	private Set<Tag> parse(final String tagString) throws RecognitionException {
		final Set<Tag> tags = new TreeSet<Tag>();

		if (tagString != null) {
			/*
			 * prepare parser
			 */
			final CommonTokenStream tokens = new CommonTokenStream();
			tokens.setTokenSource(new TagString3Lexer(new ANTLRStringStream(tagString)));
			final TagString3Parser parser = new TagString3Parser(tokens, tags);
			/*
			 * parse
			 */
			parser.tagstring();
		}
		return tags;
	}

	private PostBookmarkValidator getValidator() {
		return new PostBookmarkValidator();
	}

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * Sets the tag recommender this controller uses to provide tag
	 * recommendations during posting.
	 * 
	 * @param tagRecommender
	 */
	public void setTagRecommender(TagRecommender tagRecommender) {
		Assert.notNull(tagRecommender, "The provided tag recommender must not be null.");
		this.tagRecommender = tagRecommender;
	}

}
