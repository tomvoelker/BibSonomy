/**
 * BibSonomy-Web-Common - Common things for web
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
package org.bibsonomy.services.information;

import static org.bibsonomy.util.ValidationUtils.present;

import org.antlr.stringtemplate.StringTemplate;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;


/**
 * A mail information service to inform about changes in goldstandards.
 *
 * @author kchoong
 */
public class GoldStandardInformationService extends MailInformationService{

    /**
     * Current property to determine, if the users should be notified about goldstandard changes
     */
    private boolean enabled;

    @Override
    protected void setAttributes(StringTemplate stringTemplate, User userToInform, Post<? extends Resource> post) {
        stringTemplate.setAttribute("user", userToInform.getName());
        stringTemplate.setAttribute("title", post.getResource().getTitle());
        stringTemplate.setAttribute("history", this.absoluteURLGenerator.getHistoryUrlForPost(post));
    }

    @Override
    protected boolean userWantsToBeInformed(User userToInform) {
        return present(this.getMailAddress(userToInform)) && this.enabled;
    }


    /**
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
