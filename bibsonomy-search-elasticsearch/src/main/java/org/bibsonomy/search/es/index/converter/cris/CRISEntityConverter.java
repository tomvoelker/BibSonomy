package org.bibsonomy.search.es.index.converter.cris;

import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.search.util.Converter;

/**
 * interface to convert a {@link Linkable} to the model representation
 *
 * @author dzo
 */
public interface CRISEntityConverter<T extends Linkable, S, O> extends Converter<T, S, O> {

	/**
	 * @param linkable
	 * @return <code>true</code> iff the converter can convert the linkable
	 */
	boolean canConvert(final Linkable linkable);
}
