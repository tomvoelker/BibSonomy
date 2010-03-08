package org.bibsonomy.webapp.controller.admin;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.AdminActions;
import org.bibsonomy.common.enums.ClassifierSettings;
import org.bibsonomy.common.enums.FilterEntity;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.UserUpdateOperation;
import org.bibsonomy.common.exceptions.ValidationException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.EvaluatorUser;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.webapp.command.ajax.AdminAjaxCommand;
import org.bibsonomy.webapp.controller.AjaxController;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.ValidationAwareController;
import org.bibsonomy.webapp.util.Validator;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.validation.AdminActionsValidator;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * Controller for ajax requests on admin pages
 * 
 * @author Stefan Stützer
 * @author Beate Krause
 * @version $Id$
 */
public class AdminAjaxController extends AjaxController implements MinimalisticController<AdminAjaxCommand> , ErrorAware, ValidationAwareController<AdminAjaxCommand>{

	
	private static final Log log = LogFactory.getLog(AdminAjaxController.class);
	
	private Errors errors;

	public View workOn(AdminAjaxCommand command) {

		final RequestWrapperContext context = command.getContext();

		/* Check user role
		 * If user is not logged in or not an admin: show error message */
		if (!context.isUserLoggedIn() || !Role.ADMIN.equals(context.getLoginUser().getRole())) {
			throw new ValidationException("error.method_not_allowed");
		}
		
		final String action = command.getAction();
		
		log.debug("Action: " + action);
		
		/*
		 * validate fields before values are entered into database
		 */
		org.springframework.validation.ValidationUtils.invokeValidator(getValidator(), command, errors);
		
		/*
		 * return to form until validation passes
		 */
		if (errors.hasErrors()) {
			/*
			 * Do not update database as some input fields contain errors
			 */
			this.setResponse(command, "Error in input: " + errors.getFieldError().getObjectName() + " " + errors.getFieldError().getRejectedValue());
			return Views.AJAX;
		}
		/*
		 * 	
		 */
	
		switch (AdminActions.getAdminAction(action)) {
		case FLAG_SPAMMER:
			log.debug("flag spammer");
			this.flagSpammer(command, true);
			this.setResponse(command, command.getUserName() + " flagged as spammer");
			break;
		case UNFLAG_SPAMMER:
			log.debug("unflag spammer");
			this.flagSpammer(command, false);
			this.setResponse(command, command.getUserName() + " flagged as nonspammer");
			break;
		case MARK_UNCERTAINUSER:
			log.debug("here to mark uncertain user");
			this.flagUnsureSpammer(command, false);
			this.setResponse(command, command.getUserName() + " flagged as uncertain user");
			break;
		case LATEST_POSTS:
			log.debug("Get latest posts");
			this.setLatestPosts(command);
			return Views.AJAX_POSTS;
		case PREDICTION_HISTORY:
			log.debug("Get prediction history");
			this.setPredictionHistory(command);
			return Views.AJAX_PREDICTIONS;
		case UPDATE_SETTINGS:
			this.updateSettings(command);
			this.setResponse(command, command.getKey() + " updated");
			break;
		default:
			break;
		}
		
		
	/*	
	 * TODO Discuss evaluator interface
	} else if ("flag_spammer_evaluator".equals(action)) {
		this.flagSpammerEvaluator(command, true);
		this.setResponse(command, command.getUserName() + " flagged as spammer");
	} else if ("unflag_spammer_evaluator".equals(action)) {
		this.flagSpammerEvaluator(command, false);
		this.setResponse(command, command.getUserName() + " flagged as nonspammer");
	} */
		
		return Views.AJAX;
		
	}

	private void flagSpammerEvaluator(AdminAjaxCommand cmd, boolean spammer) {
		if (cmd.getUserName() != null) {

			EvaluatorUser user = new EvaluatorUser(cmd.getUserName());
			user.setToClassify(9);
			user.setAlgorithm("admin");
			user.setSpammer(spammer);
			user.setEvaluator(cmd.getEvaluator());

			// set date for evaluation (table is not normalized)
			String number = cmd.getEvaluator().substring(cmd.getEvaluator().indexOf("evaluator") + 9, cmd.getEvaluator().length());
			String evalDate = "date".concat(number);
			
			user.setEvalDate(evalDate);
			this.logic.updateUser(user, UserUpdateOperation.UPDATE_ALL);
		}
	}

	/**
	 * flags a user as spammer
	 * 
	 * @param cmd
	 */
	private void flagSpammer(AdminAjaxCommand cmd, boolean spammer) {
		if (cmd.getUserName() != null) {
			User user = new User(cmd.getUserName());
			user.setToClassify(0);
			user.setAlgorithm("admin");
			user.setSpammer(spammer);
			this.logic.updateUser(user, UserUpdateOperation.UPDATE_ALL);
		}
	}

	/**
	 * marks a user of not being a sure non-spammer
	 * 
	 * @param cmd
	 */
	private void flagUnsureSpammer(AdminAjaxCommand cmd, boolean spammer) {
		if (cmd.getUserName() != null) {
			User user = new User(cmd.getUserName());
			user.setToClassify(1);
			user.setAlgorithm("admin");
			user.setSpammer(spammer);
			user.setPrediction(SpamStatus.UNKNOWN.getId());
			this.logic.updateUser(user, UserUpdateOperation.UPDATE_ALL);
		}
	}

	/*
	 * updates settings of the classifier, for example algorithm, day- or night mode
	 */
	private void updateSettings(AdminAjaxCommand command) {
		if (command.getKey() != null && command.getValue() != null) {
			ClassifierSettings setting = ClassifierSettings.getClassifierSettings(command.getKey());
			this.logic.updateClassifierSettings(setting, command.getValue());
		}
	}

	private void setLatestPosts(AdminAjaxCommand command) {

		if (command.getUserName() != null && command.getUserName() != "") {
			// set filter to display spam posts
			FilterEntity filter = null;
			if (command.getShowSpamPosts().equals("true")) {
				filter = FilterEntity.ADMIN_SPAM_POSTS;
			}
			List<Post<Bookmark>> bookmarks = this.logic.getPosts(Bookmark.class, GroupingEntity.USER, command.getUserName(), null, null, Order.ADDED, filter, 0, 5, null);
			command.setBookmarks(bookmarks);

			int totalBookmarks = this.logic.getPostStatistics(Bookmark.class, GroupingEntity.USER, command.getUserName(), null, null, null, filter, 0, 100, null, null);
			command.setBookmarkCount(totalBookmarks);

			int totalBibtex = this.logic.getPostStatistics(BibTex.class, GroupingEntity.USER, command.getUserName(), null, null, null, filter, 0, 10000, null, null);
			command.setBibtexCount(totalBibtex);
		}
	}

	private void setPredictionHistory(AdminAjaxCommand command) {
		if (command.getUserName() != null && command.getUserName() != "") {
			List<User> predictions = this.logic.getClassifierHistory(command.getUserName());
			command.setPredictionHistory(predictions);
		}
	}

	public AdminAjaxCommand instantiateCommand() {
		return new AdminAjaxCommand();
	}

	@Override
	public Errors getErrors() {
		return errors;
	}

	@Override
	public void setErrors(Errors errors) {
		this.errors = errors;
		
	}

	@Override
	public Validator<AdminAjaxCommand> getValidator() {
		return new AdminActionsValidator();
	}

	@Override
	public boolean isValidationRequired(AdminAjaxCommand command) {
		// TODO Auto-generated method stub
		return false;
	}
	
}