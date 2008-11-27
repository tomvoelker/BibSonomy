package org.bibsonomy.webapp.controller.actions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
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
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.util.tagparser.TagString3Lexer;
import org.bibsonomy.model.util.tagparser.TagString3Parser;
import org.bibsonomy.recommender.RecommendedTag;
import org.bibsonomy.recommender.TagRecommender;
import org.bibsonomy.util.ValidationUtils;
import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.controller.SingleResourceListController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PostBookmarkValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

/**
 * @author fba
 * @version $Id: PostBookmarkController.java,v 1.1 2008-09-11 04:40:12
 *          ss05fbachmann Exp $
 */
public class PostBookmarkController extends SingleResourceListController implements MinimalisticController<EditBookmarkCommand>, ErrorAware, ValidationAwareController<EditBookmarkCommand> {
	private static final Log log = LogFactory.getLog(PostBookmarkController.class);
	private Errors errors = null;

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
		command.setRelevantGroups(new ArrayList<Tag>());
		command.setRelevantTagSets(new HashMap<String, Map<String,List<String>>>());
		command.setRecommendedTags(new TreeSet<RecommendedTag>());
		command.setCopytags(new ArrayList<Tag>());
		/*
		 * initialize post & resource
		 */
		command.setPost(new Post<Bookmark>());
		command.getPost().setResource(new Bookmark());		
		command.setAbstractGrouping("public");

