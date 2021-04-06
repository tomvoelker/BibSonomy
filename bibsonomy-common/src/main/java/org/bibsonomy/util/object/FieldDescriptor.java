package org.bibsonomy.util.object;

import java.util.function.Function;

/**
 * describes a field
 * @param <T> type of the field
 * @param <R> the type of the field
 */
public class FieldDescriptor<T, R> {

	private final String fieldName;
	private final Function<T, R> fieldSetter;

	/**
	 * default constructor
	 * @param fieldName
	 * @param fieldSetter
	 */
	public FieldDescriptor(String fieldName, Function<T, R> fieldSetter) {
		this.fieldName = fieldName;
		this.fieldSetter = fieldSetter;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @return the fieldSetter
	 */
	public Function<T, R> getFieldSetter() {
		return fieldSetter;
	}
}
