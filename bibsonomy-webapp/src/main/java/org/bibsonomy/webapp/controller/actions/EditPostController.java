/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.actions;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.ConceptStatus;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.RelevantForSystemTag;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandard;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelation;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.SimHash;
import org.bibsonomy.model.util.TagUtils;
import org.bibsonomy.recommender.connector.model.PostWrapper;
import org.bibsonomy.services.Pingback;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.ContextCommand;
import org.bibsonomy.webapp.command.actions.EditPostCommand;
import org.bibsonomy.webapp.controller.SingleResourceListController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.GroupingCommandUtils;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.util.captcha.Captcha;
import org.bibsonomy.webapp.util.captcha.CaptchaUtil;
import org.bibsonomy.webapp.util.spring.security.exceptions.AccessDeniedNoticeException;
import org.bibsonomy.webapp.validation.PostValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import recommender.core.Recommender;
import recommender.core.interfaces.model.TagRecommendationEntity;
import recommender.impl.database.RecommenderStatisticsManager;

/**
 * A generic edit post controller for any resource
 * 
 * NOTE: Do not import any subclasses of the {@link Resource} class!
 * 
 * @author fba
 * @param <RESOURCE>
 * @param <COMMAND>
 */
public abstract class EditPostController<RESOURCE extends Resource, COMMAND extends EditPostCommand<RESOURCE>> extends SingleResourceListController implements MinimalisticController<COMMAND>, ErrorAware {
	private static final Log log = LogFactory.getLog(EditPostController.class);

	private static final String TAGS_KEY = "tags";
	protected static final String LOGIN_NOTICE = "login.notice.post.";

	private Recommender<TagRecommendationEntity, recommender.impl.model.RecommendedTag> recommender;
	private Pingback pingback;
	private Captcha captcha;

	protected Errors errors;
	protected RequestLogic requestLogic;
	protected URLGenerator urlGenerator;

	/**
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	@Override
	public COMMAND instantiateCommand() {
		final COMMAND command = this.instantiateEditPostCommand();
		/*
		 * initialize lists
		 */
		GroupingCommandUtils.initGroupingCommand(command);
		command.setRelevantGroups(new ArrayList<String>());
		command.setRelevantTagSets(new HashMap<String, Map<String, List<String>>>());
		command.setRecommendedTags(new TreeSet<RecommendedTag>());
		command.setCopytags(new ArrayList<Tag>());
		command.setFileName(new ArrayList<String>());
		/*
		 * initialize post & resource
		 */
		command.setPost(new Post<RESOURCE>());
		command.getPost().setResource(this.instantiateResource());

		// history
		command.setDifferentEntryKeys(new ArrayList<String>());

