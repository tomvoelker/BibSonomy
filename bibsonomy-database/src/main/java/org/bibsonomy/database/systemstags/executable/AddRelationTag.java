/**
 * BibSonomy-Database - Database for BibSonomy.
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.bibsonomy.database.systemstags.executable;

import static org.bibsonomy.util.ValidationUtils.present;
import lombok.Setter;
import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.User;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.logic.querybuilder.ResourcePersonRelationQueryBuilder;
import org.bibsonomy.model.util.PersonNameUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
public class AddRelationTag extends AbstractSystemTagImpl implements ExecutableSystemTag {

    private static final boolean toHide = true;
    public static final String NAME = "rel";

    /**
     * MailUtils instance from bibsonomy-web-common module.
     * Uses Object type and reflection to avoid compile-time dependency on bibsonomy-web-common
     */
    private Object mailUtils;
    private boolean mailingEnabled;
    private String receiverMail;

    /**
     * Flag to track if reflection-based mail sending has failed.
     * Set to true after first reflection error to avoid repeated expensive reflection attempts.
     */
    private boolean reflectionMailFailed = false;

    /**
     * Flag to track if we've already logged the reflection error.
     * Ensures we only log the detailed warning once.
     */
    private boolean reflectionErrorLogged = false;

    /**
     * Counter for suppressed reflection errors (for metrics/monitoring).
     */
    private int suppressedReflectionErrorCount = 0;

    @Override
    public <T extends Resource> void performBeforeCreate(Post<T> post, DBSession session) {
        // noop
    }

    @Override
    public <T extends Resource> void performBeforeUpdate(Post<T> newPost, Post<T> oldPost, PostUpdateOperation operation, DBSession session) {
        // noop
    }

    @Override
    public <T extends Resource> void performAfterCreate(Post<T> post, DBSession session) {
        final User loggedInUser = post.getUser();

        // Check, if user has permission to
        if (!this.hasPermissions()) {
            log.debug("no permission to add relation to person");
            return;
        }

        // Check, if post is a gold standard for a publication
        if (post.getResource() instanceof BibTex) {
            log.debug("add relation to person");
            final PersonDatabaseManager personDb = PersonDatabaseManager.getInstance();
            final String personId = this.getArgument();
            final Person person = personDb.getPersonById(personId, session);

            if (present(person)) {
                // Create person resource relation as AUTHOR
                ResourcePersonRelation relation = new ResourcePersonRelation();
                relation.setPost((Post<? extends BibTex>) post);
                relation.setPerson(person);
                relation.setRelationType(PersonResourceRelationType.AUTHOR);
                relation.setChangedBy(loggedInUser.getName());
                relation.setChangedAt(new Date());

                // Find author index in the post and add relation when found
                final BibTex resource = (BibTex) post.getResource();
                final List<PersonName> authorList = resource.getAuthor();
                List<Integer> matchingAuthorPos = new ArrayList<>();
                for (int i = 0; i < authorList.size(); i++) {
                    if (PersonNameUtils.containsPerson(authorList.get(i), person.getNames(), true)) {
                        matchingAuthorPos.add(i);
                    }
                }

                if (present(matchingAuthorPos)) {
                    if (matchingAuthorPos.size() > 1) {
                        log.debug("unable to automatically match person id to an author, notify by e-mail");
                        if (this.mailingEnabled && present(this.receiverMail) && this.mailUtils != null) {
                            sendUnableToMatchRelationMailViaReflection(resource.getTitle(), resource.getInterHash(), personId, this.receiverMail);
                        }
                    } else {
                        // Set found relation person index
                        relation.setPersonIndex(matchingAuthorPos.get(0));
                        // Check, if the relation already exists to another person
                        final ResourcePersonRelationQuery query = new ResourcePersonRelationQueryBuilder()
                                .byInterhash(relation.getPost().getResource().getInterHash())
                                .byRelationType(relation.getRelationType())
                                .byAuthorIndex(relation.getPersonIndex())
                                .build();
                        final List<ResourcePersonRelation> existingRelations = personDb.queryForResourcePersonRelations(new QueryAdapter<>(query, loggedInUser), session);
                        if (existingRelations.isEmpty()) {
                            log.debug("adding relation for interhash: " + relation.getPost().getResource().getInterHash() + ", type: " + relation.getRelationType() + ", index: " + relation.getPersonIndex());
                            personDb.addResourceRelation(relation, loggedInUser, session);
                        } else {
                            log.debug("relation already exists for interhash: " + relation.getPost().getResource().getInterHash() + ", type: " + relation.getRelationType() + ", index: " + relation.getPersonIndex());
                        }
                    }
                } else {
                    log.debug("unable to match person id to an author");
                }
            }
        }
    }

    @Override
    public <T extends Resource> void performAfterUpdate(Post<T> newPost, Post<T> oldPost, PostUpdateOperation operation, DBSession session) {
        // handle new updated post same way as create
        this.performAfterCreate(newPost, session);
    }

    /**
     * Sends mail notification using reflection to avoid compile-time dependency on MailUtils.
     * This allows bibsonomy-database to work without bibsonomy-web-common.
     *
     * Tries getMethod first for public methods, then falls back to getDeclaredMethod
     * with setAccessible(true) for non-public methods.
     *
     * Reflection errors are detected once and then suppressed to avoid log spam.
     */
    private void sendUnableToMatchRelationMailViaReflection(String title, String interhash, String personId, String receiverMail) {
        // Skip if reflection has previously failed to avoid repeated expensive attempts
        if (reflectionMailFailed) {
            suppressedReflectionErrorCount++;
            if (log.isDebugEnabled()) {
                log.debug("Skipping mail notification due to previous reflection failure " +
                        "(suppressed count: " + suppressedReflectionErrorCount +
                        " for interhash: " + interhash + ")");
            }
            return;
        }

        try {
            Method method;
            try {
                // Try public method first
                method = this.mailUtils.getClass().getMethod("sendUnableToMatchRelationMail",
                        String.class, String.class, String.class, String.class);
            } catch (NoSuchMethodException e) {
                // Fall back to declared method (handles non-public methods)
                method = this.mailUtils.getClass().getDeclaredMethod("sendUnableToMatchRelationMail",
                        String.class, String.class, String.class, String.class);
                method.setAccessible(true);
            }
            method.invoke(this.mailUtils, title, interhash, personId, receiverMail);
        } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            // Mark reflection as failed to prevent future attempts
            reflectionMailFailed = true;

            // Log detailed warning only once
            if (!reflectionErrorLogged) {
                reflectionErrorLogged = true;
                log.warn("Failed to send relation matching notification mail via reflection. " +
                        "Mail notifications will be disabled for this instance. " +
                        "Ensure bibsonomy-web-common module is available if mail notifications are required. " +
                        "Error: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
            }

            suppressedReflectionErrorCount++;
        }
    }

    private boolean hasPermissions() {
        return true;
    }

    @Override
    public ExecutableSystemTag newInstance() {
        return new AddRelationTag();
    }

    @Override
    public String getName() {
        return AddRelationTag.NAME;
    }

    @Override
    public boolean isToHide() {
        return AddRelationTag.toHide;
    }

    @Override
    public boolean isInstance(String tagName) {
        return SystemTagsUtil.hasTypeAndArgument(tagName) && AddRelationTag.NAME.equals(SystemTagsUtil.extractType(tagName));
    }

    @Override
    public ExecutableSystemTag clone() {
        try {
            return (ExecutableSystemTag) super.clone();
        } catch (final CloneNotSupportedException ex) {
            // never ever reached
            return null;
        }
    }

}
