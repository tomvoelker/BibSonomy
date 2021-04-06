package org.bibsonomy.webapp.command.statistics.meta;

import org.bibsonomy.webapp.command.ajax.AjaxCommand;

/**
 * statistics command for distinct values of a field
 *
 * @author dzo
 */
public class DistinctFieldValuesCommand<T> extends AjaxCommand<Void> {
	private Class<T> clazz;

	// XXX: should be the method reference
	private String field;

	/**
	 * @return the clazz
	 */
	public Class<T> getClazz() {
		return clazz;
	}

	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @param field the field to set
	 */
	public void setField(String field) {
		this.field = field;
	}
}
