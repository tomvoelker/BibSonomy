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

import org.bibsonomy.common.enums.PostUpdateOperation;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
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
import org.bibsonomy.model.util.PersonNameUtils;

import java.util.List;

public class AddRelationTag extends AbstractSystemTagImpl implements ExecutableSystemTag{

    private static final boolean toHide = true;
    public static final String NAME = "rel";

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
                ResourcePersonRelation relation = new ResourcePersonRelation();
                relation.setPost((Post<? extends BibTex>) post);
                relation.setPerson(person);
                relation.setRelationType(PersonResourceRelationType.AUTHOR);

                final BibTex resource = (BibTex) post.getResource();
                List<PersonName> authorList = resource.getAuthor();
                for (int i = 0; i < authorList.size(); i++) {
                    if (PersonNameUtils.containsPerson(authorList.get(i), person.getNames(), true)) {
                        relation.setPersonIndex(i);
                        break;
                    }
                }

                personDb.addResourceRelation(relation, loggedInUser, session);
            }
        }
    }

    @Override
    public <T extends Resource> void performAfterUpdate(Post<T> newPost, Post<T> oldPost, PostUpdateOperation operation, DBSession session) {
        // handle new updated post same way as create
        this.performAfterCreate(newPost, session);
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
