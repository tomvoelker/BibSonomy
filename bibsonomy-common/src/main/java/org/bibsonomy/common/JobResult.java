/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
package org.bibsonomy.common;

import org.bibsonomy.common.enums.Status;
import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.common.information.JobInformation;

import java.util.List;

/**
 * class to report a job result
 *
 * @author dzo
 */
public class JobResult {

	/**
	 * builds a jobResult with the provided errors
	 * @param errors
	 * @return
	 */
	public static JobResult buildFailure(final List<ErrorMessage> errors) {
		final JobResult jobResult = new JobResult();
		jobResult.setStatus(Status.FAIL);
		jobResult.setErrors(errors);
		return jobResult;
	}

	/**
	 * @return the JobResult with status ok
	 */
	public static JobResult buildSuccess() {
		final JobResult jobResult = new JobResult();
		jobResult.setStatus(Status.OK);
		return jobResult;
	}

	/**
	 * @param id the of the job
	 * @return the JobResult with status ok
	 */
	public static JobResult buildSuccess(final String id) {
		final JobResult jobResult = buildSuccess();
		jobResult.setId(id);
		return jobResult;
	}

	/** the id of the entity that was created, updated, deleted, … */
	private String id;

	private Status status;

	private List<ErrorMessage> errors;

	/** job information */
	private List<JobInformation> info;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the errors
	 */
	public List<ErrorMessage> getErrors() {
		return errors;
	}

	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<ErrorMessage> errors) {
		this.errors = errors;
	}

	/**
	 * @return the info
	 */
	public List<JobInformation> getInfo() {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(List<JobInformation> info) {
		this.info = info;
	}
}
