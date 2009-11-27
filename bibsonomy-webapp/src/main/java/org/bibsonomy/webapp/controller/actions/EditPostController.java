package org.bibsonomy.webapp.controller.actions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.model.util.UserUtils;
import org.bibsonomy.recommender.tags.database.RecommenderStatisticsManager;
import org.bibsonomy.services.recommender.TagRecommender;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.controller.SingleResourceListController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.captcha.Captcha;
import org.bibsonomy.webapp.validation.EditPostValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * @author fba
 * @version $Id$
 * @param <RESOURCE> 
 */
public abstract class EditPostController<RESOURCE extends Resource> extends SingleResourceListController implements MinimalisticController<EditPostCommand<RESOURCE>>, ErrorAware {

	private static final Log log = LogFactory.getLog(EditPostController.class);
	private Errors errors = null;
	private TagRecommender tagRecommender;
	private Captcha captcha;
	private RequestLogic requestLogic;


	/**
	 * FIXME: system tag handling should be done by system tags ... not by this
	 * controller.
	 */
	private static final String SYS_RELEVANT_FOR = SystemTags.RELEVANTFOR.getPrefix() + SystemTags.SYSTAG_DELIM;

	private static final Group PUBLIC_GROUP = GroupUtils.getPublicGroup();
	private static final Group PRIVATE_GROUP = GroupUtils.getPrivateGroup();

	protected static final String LOGIN_NOTICE = "login.notice.post.";

