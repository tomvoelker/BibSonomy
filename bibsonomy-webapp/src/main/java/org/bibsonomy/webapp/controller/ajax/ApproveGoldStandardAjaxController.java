package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.GroupLevelPermission;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.common.enums.Role;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.common.exceptions.DatabaseException;
import org.bibsonomy.common.exceptions.ObjectMovedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.ajax.ApproveGoldStandardAjaxCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.validation.Errors;

@Getter
@Setter
public class ApproveGoldStandardAjaxController extends AjaxController implements MinimalisticController<ApproveGoldStandardAjaxCommand>, ErrorAware {

    private Errors errors;
    private URLGenerator urlGenerator;
    private String redirectUrl;

    @Override
    public View workOn(ApproveGoldStandardAjaxCommand command) {
        final RequestWrapperContext context = command.getContext();
        final User loggedInUser = context.getLoginUser();

        // check, if logged in
        if (!context.isUserLoggedIn()) {
            throw new AccessDeniedException();
        }

        final String username = command.getCopyFrom();
        final String intrahash = command.getIntrahash();
        final String interhash = command.getInterhash();

        // set redirect url
        if (present(interhash)) {
            this.redirectUrl = this.urlGenerator.getPublicationUrlByInterHash(interhash);
        } else if (present(interhash)) {
            this.redirectUrl = this.urlGenerator.getMyHomeUrl();
        }

        // check, if valid ckey
        if (context.isValidCkey()) {
            // check, if allowed to approve the community post
            if (loggedInUser.hasGroupLevelPermission(GroupLevelPermission.COMMUNITY_POST_INSPECTION) || loggedInUser.getRole() == Role.ADMIN) {
                if (present(intrahash) & present(username)) {
                    this.handleCopyAndApprove(intrahash, username);
                } else if (present(interhash)) {
                    this.handleEditAndApprove(interhash);
                }
            } else {
                this.errors.reject("error.goldstandard.approve.permission_denied");
            }
        } else {
            this.errors.reject("error.field.valid.ckey");
        }

        return new ExtendedRedirectView(redirectUrl);
    }

    private void handleEditAndApprove(final String interhash) {
        // get details with no username, since we are retrieving an existing gold standard
        Post<BibTex> postToUpdate = this.getPostDetails(interhash, "");
        if (!present(postToUpdate)) {
            this.errors.reject("error.post.update", "Could not update post.");
            return;
        }

        // set interhash as intrahash for goldstandard, because intrahashToUpdate is actually INTERhashToUpdate
        postToUpdate.getResource().setIntraHash(interhash);
        // set approved
        postToUpdate.setApproved(true);

        try {
            /*
             * update post in DB
             */
            final List<JobResult> updateResults = this.logic.updatePosts(Collections.singletonList(postToUpdate), PostUpdateOperation.UPDATE_ALL);

            if (Status.FAIL.equals(updateResults.get(0).getStatus())) {
                /*
                 * show error page FIXME: when/why can this happen? We get some
                 * error messages here in the logs, but can't explain them.
                 */
                this.errors.reject("error.post.update", "Could not update post.");
            }

        } catch (final DatabaseException ex) {
            this.errors.reject("error.post.update", "Could not update post.");
        }
    }

    private void handleCopyAndApprove(final String intrahash, final String username) {
        Post<BibTex> postToCreate = this.getPostDetails(intrahash, username);
        if (!present(postToCreate)) {
            this.errors.reject("error.post.update", "Could not update post.");
            return;
        }
        postToCreate.setApproved(true);
        try {
            // setting copyFrom if present
            if (present(username)) {
                postToCreate.setCopyFrom(username);
            }

            final List<JobResult> results = this.logic.createPosts(Collections.singletonList(postToCreate));

        } catch (final DatabaseException de) {
            this.errors.reject("error.post.update", "Could not update post.");
        }
    }

    /**
     * The method {@link PostLogicInterface#getPostDetails(String, String)}
     * throws an exception, if the post with the requested hash+user does not
     * exist but once existed and now has been moved. Since we just want to
     * check, if the post with the given hash exists NOW, we can ignore that
     * exception and instead just return null.
     */
    @SuppressWarnings("unchecked")
    private Post<BibTex> getPostDetails(final String intraHash, final String userName) {
        try {
            Post<BibTex> post = (Post<BibTex>) this.logic.getPostDetails(intraHash, userName);
            return BibTexUtils.convertToGoldStandard(post);
        } catch (final ObjectMovedException e) {
            // noop
        }

        return null;
    }

    @Override
    public ApproveGoldStandardAjaxCommand instantiateCommand() {
        return new ApproveGoldStandardAjaxCommand();
    }

    @Override
    public Errors getErrors() {
        return this.errors;
    }

    @Override
    public void setErrors(Errors errors) {
        this.errors = errors;
    }
}
