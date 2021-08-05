package org.bibsonomy.webapp.controller.statistics.meta;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldValuesQuery;
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
		final Set<?> values = this.logic.getMetaData(new DistinctFieldValuesQuery<>(command.getClazz(), createFieldDescriptor(command)));

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