		/*
		 * set default url.
		 */
		command.getPost().getResource().setUrl("http://");
		return command;
	}

	/** Main method which does the postBookmark-procedure.
	 *
	 * FIXME: for:group is missing
	 *
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(java.lang.Object)
	 */
	public View workOn(EditBookmarkCommand command) {
		log.debug("######## PostBookmarkController: workOn() called ########");
		log.debug("working on command " + command);

		command.setPageTitle("post a new bookmark");
		final RequestWrapperContext context = command.getContext();

		/*
		 * only users which are logged in might post bookmarks 
		 * -> send them to login page
		 * FIXME: add redirect to posting page (?referer=... ?)
		 */
		if (!context.isUserLoggedIn()) {
			log.debug("user not logged in -> redirect to /login");
			return new ExtendedRedirectView("/login");
		}

		final User loginUser = context.getLoginUser();
		final Post<Bookmark> post = command.getPost();

		/*
		 * initialize tag sets for groups
		 */
		initGroupTagSets(loginUser);

		/*
		 * set the user of the post to the loginUser (the recommender might need the user name)
		 */
		post.setUser(loginUser);

		/*
		 * get the recommended tags
		 */
		if (tagRecommender != null)	command.setRecommendedTags(tagRecommender.getRecommendedTags(post));
		
		/*
		 * get the tag cloud of the user
		 * (this must be done before any error checking, because the user must have this)
		 * FIXME: get ALL tags of the user!
		 */
		this.setTags(command, Resource.class, GroupingEntity.USER, loginUser.getName(), null, null, null, null, 0, 100, null);

		
		
		/*
		 * initialize groups 
		 * TODO: we need the other way around: when we get a post from the DB and put it
		 * into the command, we need to initialize the abstractGrouping, etc.
		 */
		initGroups(command, post);

		/*
		 * decide, what to do
		 */
		final String intraHashToUpdate = command.getIntraHashToUpdate();
		if (ValidationUtils.present(intraHashToUpdate)) {
			log.debug("intra hash to update found -> handling update of existing post");
			/*
			 * we're editing an existing post
			 */
			if (!context.isValidCkey()) {
				log.debug("no valid ckey found -> assuming first call, populating form");
				/*
				 * ckey is invalid, so this is probably the first call --> get post from DB
				 */
				final Post<Bookmark> dbPost = (Post<Bookmark>) logic.getPostDetails(intraHashToUpdate, loginUser.getName());
				/*
				 * TODO: check for dbPost == null!
				 */
				/*
				 * put it into command
				 * TODO: initialize abstractGrouping, etc.
				 * (like we do it with the tags in the sequel)
				 * 
				 */
				command.setPost(dbPost);
				/*
				 * create tag string for view input field
				 */
				command.setTags(getSimpleTagString(dbPost.getTags()));
				/*
				 * return to view
				 */
				return Views.POST_BOOKMARK;
				/*
				 * TODO: what, if the ckey really is the problem (i.e., it is broken?) and not the first call?
				 */
			}
			log.debug("ckey given, so parse tags, validate post, update post");
			/*
			 * ckey is given, so user is already editing the post  -> validate it
			 */
			try {
				post.setTags(parse(command.getTags()));
			} catch (Exception e) {
				errors.rejectValue("tags", "error.field.valid.tags.parseerror");
			}
			org.springframework.validation.ValidationUtils.invokeValidator(new PostBookmarkValidator(), command, errors);
			/*
			 * check, if the URL has changed
			 * TODO: what to do, if it has changed?
			 */
			post.getResource().recalculateHashes();
			/*
			 * return to form until validation passes
			 */
			if (errors.hasErrors()) {
				log.debug("returning to view because of errors: " + errors.getErrorCount());
				log.debug("post is " + post.getResource());
				return Views.POST_BOOKMARK;
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
			 * update date 
			 * TODO: don't we want to keep the posting date unchanged and only update the date?
			 */
			post.setDate(new Date());
			/*
			 * update post in DB
			 * FIXME: posting duplicates is possible! :-(
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
			 * TODO: when to redirect to post.getResource().getUrl()?
			 */
			try {
				return new ExtendedRedirectView("/user/" + URLEncoder.encode(loginUser.getName(), "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				log.error("Could not encode redirect URL.", ex);
				errors.reject("error encoding URL");
				return Views.ERROR;
			} 
		} 

		log.debug("no intra hash given -> new post");
		/*
		 * no intra hash given --> user posts a new entry (which might already exist!)
		 */

		/*
		 * check, if post already exists
		 */
		post.getResource().recalculateHashes();
		final Post<Bookmark> dbPost = (Post<Bookmark>) logic.getPostDetails(post.getResource().getIntraHash(), loginUser.getName());
		if (dbPost != null) {
			log.debug("user already owns this URL ... handling update");
			/*
			 * TODO: here we could check, if the user has marked a checkbox which says "I want to update this post"
			 * and only then set the intraHashToUpdate
			 */
			/*
			 * post exists -> warn user
			 */
			errors.rejectValue("post.resource.url", "error.field.valid.url.alreadybookmarked");
			/*
			 * the next time this will be handled as an update
			 * TODO: this does not make sense, since we don't show the user the existing post
			 * (actually: again, we can't exchange the post ... :-( ) 
			 */
			//command.setIntraHashToUpdate(post.getResource().getIntraHash());
			/*
			 * in any case: return to view
			 */
			return Views.POST_BOOKMARK;
		}
		log.debug("wow, post is completely new! So ... return until no errors and then store it");
		/*
		 * post is completely new --> return to view on error, otherwise store it.
		 */


		/*
		 * return to form until validation passes
		 */
		if (errors.hasErrors()) {
			log.debug("returning to view because of errors: " + errors.getErrorCount());
			log.debug("post is " + post.getResource());
			return Views.POST_BOOKMARK;
		}


		/*
		 * check credentials to fight CSRF attacks
		 * We do this that late to not cause the error message pop up 
		 * on the first call to the controller. Otherwise, the form would
		 * be empty and the hidden ckey field not sent.
		 */
		if (!context.isValidCkey()) {
			errors.reject("error.field.valid.ckey");
			return Views.POST_BOOKMARK;
		}

		/*
		 * TODO: why is the content id set?
		 */
//		post.setContentId(null);
		post.setDate(new Date());


		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(post);

		/*
		 * new post -> create
		 */
		log.debug("finally: creating a new post in the DB");
		final String createPosts = logic.createPosts(posts).get(0);
		log.debug("created post: " + createPosts);	


		/*
		 * check, if bookmark was posted by bookmarklet (jump = true) or not 
		 */
		String redirectURL = post.getResource().getUrl();
		if (!command.isJump()) {
			try {
				redirectURL = "/user/" + URLEncoder.encode(loginUser.getName(), "UTF-8");
			} catch (UnsupportedEncodingException ex) {
				log.error("Could not encode redirect URL.", ex);
			} 
		}
		return new ExtendedRedirectView(redirectURL);
	}

	/**
	 * Copy the groups from the command into the post (make proper groups from them)
	 * 
	 * @param command - contains the groups as represented by the form fields.
	 * @param post
	 */
	private void initGroups(EditBookmarkCommand command, final Post<Bookmark> post) {
		if(!command.getAbstractGrouping().endsWith("other")){
			/*
			 * if the post is private or public --> remove all groups and add one (private or public)
			 */
			post.getGroups().clear();
			post.getGroups().add(new Group(command.getAbstractGrouping()));
		} else {
			final List<String> groups = command.getGroups();
			for (final String groupname: groups){
				post.getGroups().add(new Group(groupname));
			}
		}
	}


	/** Concatenates the tags into one tag string to show them to the user.
	 * 
	 * @param tags
	 * @return A string representing the tags, separated by space
	 */
	private String getSimpleTagString(final Set<Tag> tags) {
		final StringBuffer s = new StringBuffer();
		for (final Tag tt: tags) {
			s.append(tt.getName() + " ");
		}
		return s.toString();
	}


	/** Parses the incoming tag string into a set of tags.
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

	public Errors getErrors() {
		/* here: check for binding errors */
		return errors;
	}

	public void setErrors(final Errors errors) {
		this.errors = errors;
	}

	public Validator<EditBookmarkCommand> getValidator() {
		return new PostBookmarkValidator();
	}

	/** 
	 * If user already posted the given URL and this is the first call (no ckey), 
	 * no validation is required - we will use the post from the DB.
	 * 
	 * 
	 * @see org.bibsonomy.webapp.util.ValidationAwareController#isValidationRequired(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public boolean isValidationRequired(EditBookmarkCommand command) {

		if (ValidationUtils.present(command.getIntraHashToUpdate())) {
			return false;
		}

		/*
		 * if user is posting an already bookmarked URL, no validation is required (really?)
		 */
//		final RequestWrapperContext context = command.getContext();
//		/*
//		* weak heuristics: no ckey, so probably first call to controller -> skip validation
//		*/
//		if (!context.isValidCkey()) {
//		log.debug("ckey NOT valid");
//		final Post<Bookmark> post = command.getPost();
//		if (command.getIntraHashToUpdate() != null) {
//		log.debug("command.getIntraHashToUpdate() != null");
//		final User loginUser = context.getLoginUser();
//		try {
//		final Post<? extends Resource> dbPost = logic.getPostDetails(command.getIntraHashToUpdate(), loginUser.getName());
//		if (dbPost != null) {
//		/*
//		* ckey != null && post exists: no validation
//		*/
//		log.debug("skipping validation");
//		return false;
//		} 
//		} catch (ResourceMovedException e) {
//		log.warn("resource moved", e);
//		}
//		} else if (post != null && post.getResource() != null && post.getResource().getUrl() != null) {
//		log.debug("post != null && post.getResource() != null && post.getResource().getUrl() != null");
//		post.getResource().recalculateHashes();
//		final User loginUser = context.getLoginUser();
//		try {
//		final Post<? extends Resource> dbPost = logic.getPostDetails(post.getResource().getIntraHash(), loginUser.getName());
//		if (dbPost != null) {
//		/*
//		* ckey != null && post exists: no validation
//		*/
//		log.debug("skipping validation");
//		return false;
//		} 
//		} catch (ResourceMovedException e) {
//		log.warn("resource moved", e);
//		}
//		}

//		}
		return true;
	}


	/** Gets the tagsets for each group from the DB and stores them in the 
	 * users group list.
	 * 
	 * @param loginUser
	 */
	private void initGroupTagSets(final User loginUser) {
		/* 
		 * Get tagsets for each group and add them to the loginUser object.
		 * Why into the loginUser? Because there we already have the groups 
		 * the user is member of.
		 */
		final List<Group> usersGroups = loginUser.getGroups();
		final ArrayList<Group> groupsWithTagSets = new ArrayList<Group>();
		for(final Group group: usersGroups){
			if(group.getName() != null){
				groupsWithTagSets.add(this.logic.getGroupDetails(group.getName()));
			}
		}
		loginUser.setGroups(groupsWithTagSets);

	}

	public TagRecommender getTagRecommender() {
		return this.tagRecommender;
	}

	public void setTagRecommender(TagRecommender tagRecommender) {
		Assert.notNull(tagRecommender, "The provided tag recommender must not be null.");
		this.tagRecommender = tagRecommender;
	}

}
