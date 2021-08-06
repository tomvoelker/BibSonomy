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

import org.bibsonomy.common.errors.ErrorMessage;

import java.util.List;

/**
 * a helper class to wrap errors while generating a result
 * @param <R>
 *
 * @author dzo
 */
public class ErrorAwareResult<R> {

	private R result;

	private List<ErrorMessage> errors;

	/**
	 * the default constructor
	 *
	 * @param result
	 * @param errors
	 */
	public ErrorAwareResult(final R result, final List<ErrorMessage> errors) {
		this.result = result;
		this.errors = errors;
	}

	/**
	 * @return the result
	 */
	public R getResult() {
		return result;
	}

	/**
	 * @return the errors
	 */
	public List<ErrorMessage> getErrors() {
		return errors;
	}
}
