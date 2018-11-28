package org.bibsonomy.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface Exporter<T> {
	void save (Collection<T> entities, OutputStream outputStream,
						Map<String, Function<T, String>> mappings) throws IOException;
}
