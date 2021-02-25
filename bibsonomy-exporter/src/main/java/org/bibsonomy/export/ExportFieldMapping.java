package org.bibsonomy.export;

import java.util.function.Function;

/**
 * @author dzo
 * @param <T>
 */
public class ExportFieldMapping<T> {
	private String header;
	private Function<T, String> converter;

	/**
	 * default constructor
	 * @param header
	 * @param converter
	 */
	public ExportFieldMapping(String header, Function<T, String> converter) {
		this.header = header;
		this.converter = converter;
	}

	/**
	 * @return the header
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * @return the converter
	 */
	public Function<T, String> getConverter() {
		return converter;
	}
}