	/**
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	public EditPostCommand<RESOURCE> instantiateCommand() {
		final EditPostCommand<RESOURCE> command = instantiateEditPostCommand();
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
		command.setPost(new Post<RESOURCE>());
		command.setAbstractGrouping(PUBLIC_GROUP.getName());
		command.getPost().setResource(instantiateResource());

		/*
		 * set default values.
		 */
		command.setPostID(RecommenderStatisticsManager.getUnknownPID());
		return command;
	}

	/**
	 * Instantiated the correct command for this controller.
	 * @return
	 */
	protected abstract EditPostCommand<RESOURCE> instantiateEditPostCommand();

	/**
	 * Instantiates a resource which the controller puts into the commands post.
	 * 
	 * @return 
	 */
	protected abstract RESOURCE instantiateResource();


	/**
	 * Main method which does the posting-procedure.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(java.lang.Object)
	 */
	public View workOn(final EditPostCommand<RESOURCE> command) {
		final Locale locale = requestLogic.getLocale();
		final RequestWrapperContext context = command.getContext();
		/*
		 * TODO: i18n
		 */
		command.setPageTitle("edit a post");
		/*
		 * We store the referer in the command, to send the user back to the 
		 * page he's coming from at the end of the posting process. 
		 */
		if (!ValidationUtils.present(command.getReferer())) {
			command.setReferer(requestLogic.getReferer());
		}

		/*
		 * only users which are logged in might post -> send them to
		 * login page
		 */
		if (!context.isUserLoggedIn()) {
			/*
			 * We add two referer headers: the inner for this controller to 
			 * send the user back to the page he was initially coming from,
			 * the outer for the login page to send the user back to this 
			 * controller.
			 */
			return new ExtendedRedirectView("/login" + 
					"?notice=" + LOGIN_NOTICE + command.getPost().getResource().getClass().getSimpleName().toLowerCase() + 
					"&referer=" + safeURIEncode(requestLogic.getCompleteRequestURL() + "&referer=" + safeURIEncode(requestLogic.getReferer()))); 
		}

		final User loginUser = context.getLoginUser();

		
		/*
		 * After having handled the general issues (login, referer, etc.),
		 * sub classes can now execute their workOn code 
		 */
		workOnCommand(command, loginUser);
		
		/*
		 * If user is spammer block him silently by entering captcha again and again
		 */
		if (loginUser.isSpammer()){
			command.setCaptchaHTML(captcha.createCaptchaHtml(locale));
			errors.rejectValue("recaptcha_response_field", "error.field.valid.captcha");
		}


		/*
		 * handle copying of a post using intra hash + user name
		 */
		if (ValidationUtils.present(command.getHash()) && ValidationUtils.present(command.getUser())) {
			/*
			 * hash + user given: user wants to copy a post
			 * FIXME: really ensure, that the tag field is not filled
			 *        (otherwise the post is automatically saved ...)
			 */
			command.setPost(getPostDetails(command.getHash(), command.getUser()));
		} else {
			/*
			 * The post in the command is coming from the form: bring it into 
			 * the format we're using internally. 
			 */
			this.preparePostAfterView(command.getPost());
		}

		/*
		 * this is the post we're working on for now ...
		 */
		final Post<RESOURCE> post = command.getPost();

		/*
		 * set user, init post groups, relevant for tags (FIXME: candidate for 
		 * system tags) and recommender
		 */
		this.initPost(command, post, loginUser);

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
	
	protected abstract void workOnCommand(final EditPostCommand<RESOURCE> command, final User loginUser);

	/**
	 * TODO extract method; used by many controllers
	 * Encodes the given String with URLEncoder. If that fails, returns an empty string.
	 * 
	 * @param s
	 * @return
	 */
	protected String safeURIEncode(final String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception ex) {
			return s;
		}
	}

	/**
	 * This methods does everything which needs to be done before proceeding to
	 * the view. This includes:
	 * <ul>
	 * <li>initializing the group tag sets</li>
	 * <li>getting the recommended tags</li>
	 * <li>getting the tag cloud of the user</li>
	 * </ul>
	 * Thus, never return the view directly, but use this method!
	 * 
	 * @param command -
	 *            the command the controller is working on (and which is also
	 *            handed over to the view).
	 * @param loginUser -
	 *            the login user.
	 * @return The post view.
	 */
	protected View getEditPostView(final EditPostCommand<RESOURCE> command, final User loginUser) {
		/*
		 * initialize tag sets for groups
		 */
		initGroupTagSets(loginUser);

		/*
		 * get the tag cloud of the user (this must be done before any error
		 * checking, because the user must have this) 
		 */
		this.setTags(command, Resource.class, GroupingEntity.USER, loginUser.getName(), null, null, null, null, 0, 20000, null);
		/*
		 * get the relations of the user
		 */
		final List<Tag> concepts = this.logic.getConcepts(null, GroupingEntity.USER, loginUser.getName(), null, null, ConceptStatus.PICKED, 0, Integer.MAX_VALUE);
		command.getConcepts().setConceptList(concepts);
		command.getConcepts().setNumConcepts(concepts.size());

		/*
		 * prepare post from internal format into user's form format
		 */
		this.preparePostForView(command.getPost());
		/*
		 * return the view
		 */
		return getPostView();
	}

	/**
	 * Before a post is sent to the view, this method is called.
	 * 
	 * @param post
	 */
	protected void preparePostForView(final Post<RESOURCE> post) {
		// do nothing	
	}

	/**
	 * Immediately after a post is coming from the view, this method is called.
	 * 
	 * @param post
	 */
	protected void preparePostAfterView(final Post<RESOURCE> post) {
		// do nothing		
	}

	protected abstract View getPostView();

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
	@SuppressWarnings("unchecked")
	private View handleUpdatePost(final EditPostCommand<RESOURCE> command, final RequestWrapperContext context, final User loginUser, final Post<RESOURCE> post, final String intraHashToUpdate) {
		final String loginUserName = loginUser.getName();
		/*
		 * we're editing an existing post
		 */
		if (!context.isValidCkey()) {
			log.debug("no valid ckey found -> assuming first call, populating form");
			/*
			 * ckey is invalid, so this is probably the first call --> get post
			 * from DB
			 */
			final Post<RESOURCE> dbPost = getPostDetails(intraHashToUpdate, loginUserName);
			if (dbPost == null) {
				/*
				 * invalid intra hash: post could not be found
				 */
				errors.reject("error.post.notfound");
				return Views.ERROR;
			}
			/*
			 * put post into command
			 */
			populateCommandWithPost(command, dbPost);
			/*
			 * returning to view
			 */
			return getEditPostView(command, loginUser);
		}
		log.debug("ckey given, so parse tags, validate post, update post");
		/*
		 * ckey is given, so user is already editing the post -> parse tags
		 */
		cleanAndValidatePost(command, post);
		/*
		 * check, if the post has changed
		 */
		post.getResource().recalculateHashes();
		if (!intraHashToUpdate.equals(post.getResource().getIntraHash())) {
			/*
			 * post has changed -> check, if new post has already been posted
			 */
			final Post<RESOURCE> dbPost = getPostDetails(post.getResource().getIntraHash(), loginUserName);
			if (dbPost != null) {
				log.debug("user already owns this post ... handling update");
				/*
				 * post exists -> warn user
				 */
				setDuplicateErrorMessage(post, errors);
			}
		}
		/*
		 * return to form until validation passes
		 */
		if (errors.hasErrors()) {
			log.debug("returning to view because of errors: " + errors.getErrorCount());
			log.debug("post is " + post.getResource());
			return getEditPostView(command, loginUser);
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
		setDate(post, loginUserName);
		/*
		 * update post in DB
		 */
		final List<String> updatePosts = logic.updatePosts(posts, PostUpdateOperation.UPDATE_ALL);
		if (updatePosts == null || updatePosts.isEmpty()) {
			/*
			 * show error page
			 */
			errors.reject("error.post.update", "Could not update post.");
			log.warn("could not update post");
			return Views.ERROR;
		}
		/*
		 * update recommender table such that recommendations are linked to the final post
		 */
		setRecommendationFeedback(post, command.getPostID());

		/*
		 * leave if and reach final redirect
		 */
		return finalRedirect(loginUserName, command.getReferer());
	}

	/**
	 * The method {@link PostLogicInterface#getPostDetails(String, String)} throws
	 * an exception, if the post with the requested hash+user does not exist but 
	 * once existed and now has been moved. Since we just want to check, if the
	 * post with the given hash exists NOW, we can ignore that exception and 
	 * instead just return null.
	 * 
	 * @param intraHash
	 * @param userName
	 * @return
	 * @see {https://www.kde.cs.uni-kassel.de/mediawiki/index.php/Bibsonomy:PostHashRedirect}
	 * @see {https://www.kde.cs.uni-kassel.de/mediawiki/index.php/Bibsonomy:PostPublicationUmziehen#gel.C3.B6schte.2Fge.C3.A4nderte_Posts_.28Hash-Redirect-Problem.29}
	 */
	@SuppressWarnings("unchecked")
	private Post<RESOURCE> getPostDetails(final String intraHash, final String userName) {
		try {
			return (Post<RESOURCE>) logic.getPostDetails(intraHash, userName);
		} catch (final ResourceMovedException e) {
			/*
			 * getPostDetails() has a redirect mechanism that checks for posts 
			 * in the log tables. If it find's a post with the given hash there,
			 * it throws an exception, giving the hash of the next post. We want
			 * to ignore this behavior, thus we ignore the exception
			 * 
			 * see https://www.kde.cs.uni-kassel.de/mediawiki/index.php/Bibsonomy:PostHashRedirect
			 * and https://www.kde.cs.uni-kassel.de/mediawiki/index.php/Bibsonomy:PostPublicationUmziehen#gel.C3.B6schte.2Fge.C3.A4nderte_Posts_.28Hash-Redirect-Problem.29
			 */
		}
		return null;
	}

	/**
	 * When we detect that the user has changed the post such that it is equal to
	 * an existing post, this method is called and shall provide the user with a
	 * meaningful error message.
	 * 
	 * @param post
	 */
	protected abstract void setDuplicateErrorMessage(final Post<RESOURCE> post, final Errors errors);

	/**
	 * This method cleans and validates the post:
	 * <ul>
	 * <li>parsing tags</li>
	 * <li>calling the validator</li>
	 * <li>cleaning the post using {@link #cleanPost(Post)}</li>
	 * </ul>
	 * 
	 * @param command
	 * @param post
	 */
	private void cleanAndValidatePost(final EditPostCommand<RESOURCE> command, final Post<RESOURCE> post) {
		try {
			/*
			 * we use addAll here because there might already be system tags 
			 * in the post which should not be overwritten
			 */
			post.getTags().addAll(TagUtils.parse(command.getTags()));
		} catch (final Exception e) {
			log.warn("error parsing tags", e);
			errors.rejectValue("tags", "error.field.valid.tags.parseerror");
		}
		/*
		 * validate post
		 */
		this.validatePost(command);
		/*
		 * clean post
		 */
		this.cleanPost(post);
	}

	protected void setDate(final Post<RESOURCE> post, final String loginUserName) {
		/*
		 * Overwrite the date with the current date, if not posted by the DBLP user.
		 * If DBLP does not provide a date, we have to set the date, too.
		 */
		if (!UserUtils.isDBLPUser(loginUserName) || post.getDate() == null) {
			/*
			 * update date TODO: don't we want to keep the posting date unchanged
			 * and only update the date? --> actually, this does currently not work,
			 * since the DBLogic doesn't set the date and thus we get a NPE from the
			 * database
			 */
			post.setDate(new Date());	
		}
	}

	/**
	 * Validates the post using the validator returned by {@link #getValidator()}.
	 * 
	 * @param command
	 */
	private void validatePost(final EditPostCommand<RESOURCE> command) {
		org.springframework.validation.ValidationUtils.invokeValidator(getValidator(), command, errors);
	}

	/**
	 * After validation, the controller can clean the post, i.e., normalize tags 
	 * or so. 
	 * 
	 * @param post
	 */
	protected void cleanPost(final Post<RESOURCE> post) {
		// noop
	}


	/**
	 * Update recommender table such that recommendations are linked to the final 
	 * post.
	 * 
	 * @param post - the final post as saved in the database.
	 * @param postID - the ID of the post during the posting process.
	 */
	private void setRecommendationFeedback(final Post<RESOURCE> post, final int postID) {
		try {
			/*
			 * To allow the recommender to identify the post and connect it with
			 * the post we provided at recommendation time, we give it the post
			 * id using the contentid field. 
			 */
			post.setContentId(postID);
			tagRecommender.setFeedback(post);
		} catch (final Exception ex) {
			log.warn("Could not connect post with recommendation.");
			/*
			 * fail silently to not confuse user with error 500 when recommender fails 
			 */
		}
	}


	/**
	 * Create the final redirect after successful creating / updating a post. We 
	 * redirect to the URL the user was initially coming from. If we don't have 
	 * that URL (for whatever reason), we redirect to the user's page.
	 * 
	 * @param userName - the name of the loginUser
	 * @param referer - the URL of the page the user is initially coming from
	 *        
	 * @return
	 */
	private View finalRedirect(final String userName, final String referer) {

		/*
		 * If there is no referer URL given, redirect to the user's home page. 
		 */
		if (!ValidationUtils.present(referer)) {
			try {
				log.debug("redirecting to user page");
				return new ExtendedRedirectView("/user/" + URLEncoder.encode(userName, "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				log.error("Could not encode redirect URL.", ex);
				errors.reject("error.post.redirect", new Object[]{ex.getMessage()}, "Error encoding URL for redirect: " + ex.getMessage());
				return Views.ERROR;
			}
		}
		/*
		 * redirect to referer URL
		 */
		return new ExtendedRedirectView(referer);
	}

	private View handleCreatePost(final EditPostCommand<RESOURCE> command, final RequestWrapperContext context, final User loginUser, final Post<RESOURCE> post) {
		final String loginUserName = loginUser.getName();

		/*
		 * no intra hash given --> user posts a new entry (which might already
		 * exist!)
		 */

		/*
		 * check, if post already exists
		 */
		if (this.setDiffPost(command)) {
			/*
			 * post already exists -> let user edit that post
			 */
			return getEditPostView(command, loginUser);
		}

		log.debug("wow, post is completely new! So ... return until no errors and then store it");

		/*
		 * parses the tags, 
		 */
		cleanAndValidatePost(command, post);

		/*
		 * return to form until validation passes
		 */
		if (errors.hasErrors()) {
			log.debug("returning to view because of errors: " + errors.getErrorCount());
			log.debug("post is " + post.getResource());
			return getEditPostView(command, loginUser);
		}

		/*
		 * check credentials to fight CSRF attacks 
		 * 
		 * We do this that late to not
		 * cause the error message pop up on the first call to the controller.
		 * Otherwise, the form would be empty and the hidden ckey field not
		 * sent.
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return getEditPostView(command, loginUser);
		}

		setDate(post, loginUserName);

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

		/*
		 * update recommender table such that recommendations are linked to the final post
		 */
		setRecommendationFeedback(post, command.getPostID());

		return finalRedirect(loginUserName, command.getReferer()); 
	}

	/**
	 * Populates the command with the given post. Ensures, that fields which
	 * depend on the post (like the tag string, or the groups) in the command
	 * are correctly filled.
	 * 
	 * @param command
	 * @param dbPost
	 */
	protected void populateCommandWithPost(final EditPostCommand<RESOURCE> command, final Post<RESOURCE> dbPost) {
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
		 * create tag string for view input field (NOTE: this needs to be done
		 * after initializing the relevantFor groups, because there the
		 * relevantFor tags are removed from the post)
		 * 
		 */
		command.setTags(getSimpleTagString(dbPost.getTags()));

	}

	/** Initializes the relevant for groups in the command from the (system) tags of the 
	 * post. Also removes the corresponding system tags from the post such that they're 
	 * not shown in the tag input field.
	 * 
	 * @param command
	 * @param tags
	 */
	private void initCommandRelevantForGroups(final EditPostCommand<RESOURCE> command, final Set<Tag> tags) {
		final List<String> relevantGroups = command.getRelevantGroups();

		final Iterator<Tag> iterator = tags.iterator();
		while (iterator.hasNext()) {
			final String name = iterator.next().getName();
			if (name.startsWith(SYS_RELEVANT_FOR)) {
				relevantGroups.add(name.substring(SYS_RELEVANT_FOR.length()));
				/*
				 * removing the tag from the post such that it is not shown in
				 * the tag input form
				 */
				iterator.remove();
			}
		}
	}

	/**
	 * Adds the relevant groups from the command as system tags to the post. 
	 * 
	 * @param command
	 * @param post
	 */
	private void initRelevantForTags(final EditPostCommand<RESOURCE> command, final Post<RESOURCE> post) {
		final Set<Tag> tags = post.getTags();
		final List<Group> groups = command.getContext().getLoginUser().getGroups();
		final List<String> relevantGroups = command.getRelevantGroups();
		/*
		 * null check neccessary, because Spring sets the list to null, when no group 
		 * has been selected. :-(
		 */
		if (relevantGroups != null) {
			for (final String relevantGroup : relevantGroups) {
				/*
				 * ignore groups the user is not a member of
				 */
				if (groups.contains(new Group(relevantGroup))) {
					tags.add(new Tag(SYS_RELEVANT_FOR + relevantGroup));
				} else {
					log.info("ignored relevantFor group '" + relevantGroup + "' because user is not member of it");
				}
			}
		}
	}

	/**
	 * sets user; inits post groups, relevant tags and recommender
	 * 
	 * @param command
	 */
	protected void initPost(final EditPostCommand<RESOURCE> command, final Post<RESOURCE> post, final User loginUser) {
		/* 
		 * set the user of the post to the loginUser (the recommender might need
		 * the user name)
		 */
		post.setUser(loginUser);
		/*
		 * initialize groups
		 */
		this.initPostGroups(command, post);
		/*
		 * initialize relevantFor-tags FIXME: candidate for system tags
		 */
		this.initRelevantForTags(command, post);
		/*
		 * For each post process an unique identifier is generated. 
		 * This is used for mapping posts to recommendations.
		 */
		if (command.getPostID() == RecommenderStatisticsManager.getUnknownPID()) {
			command.setPostID(RecommenderStatisticsManager.getNewPID());
		}
	}

	/**
	 * checks if the user already bookmarked the resource of the command
	 * if the user owns the resource => diff post will be set
	 * 
	 * @param command
	 * @return  if user already owns resource
	 */
	@SuppressWarnings("unchecked")
	protected boolean setDiffPost(final EditPostCommand<RESOURCE> command) {
		final RequestWrapperContext context = command.getContext();
		final Post<RESOURCE> post = command.getPost();
		final String loginUserName = context.getLoginUser().getName();
		final RESOURCE resource = post.getResource();
		resource.recalculateHashes();

		/*
		 * is resource already owned by the user?
		 */
		final Post<RESOURCE> dbPost = getPostDetails(resource.getIntraHash(), loginUserName);

		if (dbPost != null) {
			log.debug("set diff post");
			/*
			 * already posted; warn user FIXME: this is bookmark-only and does 
			 * not work for publications
			 */
			errors.rejectValue("post.resource.url", "error.field.valid.url.alreadybookmarked");

			// set intraHash, diff post and set dbPost as post of command
			command.setIntraHashToUpdate(resource.getIntraHash());

			command.setDiffPost(post);

			this.populateCommandWithPost(command, dbPost);

			return true;
		}
		return false;		
	}

	/**
	 * Copy the groups from the command into the post (make proper groups from
	 * them)
	 * 
	 * @param command -
	 *            contains the groups as represented by the form fields.
	 * @param post -
	 *            the post whose groups should be populated from the command.
	 * @see #initCommandGroups(EditPostCommand, Post)
	 */
	private void initPostGroups(final EditPostCommand<RESOURCE> command, final Post<RESOURCE> post) {
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
	 * @param command -
	 *            the command whose groups should be populated from the post.
	 * @param post -
	 *            the post which contains the groups.
	 * @see #initPostGroups(EditPostCommand, Post)
	 */
	private void initCommandGroups(final EditPostCommand<RESOURCE> command, Set<Group> groups) {
		log.debug("groups from post: " + groups);
		final List<String> commandGroups = command.getGroups();
		commandGroups.clear();
		if (groups.contains(PRIVATE_GROUP)) {
			/*
			 * only private
			 */
			command.setAbstractGrouping(PRIVATE_GROUP.getName());
		} else if (groups.contains(PUBLIC_GROUP)) {
			/*
			 * only public
			 */
			command.setAbstractGrouping(PUBLIC_GROUP.getName());
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

	protected abstract EditPostValidator<RESOURCE> getValidator();

	public Errors getErrors() {
		return errors;
	}

	public void setErrors(final Errors errors) {
		this.errors = errors;
	}


	/**
	 * @return The tag recommender associated with this controller.
	 */
	public TagRecommender getTagRecommender() {
		return this.tagRecommender;
	}

	/**
	 * The tag recommender is necessary to allow giving it feedback about the
	 * post as it is stored in the database.
	 * 
	 * @param tagRecommender
	 */
	public void setTagRecommender(TagRecommender tagRecommender) {
		this.tagRecommender = tagRecommender;
	}

	/** Give this controller an instance of {@link Captcha}.
	 * 
	 * @param captcha
	 */
	@Required
	public void setCaptcha(Captcha captcha) {
		this.captcha = captcha;
	}

	/** Give this controller an instance of {@link RequestLogic}.
	 * 
	 * @param requestLogic
	 */
	@Required
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}



}
