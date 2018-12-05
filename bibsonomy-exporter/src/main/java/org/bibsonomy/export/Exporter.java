package org.bibsonomy.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Exporter<T> {
	List<String> EXPORT_FORMATS = Arrays.asList("excel");

	void save(Collection<T> entities, OutputStream outputStream,
						Map<String, Function<T, String>> mappings) throws IOException;

	String getContentType();

	String getFileExtension();
}