		/*
		 * set default values.
		 */
		command.setPostID(RecommenderStatisticsManager.getUnknownEntityID());
		return command;
	}

	/**
	 * Instantiated the correct command for this controller.
	 * 
	 * @return
	 */
	protected abstract COMMAND instantiateEditPostCommand();

	/**
	 * Instantiates a resource which the controller puts into the commands post.
	 * 
	 * @return
	 */
	protected abstract RESOURCE instantiateResource();

	/**
	 * Main method which does the posting-procedure.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(ContextCommand)
	 */
	@Override
	public View workOn(final COMMAND command) {
		final RequestWrapperContext context = command.getContext();
		/*
		 * We store the referer in the command, to send the user back to the
		 * page he's coming from at the end of the posting process.
		 */
		if (!present(command.getReferer())) {
			command.setReferer(this.requestLogic.getReferer());
		}

		/*
		 * only users which are logged in might post -> send them to login page
		 */
		if (!this.canEditPost(context)) {
			throw new AccessDeniedNoticeException("please log in", LOGIN_NOTICE + command.getPost().getResource().getClass().getSimpleName().toLowerCase());
		}

		final User loginUser = context.getLoginUser();

		/*
		 * After having handled the general issues (login, referer, etc.), sub
		 * classes can now execute their workOn code
		 */
		this.workOnCommand(command, loginUser);

		/*
		 * If the user is a spammer, we check the captcha
		 */
		if (loginUser.isSpammer()) {
			/*
			 * check the captcha (if it is wrong, an error is added)
			 */
			CaptchaUtil.checkCaptcha(this.captcha, this.errors, log, command.getRecaptcha_challenge_field(), command.getRecaptcha_response_field(), this.requestLogic.getHostInetAddress());
		}

		/*
		 * handle copying of a post using intra hash + user name
		 */
		final String hash = command.getHash();
		final String user = command.getUser();
		if (present(hash)) {
			// the user can be empty => gold standard
			final Post<RESOURCE> post = this.getCopyPost(loginUser, hash, user);

			if (!present(post)) {
				this.errors.reject("error.post.notfound");
				return this.getEditPostView(command, loginUser);
			}

			command.setPost(post);
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

		final String intraHashToUpdate = command.getIntraHashToUpdate();

		if (present(intraHashToUpdate)) {
			log.debug("intra hash to update found -> handling update of existing post");
			return this.handleUpdatePost(command, context, loginUser, post, intraHashToUpdate);
		}

		log.debug("no intra hash given -> new post");
		return this.handleCreatePost(command, context, loginUser, post);
	}

	protected boolean canEditPost(final RequestWrapperContext context) {
		return context.isUserLoggedIn();
	}

	protected Post<RESOURCE> getCopyPost(final User loginUser, final String hash, final String user) {
		if (this.urlGenerator.matchesPage(this.requestLogic.getReferer(), URLGenerator.Page.INBOX)) {
			/*
			 * The user tries to copy a post from his inbox.
			 * 
			 * We need a special method to get this post, since it could happen
			 * that the user who owns the post already has deleted it (and thus
			 * we must check the log table to get the post).
			 */
			return this.getInboxPost(loginUser.getName(), hash, user);
		}
		/*
		 * regular copy
		 */
		return this.getPostDetails(hash, user);
	}

	/**
	 * Checks loginUser's inbox for the post with the given hash+user
	 * combination and returns the corresponding post. If no such post could be
	 * found, a {@link ObjectNotFoundException} exception is thrown.
	 * 
	 * @param loginUserName
	 *        - the name of the user whose inbox should be checked
	 * @param hash
	 *        - the hash of the post we want to find
	 * @param user
	 *        - the name of the user who owns the post (!= inbox user!)
	 * @return The post from the inbox.
	 * @throws ObjectNotFoundException
	 */

	@SuppressWarnings("unchecked")
	private Post<RESOURCE> getInboxPost(final String loginUserName, final String hash, final String user) throws ObjectNotFoundException {
		/*
		 * We can only give the name of the inbox's user and the hash to the
		 * database (there are no parameters available to further restrict the
		 * search to the user name of the post's owner). Thus, if the loginUser
		 * has several posts with the same hash in his inbox, we get them all
		 * and must compare each post against the given user name.
		 */

		final List<Post<RESOURCE>> dbPosts = new LinkedList<Post<RESOURCE>>();
		List<Post<RESOURCE>> tmp;
		int startCount = 0;
		final int step = PostLogicInterface.MAX_QUERY_SIZE;

		do {
			tmp = this.logic.getPosts((Class<RESOURCE>) this.instantiateResource().getClass(), GroupingEntity.INBOX, loginUserName, null, hash, null, SearchType.LOCAL, null, null, null, null, startCount, startCount + step);
			dbPosts.addAll(tmp);
			startCount += step;
		} while (tmp.size() == step);

		if (present(dbPosts)) {
			for (final Post<RESOURCE> dbPost : dbPosts) {
				/*
				 * check, if the post is owned by the user whose post we want to
				 * copy.
				 */
				if (user.equals(dbPost.getUser().getName())) {
					return dbPost;
				}
			}
		}

		throw new ObjectNotFoundException(hash);
	}

	protected abstract void workOnCommand(final COMMAND command, final User loginUser);

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
	 * @param command
	 *        - the command the controller is working on (and which is also
	 *        handed over to the view).
	 * @param loginUser
	 *        - the login user.
	 * @return The post view.
	 */
	protected View getEditPostView(final EditPostCommand<RESOURCE> command, final User loginUser) {
		/*
		 * initialize tag sets for groups
		 */
		this.initGroupTagSets(loginUser);

		/*
		 * get the tag cloud of the user (this must be done before any error
		 * checking, because the user must have this)
		 */
		this.setTags(command, Resource.class, GroupingEntity.USER, loginUser.getName(), null, null, null, 20000, null);
		/*
		 * get the relations of the user
		 */
		final List<Tag> concepts = this.logic.getConcepts(null, GroupingEntity.USER, loginUser.getName(), null, null, ConceptStatus.PICKED, 0, Integer.MAX_VALUE);
		command.getConcepts().setConceptList(concepts);

		/*
		 * prepare post from internal format into user's form format
		 */
		if (loginUser.isSpammer()) {
			/*
			 * Generate HTML to show captcha.
			 */
			command.setCaptchaHTML(this.captcha.createCaptchaHtml(this.requestLogic.getLocale()));
		}
		/*
		 * return the view
		 */
		return this.getPostView();
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
	private View handleUpdatePost(final COMMAND command, final RequestWrapperContext context, final User loginUser, final Post<RESOURCE> post, final String intraHashToUpdate) {
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
			final Post<RESOURCE> dbPost = this.getPostDetails(intraHashToUpdate, loginUserName);
			if (dbPost == null) {
				/*
				 * invalid intra hash: post could not be found
				 */
				this.errors.reject("error.post.notfound", "The post with the given intra hash could not be found.");
				return Views.ERROR;
			}

			// if the controller is called from history page
			if (present(command.getDifferentEntryKeys())) {
				/*
				 * TODO: why don't we use the update date of the post as
				 * identifier (instead of the compare version)? A greater than
				 * query is more effective as the limit and offset caused by the
				 * compare version
				 */
				// comparePost is the history revision which will be restored.
				final int compareVersion = command.getCompareVersion();
				@SuppressWarnings("unchecked")
				final Post<RESOURCE> comparePost = (Post<RESOURCE>) this.logic.getPosts(dbPost.getResource().getClass(), GroupingEntity.USER, this.getGrouping(loginUser), null, intraHashToUpdate, null, SearchType.LOCAL, FilterEntity.POSTS_HISTORY, null, null, null, compareVersion, compareVersion + 1).get(0);

				// TODO: why don't we set the dbPost = comparePost? why do we
				// have to restore all fields by hand?
				final List<String> diffEntryKeyList = command.getDifferentEntryKeys();
				for (int i = 0; i < diffEntryKeyList.size(); i++) {
					this.replacePostFields(dbPost, diffEntryKeyList.get(i), comparePost);
				}
			}

			/*
			 * put post into command
			 */
			this.populateCommandWithPost(command, dbPost);
			/*
			 * returning to view
			 */
			return this.getEditPostView(command, loginUser);
		}
		log.debug("ckey given, so parse tags, validate post, update post");
		/*
		 * ckey is given, so user is already editing the post -> parse tags
		 */
		this.preparePost(command, post);
		/*
		 * check, if the post has changed
		 */
		if (!intraHashToUpdate.equals(post.getResource().getIntraHash())) {
			/*
			 * post has changed -> check, if new post has already been posted
			 */
			final Post<RESOURCE> dbPost = this.getPostDetails(post.getResource().getIntraHash(), loginUserName);
			if (dbPost != null) {
				log.debug("user already owns this post ... handling update");
				/*
				 * post exists -> warn user
				 */
				this.setDuplicateErrorMessage(post, this.errors);
			}
		}
		/*
		 * return to form until validation passes
		 */
		if (this.errors.hasErrors()) {
			log.debug("returning to view because of errors: " + this.errors.getErrorCount());
			log.debug("post is " + post.getResource());
			return this.getEditPostView(command, loginUser);
		}
		/*
		 * the post to update has the given intra hash
		 */
		post.getResource().setIntraHash(command.getIntraHashToUpdate());

		List<String> updatePosts = null;
		try {
			/*
			 * update post in DB
			 */
			updatePosts = this.logic.updatePosts(Collections.<Post<?>> singletonList(post), PostUpdateOperation.UPDATE_ALL);
		} catch (final DatabaseException ex) {
			return this.handleDatabaseException(command, loginUser, post, ex, "update");
		}

		if (!present(updatePosts)) {
			/*
			 * show error page FIXME: when/why can this happen? We get some
			 * error messages here in the logs, but can't explain them.
			 */
			this.errors.reject("error.post.update", "Could not update post.");
			log.warn("could not update post");
			return Views.ERROR;
		}
		/*
		 * do everything that must be done after a successful create or update
		 */
		this.createOrUpdateSuccess(command, loginUser, post);
		/*
		 * send final redirect
		 */
		return this.finalRedirect(command, post, loginUserName);
	}

	/**
	 * Replace the field with key "key" in post with the corresponding value in
	 * newPost
	 * 
	 * @param post
	 * @param key
	 * @param newPost
	 */
	protected void replacePostFields(final Post<RESOURCE> post, final String key, final Post<RESOURCE> newPost) {
		switch (key) {
		case TAGS_KEY:
			post.setTags(newPost.getTags());
			break;
		case "description":
			post.setDescription(newPost.getDescription());
			break;
		case "approved":
			post.setApproved(newPost.getApproved());
			break;
		default:
			this.replaceResourceSpecificPostFields(post.getResource(), key, newPost.getResource());
		}
		if (newPost.getApproved()) {
			post.setApproved(true);
		}
	}

	/**
	 * Replace the field with key "key" in post with the corresponding value in
	 * newPost
	 * 
	 * @param resource
	 * @param key
	 * @param newResource
	 */
	protected abstract void replaceResourceSpecificPostFields(final RESOURCE resource, String key, RESOURCE newResource);

	/**
	 * @param command
	 * @param loginUser
	 * @param post
	 * @param ex
	 * @return
	 */
	private View handleDatabaseException(final EditPostCommand<RESOURCE> command, final User loginUser, final Post<RESOURCE> post, final DatabaseException ex, final String process) {
		final List<ErrorMessage> errorMessages = ex.getErrorMessages(post.getResource().getIntraHash());
		for (final ErrorMessage em : errorMessages) {
			this.errors.reject("error.post.update", "Could not " + process + " this post.");
			log.warn("could not " + process + " post because " + em.getDefaultMessage());
			return Views.ERROR;
		}
		return this.getEditPostView(command, loginUser);
	}

	/**
	 * The method {@link PostLogicInterface#getPostDetails(String, String)}
	 * throws an exception, if the post with the requested hash+user does not
	 * exist but once existed and now has been moved. Since we just want to
	 * check, if the post with the given hash exists NOW, we can ignore that
	 * exception and instead just return null.
	 * 
	 * @param intraHash
	 * @param userName
	 * @return
	 * @see {https://www.kde.cs.uni-kassel.de/mediawiki/index.php/Bibsonomy:
	 *      PostHashRedirect}
	 * @see {https://www.kde.cs.uni-kassel.de/mediawiki/index.php/Bibsonomy:
	 *      PostPublicationUmziehen
	 *      #gel.C3.B6schte.2Fge.C3.A4nderte_Posts_.28Hash-Redirect-Problem.29}
	 */
	@SuppressWarnings("unchecked")
	protected Post<RESOURCE> getPostDetails(final String intraHash, final String userName) {
		try {
			return (Post<RESOURCE>) this.logic.getPostDetails(intraHash, userName);
		} catch (final ResourceMovedException e) {
			/*
			 * getPostDetails() has a redirect mechanism that checks for posts
			 * in the log tables. If it find's a post with the given hash there,
			 * it throws an exception, giving the hash of the next post. We want
			 * to ignore this behavior, thus we ignore the exception
			 * 
			 * see
			 * https://www.kde.cs.uni-kassel.de/mediawiki/index.php/Bibsonomy
			 * :PostHashRedirect and
			 * https://www.kde.cs.uni-kassel.de/mediawiki/index
			 * .php/Bibsonomy:PostPublicationUmziehen
			 * #gel.C3.B6schte.2Fge.C3.A4nderte_Posts_
			 * .28Hash-Redirect-Problem.29
			 */
		}

		return null;
	}

	/**
	 * When we detect that the user has changed the post such that it is equal
	 * to an existing post, this method is called and shall provide the user
	 * with a meaningful error message.
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
	protected void preparePost(final EditPostCommand<RESOURCE> command, final Post<RESOURCE> post) {
		try {
			/*
			 * we use addAll here because there might already be system tags in
			 * the post which should not be overwritten
			 */
			post.getTags().addAll(TagUtils.parse(command.getTags()));
		} catch (final Exception e) {
			log.warn("error parsing tags", e);
			this.errors.rejectValue(TAGS_KEY, "error.field.valid.tags.parseerror", "Your tags could not be parsed.");
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

	/**
	 * Validates the post using the validator returned by
	 * {@link #getValidator()}.
	 * 
	 * @param command
	 */
	protected void validatePost(final EditPostCommand<RESOURCE> command) {
		ValidationUtils.invokeValidator(this.getValidator(), command, this.errors);
	}

	/**
	 * After validation, the controller can clean the post, i.e., normalize tags
	 * or so. This method recalculates the hashes for the post and should
	 * therefore be called <em>after</em> cleansing operations affecting the
	 * hashes have happened.
	 * 
	 * @param post
	 */
	protected void cleanPost(final Post<RESOURCE> post) {
		post.getResource().recalculateHashes();
	}

	/**
	 * Update recommender table such that recommendations are linked to the
	 * final post.
	 * 
	 * @param entity
	 *            - the final post as saved in the database.
	 * @param postID
	 *            - the ID of the post during the posting process.
	 */
	protected void setRecommendationFeedback(final TagRecommendationEntity entity, final int postID) {
		try {
			/*
			 * To allow the recommender to identify the post and connect it with
			 * the post we provided at recommendation time, we give it the post
			 * id using the contentid field.
			 */
			this.recommender.setFeedback(entity, null);
		} catch (final Exception ex) {
			log.warn("Could not connect post with recommendation.");
			/*
			 * fail silently to not confuse user with error 500 when recommender
			 * fails
			 */
		}
	}

	/**
	 * Create the final redirect after successful creating / updating a post. We
	 * redirect to the URL the user was initially coming from. If we don't have
	 * that URL (for whatever reason), we redirect to the user's page.
	 * 
	 * @param userName
	 *            - the name of the loginUser
	 * @param intraHash
	 *            - the intra hash of the created/updated post
	 * @param referer
	 *            - the URL of the page the user is initially coming from
	 * 
	 * @return
	 */
	protected View finalRedirect(final String userName, final Post<RESOURCE> post, final String referer) {
		/*
		 * If there is no referer URL given, or if we come from a
		 * postBookmark/postPublication page, redirect to the user's home page.
		 * FIXME: if we are coming from /bibtex/HASH* or /url/HASH* and the hash
		 * has changed, we should redirect to the corresponding new page
		 */
		if (!present(referer) || referer.matches(".*/postPublication$") || referer.matches(".*/postBookmark$") || referer.contains("/history/")) {
			return new ExtendedRedirectView(this.urlGenerator.getUserUrlByUserName(userName));
		}
		/*
		 * redirect to referer URL
		 */
		return new ExtendedRedirectView(referer);
	}

	private View handleCreatePost(final COMMAND command, final RequestWrapperContext context, final User loginUser, final Post<RESOURCE> post) {
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
			return this.getEditPostView(command, loginUser);
		}

		log.debug("wow, post is completely new! So ... return until no errors and then store it");

		/*
		 * parses the tags,
		 */
		this.preparePost(command, post);

		/*
		 * return to form until validation passes
		 */
		if (this.errors.hasErrors()) {
			log.debug("returning to view because of errors: " + this.errors.getErrorCount());
			log.debug("post is " + post.getResource());
			return this.getEditPostView(command, loginUser);
		}

		/*
		 * should the edit view been shown before saving the post?
		 */
		if (command.isEditBeforeSaving()) {
			return this.getEditPostView(command, loginUser);
		}

		/*
		 * check credentials to fight CSRF attacks
		 * 
		 * We do this that late to not cause the error message pop up on the
		 * first call to the controller. Otherwise, the form would be empty and
		 * the hidden ckey field not sent.
		 */
		if (!context.isValidCkey()) {
			this.errors.reject("error.field.valid.ckey", "The provided security token is invalid.");
			return this.getEditPostView(command, loginUser);
		}

		/*
		 * new post -> create
		 */
		try {
			// setting copyFrom if present
			if (present(command.getUser())) {
				post.setCopyFrom(command.getUser());
			}

			log.debug("finally: creating a new post in the DB");
			final String createdPost = this.logic.createPosts(Collections.<Post<?>> singletonList(post)).get(0);

			/*
			 * store intraHash for some later changes (file upload)
			 */
			command.setIntraHashToUpdate(createdPost);
			log.debug("created post: " + createdPost);
		} catch (final DatabaseException de) {
			return this.handleDatabaseException(command, loginUser, post, de, "create");
		}
		/*
		 * do everything that must be done after a successful create or update
		 */
		this.createOrUpdateSuccess(command, loginUser, post);

		return this.finalRedirect(command, post, loginUserName);
	}

	private View finalRedirect(final COMMAND command, final Post<RESOURCE> post, final String loginUserName) {
		if (present(command.getSaveAndRate())) {
			final String ratingUrl = this.urlGenerator.getCommunityRatingUrl(post);
			return new ExtendedRedirectView(ratingUrl);
			}
		/**
		 * if the user is adding a new thesis to a person's page, he should be redirected to that person's page
		 * */
		if (present(command.getPost().getResourcePersonRelations())){
			ResourcePersonRelation resourcePersonRelation = post.getResourcePersonRelations().get(post.getResourcePersonRelations().size()-1);
			return new ExtendedRedirectView(new URLGenerator().getPersonUrl(resourcePersonRelation.getPersonName().getPersonId(), PersonNameUtils.serializePersonName(resourcePersonRelation.getPersonName().getPerson().getMainName()), ((BibTex)post.getResource()).getSimHash2(), command.getRequestedUser(), PersonResourceRelation.AUTHOR.toString()));
			
		}
		return this.finalRedirect(loginUserName, post, command.getReferer());
	}

	/**
	 * After the (created or updated) post has been successfully stored in the
	 * database, this method is called. Subclasses can use it to add additional
	 * functionality. Per default, this method updates the recommender by giving
	 * it feedback about the assigned tags and sends the post to the pingback
	 * service (if one is provided).
	 * 
	 * 
	 * @param command
	 * @param loginUser
	 * @param post
	 *            - the post that has been stored in the database.
	 */
	protected void createOrUpdateSuccess(final COMMAND command, final User loginUser, final Post<RESOURCE> post) {
		/*
		 * update recommender table such that recommendations are linked to the
		 * final post
		 */
		final PostWrapper<RESOURCE> wrapper = new PostWrapper<RESOURCE>(post);
		wrapper.setId("" + command.getPostID());
		this.setRecommendationFeedback(wrapper, command.getPostID());
		/*
		 * Send a pingback/trackback for the public posted resource.
		 */
		if (present(this.pingback) && !loginUser.isSpammer() && GroupUtils.isPublicGroup(post.getGroups())) {
			this.pingback.sendPingback(post);
		}

		/* if this field is already initiated, it means that we have come from a person page ...
		 **/
		if(command.getPersonId()!=0){
			
			Person person = this.logic.getPersonById(command.getPersonId());
			PersonName personName = new PersonName();
			for(PersonName personname: person.getNames()) {
				if(personname.getLastName().contains(command.getPerson_lastName())){
					personName = personname;
					break;
				}
			}
			if(present(personName)){
				final ResourcePersonRelation resourcePersonRelation = new ResourcePersonRelation();
			
				resourcePersonRelation.setPersonNameId(personName.getId());
				resourcePersonRelation.setSimhash1(post.getResource().getInterHash());
				resourcePersonRelation.setSimhash2(post.getResource().getIntraHash());
				resourcePersonRelation.setPubOwner(loginUser.getName());
				resourcePersonRelation.setPersonName(personName);
				if(present(command.getPerson_role())){// if a supervisor is adding a new thesis
					resourcePersonRelation.setRelatorCode(command.getPerson_role().split("supervisor,")[1]);
				}
				else{
					resourcePersonRelation.setRelatorCode(PersonResourceRelation.AUTHOR.getRelatorCode());
				}
				
				this.logic.addResourceRelation(resourcePersonRelation);
				
				if(!present(command.getPost().getResourcePersonRelations())){
					command.getPost().setResourcePersonRelations(new ArrayList<ResourcePersonRelation>());
				}
				command.getPost().getResourcePersonRelations().add(resourcePersonRelation);
			}
		}
	
	}

	/**
	 * Populates the command with the given post. Ensures, that fields which
	 * depend on the post (like the tag string, or the groups) in the command
	 * are correctly filled.
	 * 
	 * @param command
	 * @param post
	 */
	protected void populateCommandWithPost(final EditPostCommand<RESOURCE> command, final Post<RESOURCE> post) {
		/*
		 * put post into command
		 */
		command.setPost(post);
		/*
		 * populate "relevant for" groups in command
		 */
		this.initCommandRelevantForGroups(command, post.getTags());
		/*
		 * populate groups in command
		 */
		GroupingCommandUtils.initCommandGroups(command, post.getGroups());
		/*
		 * create tag string for view input field (NOTE: this needs to be done
		 * after initializing the relevantFor groups, because there the
		 * relevantFor tags are removed from the post)
		 */
		command.setTags(TagUtils.toTagString(post.getTags(), " "));
		
		if (post.getApproved()) {
			command.setApproved(true);
		}
		
	}

	/**
	 * Initializes the relevant for groups in the command from the (system) tags
	 * of the post. Also removes the corresponding system tags from the post
	 * such that they're not shown in the tag input field.
	 * 
	 * @param command
	 * @param tags
	 */
	private void initCommandRelevantForGroups(final EditPostCommand<RESOURCE> command, final Set<Tag> tags) {
		if (!present(command.getRelevantGroups())) {
			command.setRelevantGroups(new ArrayList<String>());
		}
		final List<String> relevantGroups = command.getRelevantGroups();

		final Iterator<Tag> iterator = tags.iterator();
		while (iterator.hasNext()) {
			final String name = iterator.next().getName();
			if (SystemTagsUtil.isSystemTag(name, RelevantForSystemTag.NAME)) {
				relevantGroups.add(SystemTagsUtil.extractArgument(name));
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
		 * null check neccessary, because Spring sets the list to null, when no
		 * group has been selected. :-(
		 */
		if (relevantGroups != null) {
			for (final String relevantGroup : relevantGroups) {
				/*
				 * ignore groups the user is not a member of
				 */
				if (groups.contains(new Group(relevantGroup))) {
					tags.add(new Tag(SystemTagsUtil.buildSystemTagString(RelevantForSystemTag.NAME, relevantGroup)));
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
		GroupingCommandUtils.initGroups(command, post.getGroups());
		/*
		 * initialize relevantFor-tags FIXME: candidate for system tags
		 */
		this.initRelevantForTags(command, post);
		/*
		 * For each post process an unique identifier is generated. This is used
		 * for mapping posts to recommendations.
		 */
		if (command.getPostID() == RecommenderStatisticsManager.getUnknownEntityID()) {
			command.setPostID(RecommenderStatisticsManager.getNewPID());
		}
	}

	/**
	 * checks if the user already bookmarked the resource of the command if the
	 * user owns the resource => diff post will be set
	 * 
	 * @param command
	 * @return <code>true</code> iff user already owns resource
	 */
	protected boolean setDiffPost(final EditPostCommand<RESOURCE> command) {
		final RequestWrapperContext context = command.getContext();
		final Post<RESOURCE> post = command.getPost();
		final String loginUserName = context.getLoginUser().getName();
		final RESOURCE resource = post.getResource();
		resource.recalculateHashes();

		this.prepareResourceForDatabase(resource);

		/*
		 * is resource already owned by the user?
		 */
		final Post<RESOURCE> dbPost = this.getPostDetails(resource.getIntraHash(), loginUserName);

		if (dbPost != null) {
			log.debug("set diff post");
			/*
			 * already posted; warn user
			 */
			this.setDuplicateErrorMessage(dbPost, this.errors);

			// set intraHash, diff post and set dbPost as post of command
			command.setIntraHashToUpdate(resource.getIntraHash());

			command.setDiffPost(post);

			this.populateCommandWithPost(command, dbPost);

			return true;
		}

		return false;
	}

	// FIXME: find a more suitable name for this method
	protected void prepareResourceForDatabase(final RESOURCE resource) {
		if (resource instanceof GoldStandard<?>) {
			resource.setIntraHash(SimHash.getSimHash(resource, HashID.INTRA_HASH));
		}
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
		final List<Group> groupsWithTagSets = new ArrayList<Group>();
		for (final Group group : usersGroups) {
			if (group.getName() != null) {
				groupsWithTagSets.add(this.logic.getGroupDetails(group.getName()));
			}
		}
		loginUser.setGroups(groupsWithTagSets);

	}

	protected abstract PostValidator<RESOURCE> getValidator();

	/**
	 * Returns the userName. Override in GoldStandard Controllers
	 * 
	 * @param requestedUser
	 * @param post
	 * @return
	 */
	protected String getGrouping(final User requestedUser) {
		return requestedUser.getName();
	}

	@Override
	public Errors getErrors() {
		return this.errors;
	}

	@Override
	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	/**
	 * @return The tag recommender associated with this controller.
	 */
	public Recommender<TagRecommendationEntity, recommender.impl.model.RecommendedTag> getTagRecommender() {
		return this.recommender;
	}

	/**
	 * The tag recommender is necessary to allow giving it feedback about the
	 * post as it is stored in the database.
	 * 
	 * @param tagRecommender
	 */
	public void setRecommender(final Recommender<TagRecommendationEntity, recommender.impl.model.RecommendedTag> tagRecommender) {
		this.recommender = tagRecommender;
	}

	/**
	 * Give this controller an instance of {@link Captcha}.
	 * 
	 * @param captcha
	 */
	@Required
	public void setCaptcha(final Captcha captcha) {
		this.captcha = captcha;
	}

	/**
	 * Give this controller an instance of {@link RequestLogic}.
	 * 
	 * @param requestLogic
	 */
	@Required
	public void setRequestLogic(final RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

	/**
	 * Sets a string attribute in the session.
	 * 
	 * @param key
	 * @param value
	 */
	protected void setSessionAttribute(final String key, final Object value) {
		this.requestLogic.setSessionAttribute(key, value);
	}

	/**
	 * Gets a string attribute from the session.
	 * 
	 * @param key
	 * @return
	 */
	protected Object getSessionAttribute(final String key) {
		return this.requestLogic.getSessionAttribute(key);
	}

	/**
	 * Set the URLGenerator to be used to generate (redirect) URLs.
	 * 
	 * @param urlGenerator
	 */
	@Required
	public void setUrlGenerator(final URLGenerator urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	/**
	 * A service that sends pingbacks / trackbacks to posted URLs.
	 * 
	 * @param pingback
	 */
	public void setPingback(final Pingback pingback) {
		this.pingback = pingback;
	}


}
