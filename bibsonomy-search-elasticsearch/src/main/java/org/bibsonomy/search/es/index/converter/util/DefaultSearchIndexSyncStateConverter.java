package org.bibsonomy.search.es.index.converter.util;

import static org.bibsonomy.util.ValidationUtils.present;
import org.bibsonomy.search.update.DefaultSearchIndexSyncState;
import org.bibsonomy.search.util.Converter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * converts the {@link DefaultSearchIndexSyncState} to elasticsearch documents and vise versa
 *
 * @author dzo
 */
public class DefaultSearchIndexSyncStateConverter implements Converter<DefaultSearchIndexSyncState, Map<String, Object>, Object> {

	private static final String LAST_PERSON_CHANGE_ID_KEY = "last_person_change_id";
	private static final String LAST_PERSON_LOG_DATE = "last_person_log_date";
	private static final String LAST_LOG_DATE_KEY = "last_log_date";
	private static final String LAST_TAS_KEY = "last_tas_id";
	private static final String LAST_DOCUMENT_DATE_KEY = "last_document_date";
	private static final String LAST_PREDICTION_CHANGE_DATE = "lastPredictionChangeDate";
	private static final String LAST_POST_CONTENT_ID_KEY = "last_post_content_id";
	protected static final String MAPPING_VERSION = "mapping_version";

	/**
	 * @param date the date for the index
	 * @return
	 */
	private static Date getDateForIndex(Date date) {
		if (!present(date)) {
			return new Date();
		}
		return date;
	}

	@Override
	public Map<String, Object> convert(DefaultSearchIndexSyncState state) {
		final Map<String, Object> values = new HashMap<>();
		values.put(LAST_TAS_KEY, state.getLast_tas_id());
		final Date lastLogDate = getDateForIndex(state.getLast_log_date());
		values.put(LAST_LOG_DATE_KEY, Long.valueOf(lastLogDate.getTime()));
		values.put(LAST_PERSON_CHANGE_ID_KEY, Long.valueOf(state.getLastPersonChangeId()));
		final Date lastDocumentDate = getDateForIndex(state.getLastDocumentDate());
		values.put(LAST_DOCUMENT_DATE_KEY, Long.valueOf(lastDocumentDate.getTime()));
		values.put(MAPPING_VERSION, state.getMappingVersion());
		final Date lastPredictionDate = state.getLastPredictionChangeDate();
		if (present(lastPredictionDate)) {
			values.put(LAST_PREDICTION_CHANGE_DATE, Long.valueOf(lastPredictionDate.getTime()));
		}
		final long lastPostContentId = state.getLastPostContentId();
		values.put(LAST_POST_CONTENT_ID_KEY, lastPostContentId);

		final Date lastPersonLogDate = state.getLastPersonLogDate();
		if (present(lastPersonLogDate)) {
			values.put(LAST_PERSON_LOG_DATE, Long.valueOf(lastPersonLogDate.getTime()));
		}
		return values;
	}

	@Override
	public DefaultSearchIndexSyncState convert(Map<String, Object> source, Object options) {
		final DefaultSearchIndexSyncState searchIndexState = new DefaultSearchIndexSyncState();
		searchIndexState.setLast_tas_id((Integer) source.get(LAST_TAS_KEY));
		final Long dateAsTime = (Long) source.get(LAST_LOG_DATE_KEY);
		final Date lastLogDate = new Date(dateAsTime.longValue());
		searchIndexState.setLast_log_date(lastLogDate);

		final Long documentDateAsTime = (Long) source.get(LAST_DOCUMENT_DATE_KEY);
		final Date lastDocumentDate;
		if (present(documentDateAsTime)) {
			lastDocumentDate = new Date(documentDateAsTime.longValue());
		} else {
			lastDocumentDate = null;
		}
		searchIndexState.setLastDocumentDate(lastDocumentDate);

		final Long predictionChangeDateAsTime = (Long) source.get(LAST_PREDICTION_CHANGE_DATE);
		final Date predictionChangeDate;
		if (present(predictionChangeDateAsTime)) {
			predictionChangeDate = new Date(predictionChangeDateAsTime.longValue());
		} else {
			// the change date was the last log date
			predictionChangeDate = lastLogDate;
		}
		searchIndexState.setLastPredictionChangeDate(predictionChangeDate);
		// mapping version
		String mappingVersion = (String) source.get(MAPPING_VERSION);
		if (mappingVersion == null) {
			mappingVersion = "unknown";
		}
		searchIndexState.setMappingVersion(mappingVersion);

		searchIndexState.setLastPersonChangeId(((Integer) source.get(LAST_PERSON_CHANGE_ID_KEY)).longValue());

		final Long lastPersonLogDateTime = (Long) source.get(LAST_PERSON_LOG_DATE);
		if (present(lastPersonLogDateTime)) {
			searchIndexState.setLastPersonLogDate(new Date(lastPersonLogDateTime.longValue()));
		}

		if (source.containsKey(LAST_POST_CONTENT_ID_KEY)) {
			searchIndexState.setLastPostContentId((Integer) source.get(LAST_POST_CONTENT_ID_KEY));
		}
		return searchIndexState;
	}
}
