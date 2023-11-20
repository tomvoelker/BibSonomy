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
package org.bibsonomy.webapp.command.cris;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.command.ListCommand;

/**
 * organization page command
 *
 * @author dzo
 * @author pda
 */
@Getter
@Setter
public class OrganizationPageCommand extends BaseCommand {

	private OrganizationPageSubPage subPage = OrganizationPageSubPage.INFO;

	private String requestedOrganizationName;
	private Group group;

	private String sortPageOrder = "desc";
	private String sortPage = "date";

	private final ListCommand<Post<GoldStandardPublication>> publications = new ListCommand<>(this);
	private final ListCommand<Person> persons = new ListCommand<>(this);
	private final ListCommand<Project> projects = new ListCommand<>(this);

}
