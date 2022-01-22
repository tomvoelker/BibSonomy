/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.cris;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.util.object.FieldDescriptor;

/**
 * model representation of a project
 *
 * @author dzo
 */
@Getter
@Setter
public class Project implements Linkable {

	/** the sponsor field */
	public static final String SPONSOR_FIELD_NAME = "sponsor";

	/** the type field */
	public static final String TYPE_FIELD_NAME = "type";

	/**
	 * a lookup method for method references for this class
	 */
	public static final Function<String, FieldDescriptor<Project, ?>> METHOD_REFERENCE = (field) -> {
		switch (field) {
			case "sponsor": return new FieldDescriptor<>(SPONSOR_FIELD_NAME, Project::getSponsor);
			case "type": return new FieldDescriptor<>(TYPE_FIELD_NAME, Project::getType);
		}

		return null;
	};

	/** the database id */
	private Integer id;

	/** the external id of the project */
	private String externalId;

	/** the internal id of the project */
	private String internalId;

	/** the title of the project */
	private String title;

	/** the subtitle of the project */
	private String subTitle;

	/** the description of the project */
	private String description;

	/** the type of the project */
	private String type;

	private String sponsor;

	/** the funding */
	private Float budget;

	/** the start date */
	private Date startDate;

	/** the end date */
	private Date endDate;

	/** the parent project */
	private Project parentProject;

	/** sub projects of the project */
	private List<Project> subProjects = new LinkedList<>();

	/** cris links that are connected to this project */
	private List<CRISLink> crisLinks = new LinkedList<>();

	/**
	 * @return the id
	 */
	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getLinkableId() {
		return this.getExternalId();
	}
}
