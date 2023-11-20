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
package org.bibsonomy.webapp.controller.statistics.meta;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery;
import org.bibsonomy.util.object.FieldDescriptor;
import org.bibsonomy.webapp.command.statistics.meta.DistinctFieldValuesCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

import net.sf.json.JSONArray;

/**
 * meta statistics for a field (distinct field)
 *
 * @author dzo
 */
public class DistinctFieldValuesController<T> implements MinimalisticController<DistinctFieldValuesCommand<T>> {

	private Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers;

	private LogicInterface logic;

	@Override
	public DistinctFieldValuesCommand<T> instantiateCommand() {
		return new DistinctFieldValuesCommand<>();
	}

	@Override
	public View workOn(final DistinctFieldValuesCommand<T> command) {
		final Set<?> values = this.logic.getMetaData(command.getContext().getLoginUser(),
				new DistinctFieldQuery<>(command.getClazz(), createFieldDescriptor(command)));

		final JSONArray jsonArray = new JSONArray();
		jsonArray.addAll(values);
		command.setResponseString(jsonArray.toString());

		return Views.AJAX_JSON;
	}

	private FieldDescriptor<T, ?> createFieldDescriptor(final DistinctFieldValuesCommand<T> command) {
		final Class<T> clazz = command.getClazz();
		return (FieldDescriptor<T, ?>) mappers.get(clazz).apply(command.getField());
	}

	/**
	 * @param mappers the mappers to set
	 */
	public void setMappers(Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers) {
		this.mappers = mappers;
	}

	/**
	 * @param logic the logic to set
	 */
	public void setLogic(LogicInterface logic) {
		this.logic = logic;
	}
}
