package org.bibsonomy.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author pda
 * @param <T>
 */
public interface Exporter<T> {
	List<String> EXPORT_FORMATS = Arrays.asList("excel");

	/**
	 * saves the specified entities to the outputstream
	 * @param entities
	 * @param outputStream
	 * @param mappings
	 * @throws IOException
	 */
	void save(Collection<T> entities, OutputStream outputStream,
						Map<String, Function<T, String>> mappings) throws IOException;

	/**
	 * @return the content type of the written format
	 */
	String getContentType();

	/**
	 * @return the file extension of the written file
	 */
	String getFileExtension();
}
