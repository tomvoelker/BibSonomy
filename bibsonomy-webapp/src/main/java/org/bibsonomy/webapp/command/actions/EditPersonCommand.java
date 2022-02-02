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
package org.bibsonomy.webapp.command.actions;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.PersonUpdateOperation;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.webapp.command.PersonPageCommand;

/**
 * Command for editing a person.
 *
 * @author kchoong
 */
@Getter
@Setter
public class EditPersonCommand extends PersonPageCommand {

    private String personId;
    private boolean claimedPerson;
    private PersonUpdateOperation updateOperation;

    /** Properties for editing names of person */
    private PersonName personName;

    /** Properties if deleting or adding relations */
    private String type;
    private String interhash;
    private String index;
    private String selectedName;

    @Deprecated // TODO: bind person directly
    private String formResourceHash;
    @Deprecated // TODO: bind person directly
    private PersonResourceRelationType formPersonRole;
    @Deprecated // TODO: bind person dier rectly
    private String formThesisId;
    @Deprecated // TODO: bind person directly
    private List<String> formPersonRoles;
    @Deprecated // TODO: bind person directly
    private String formRequestType;
    @Deprecated // TODO: bind person directly
    private String formResourcePersonRelationId;
    @Deprecated // TODO: bind person directly
    private String formInterHash;
    @Deprecated // TODO: bind person directly
    private String formIntraHash;
    @Deprecated // TODO: bind person directly
    private int formPersonIndex = -1;
    private int formMatchId;

}
