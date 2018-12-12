package org.bibsonomy.search.es.index.converter.cris;

import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.util.Converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * converter for converting {@link CRISLink}s
 * @author dzo
 */
public class CRISLinkConverter implements Converter<CRISLink, Map<String, Object>, Object> {

	private static Map<String, Object> convertCRISEntity(final Linkable source, final List<CRISEntityConverter<Linkable, Map<String, Object>, Object>> converters) {
		final Optional<CRISEntityConverter<Linkable, Map<String, Object>, Object>> first = converters.stream().filter(converter -> converter.canConvert(source)).findFirst();
		if (!first.isPresent()) {
			throw new IllegalStateException("no converter for " + source.getClass().getSimpleName() + " found");
		}

		return first.get().convert(source);
	}

	private final List<CRISEntityConverter<Linkable, Map<String, Object>, Object>> sourceConverter;
	private final List<CRISEntityConverter<Linkable, Map<String, Object>, Object>> targetConverter;

	/**
	 * sets the cris entity converter
	 * @param sourceConverter
	 * @param targetConverter
	 */
	public CRISLinkConverter(List<CRISEntityConverter<Linkable, Map<String, Object>, Object>> sourceConverter, List<CRISEntityConverter<Linkable, Map<String, Object>, Object>> targetConverter) {
		this.sourceConverter = sourceConverter;
		this.targetConverter = targetConverter;
	}

	@Override
	public Map<String, Object> convert(final CRISLink source) {
		final Map<String, Object> convertedFields = new HashMap<>();

		convertedFields.putAll(convertCRISEntity(source.getSource(), this.sourceConverter));
		convertedFields.putAll(convertCRISEntity(source.getTarget(), this.targetConverter));

		convertedFields.put(CRISLinkFields.START_DATE, ElasticsearchUtils.dateToString(source.getStartDate()));
		convertedFields.put(CRISLinkFields.END_DATE, ElasticsearchUtils.dateToString(source.getStartDate()));
		return convertedFields;
	}

	@Override
	public CRISLink convert(Map<String, Object> source, Object options) {
		return null;
	}
}
