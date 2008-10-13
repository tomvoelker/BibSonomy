package org.bibsonomy.webapp.controller.actions;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.UserSettings;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.tagparser.TagString3Lexer;
import org.bibsonomy.model.util.tagparser.TagString3Parser;
import org.bibsonomy.webapp.command.actions.EditBookmarkCommand;
import org.bibsonomy.webapp.controller.SingleResourceListController;
import org.bibsonomy.webapp.controller.SingleResourceListControllerWithTags;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestAware;
import org.bibsonomy.webapp.util.RequestLogic;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.PostBookmarkValidator;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.bibsonomy.webapp.view.Views;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;

/**
 * <pre>
 * TODO: 
 * - "Fehler" nicht beim ersten Seiten-Aufruf anzeigen 
 * - "viewable for" mit übergeben und verarbeiten 
 * - Spezialfall, wenn Scrapbare-Seite gebookmarked werden soll 
 *   - <c:when test="${not empty scraped}"> 
 *   - http://bibsonomy.org/scraperinfo
 * - ersetzen in allen jsp(x)-Dateien:
 *   - ${mtl:ch('nbsp')} --> &nbsp;
 * </pre>
 * 
 * 
 * @author fba
 * @version $Id: PostBookmarkController.java,v 1.1 2008-09-11 04:40:12
 *          ss05fbachmann Exp $
 */
public class PostBookmarkController extends SingleResourceListController implements MinimalisticController<EditBookmarkCommand>, ErrorAware, ValidationAwareController<EditBookmarkCommand>, RequestAware {

	private static final Logger log = Logger.getLogger(PostBookmarkController.class);

	private Errors errors = null;
	private RequestLogic requestLogic;

	/** 
	 * Returns an instance of the command the controller handles.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#instantiateCommand()
	 */
	public EditBookmarkCommand instantiateCommand() {
		return new EditBookmarkCommand();
	}

	/** Main method which does the postBookmark-procedure.
	 * 
	 * @see org.bibsonomy.webapp.util.MinimalisticController#workOn(java.lang.Object)
	 */
	
	public View workOn(EditBookmarkCommand command) {
		log.debug("--> PostBookmarkController: workOn() called");

		command.setPageTitle("post a new bookmark");
		final RequestWrapperContext context = command.getContext();

		
		Post<Bookmark> post = command.getPostBookmark();
		
		if (!context.isUserLoggedIn()) {
			log.debug("--> PostBookmarkController: workOn() called -> User not logged in -> Redirect to /login");
			return new ExtendedRedirectView("/login");
		}
		
		final User loginUser = context.getLoginUser();
		//cheat, bis wir die recommended tags kriegen
		final List<String> recommendedTags = new ArrayList<String>();

		for(Group group: loginUser.getGroups()){
				recommendedTags.add(group.getName());
				String s[] = {"A","B","C","D","E"};
				TreeMap<String, List<String>> set = new TreeMap<String, List<String>>();
				for(int j = 0; j < 5; j++){
					String listName = "Liste"+s[j];
					ArrayList<String> tagset = new ArrayList<String>();
					set.put(listName, tagset);
					for(int i = 0; i < 3; i++){
						tagset.add("tag"+i);
					}
					j++;
				}
				command.getRelevantTagSets().put(group.getName(),set);
			
		}
		this.setTags(command, Resource.class, GroupingEntity.ALL, null, null, null, null, null, 0, 100, null);
		command.setRecommendedTags(recommendedTags);

		//be aware of double postings
		command.getPostBookmark().getResource().recalculateHashes();
		String interHash = command.getPostBookmark().getResource().getInterHash();
		
		if (logic.getPostDetails(interHash, loginUser.getName()) != null)  {
			errors.rejectValue("postBookmark.resource.url", "error.field.valid.url.alreadybookmarked");
		}
		
		
		/*
		 * return to form until validation passes
		 */
		if (errors.hasErrors()) {
			return Views.POST_BOOKMARK;
		}
		
		
		//groups
		final Group group = new Group();
		group.setDescription(null);
		group.setName("public");
		post.getGroups().add(group);
//		post.setGroupid(groupman.getGroup(context.getLoginUser(), request.getParameter("group")));
		
		//tags
		Set<Tag> t = parse(command.getTags());
		post.setTags(t);
		System.out.println("--> PostBookmarkController: workOn() setTags: " + getTagsAsStringSet(t));
		
		post.setContentId(null);
		post.setDate(new Date());
		post.setUser(context.getLoginUser());
//		post.setResource(ModelUtils.getBookmark());
		
		System.out.println("--> PostBookmarkController: workOn() insert into database");
		String out = logic.createPost(command.getPostBookmark());
		System.out.println("out: " + out);
		
		/*
		 * check, if bookmark was posted by bookmarklet (jump = true) or not 
		 */
		String redirectURL;
//		if (b.isJump()) {
//			/*
//			 * posted by bookmarklet --> don't change an existing bookmark, but make
//			 * a copy of it (with the new, changed URL) 
//			 */
//			change = false;
			redirectURL = command.getPostBookmark().getResource().getUrl();
//		} else {
			try {
				redirectURL = "/user/" + URLEncoder.encode(context.getLoginUser().getName(), "UTF-8");
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			} 
//		}
		
		return new ExtendedRedirectView(redirectURL);
	}
	
	private  Set<String> getTagsAsStringSet(Collection<Tag> tags) {
		TreeSet<String> tagsStringSet = new TreeSet<String>();
		for (Tag tag : tags) {
			tagsStringSet.add(tag.getName());
		}
		return tagsStringSet;
	}
	
	private Set<Tag> parse(String tagString) {
		Set<Tag> tags = new TreeSet<Tag>();
		
		if (tagString != null) {
			CommonTokenStream tokens = new CommonTokenStream();
			tokens.setTokenSource(new TagString3Lexer(new ANTLRStringStream(tagString)));
			TagString3Parser parser = new TagString3Parser(tokens, tags);
			try {
				parser.tagstring();
            } catch (RecognitionException e) {
                System.out.println(e);
                e.printStackTrace();
            }
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

	/** The logic needed to access the request
	 * @param requestLogic 
	 */
	@Required
	public void setRequestLogic(RequestLogic requestLogic) {
		this.requestLogic = requestLogic;
	}

}
