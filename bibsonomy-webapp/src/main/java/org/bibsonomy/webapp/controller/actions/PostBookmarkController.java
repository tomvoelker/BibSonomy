package org.bibsonomy.webapp.controller.actions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
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
		command.getPost().setGroups(Collections.singleton(new Group("public")));
		/*
		 * set default url.
		 */
		command.getPost().getResource().setUrl("http://");
		return command;
	}

	/** Main method which does the postBookmark-procedure.
	 *
	 * TODO: checking of ckey!
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(java.lang.Object)
	 */
	public View workOn(EditBookmarkCommand command) {
		log.debug("--> PostBookmarkController: workOn() called");
		if (log.isDebugEnabled() && command.getPost() != null && command.getPost().getResource() != null) {
			log.debug("url = " + command.getPost().getResource().getUrl());
		}

		command.setPageTitle("post a new bookmark");
		final RequestWrapperContext context = command.getContext();

		if (!context.isUserLoggedIn()) {
			log.debug("--> PostBookmarkController: workOn() called -> User not logged in -> Redirect to /login");
			return new ExtendedRedirectView("/login");
		}

		final User loginUser = context.getLoginUser();
		final Post<Bookmark> post = command.getPost();
		
		/*
		 * get tag recommendations 
		 * (this must be done before any error checking, because the user must have this)
		 */
		if (tagRecommender != null) {
			final SortedSet<RecommendedTag> recommendedTags = tagRecommender.getRecommendedTags(post);
			log.debug("got " + recommendedTags.size() + " recommended tags");
			command.setRecommendedTags(recommendedTags);
		}
		/*
		 * get the tag cloud of the user
		 * (this must be done before any error checking, because the user must have this)
		 * FIXME: get ALL tags of the user!
		 */
		this.setTags(command, Resource.class, GroupingEntity.USER, loginUser.getName(), null, null, null, null, 0, 100, null);
		log.debug("got " + command.getTagcloud().getTags().size() + " tags for user tag cloud.");

		/*
		 * check for duplicate posts
		 */
		post.getResource().recalculateHashes();
		if (logic.getPostDetails(post.getResource().getIntraHash(), loginUser.getName()) != null)  {
			errors.rejectValue("post.resource.url", "error.field.valid.url.alreadybookmarked");
		}

		/*
		 * parse the tags
		 * TODO: add proper error handling (key for message still missing!) 
		 */
		try {
			post.setTags(parse(command.getTags()));
		} catch (Exception e) {
			errors.rejectValue("tags", "error.field.valid.tags.parseerror");
		}
		
		/*
		 * return to form until validation passes
		 */
		if (errors.hasErrors()) {
			log.debug("returning to view because of errors: " + errors.getErrorCount());
			log.debug("post is " + post.getResource());
			return Views.POST_BOOKMARK;
		}


		/*
		 * copy the groups of the post into the post (make proper groups from them)
		 */
		final List<String> groups = command.getGroups();
		for (final String groupname: groups){
			post.getGroups().add(new Group(groupname));
		}

		
		/*
		 * TODO: why is the content id set?
		 */
//		post.setContentId(null);
		post.setDate(new Date());
		post.setUser(loginUser);

		/*
		 * store post
		 * TODO: how are updates handled?
		 */
		final List<Post<?>> posts = new LinkedList<Post<?>>();
		posts.add(post);
		final String out = logic.createPosts(posts).get(0);
		log.debug("posted hash: " + out);

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

	/** Returns, if validation is required for the given command. On default,
	 * for all incoming data validation is required.
	 * 
	 * @see org.bibsonomy.webapp.util.ValidationAwareController#isValidationRequired(java.lang.Object)
	 */
	public boolean isValidationRequired(EditBookmarkCommand command) {
		return true;
	}

	public TagRecommender getTagRecommender() {
		return this.tagRecommender;
	}

	public void setTagRecommender(TagRecommender tagRecommender) {
		Assert.notNull(tagRecommender, "The provided tag recommender must not be null.");
		this.tagRecommender = tagRecommender;
	}

}
