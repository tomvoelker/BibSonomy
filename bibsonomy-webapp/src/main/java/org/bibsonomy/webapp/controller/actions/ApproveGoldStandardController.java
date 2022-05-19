/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.PostLogicInterface;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.actions.ApproveGoldStandardCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.RequestWrapperContext;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.ExtendedRedirectView;
import org.springframework.validation.Errors;

@Getter
@Setter
public class ApproveGoldStandardController implements MinimalisticController<ApproveGoldStandardCommand>, ErrorAware {

    private LogicInterface logic;
    private URLGenerator urlGenerator;

    private Errors errors;
    private String redirectUrl;

    @Override
    public View workOn(ApproveGoldStandardCommand command) {
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
            this.errors.reject("error.post.notfound", "Could not find post.");
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
            this.errors.reject("error.post.notfound", "Could not find post.");
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
    public ApproveGoldStandardCommand instantiateCommand() {
        return new ApproveGoldStandardCommand();
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
