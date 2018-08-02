package org.bibsonomy.testutil;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.BibTexReader;

import java.util.Collection;
import java.util.Collections;

/**
 * dummy implementation for {@link BibTexReader}
 * @author dzo
 */
public class DummyPublicationReader implements BibTexReader {
	@Override
	public Collection<BibTex> read(ImportResource importRes) {
		return Collections.emptyList();
	}
}
