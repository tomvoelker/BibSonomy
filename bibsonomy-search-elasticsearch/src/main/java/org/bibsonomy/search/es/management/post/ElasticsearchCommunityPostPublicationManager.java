package org.bibsonomy.search.es.management.post;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.es.ESClient;
import org.bibsonomy.search.es.index.generator.ElasticsearchIndexGenerator;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.index.database.DatabaseInformationLogic;
import org.bibsonomy.search.index.update.post.CommunityPostIndexCommunityUpdateLogic;
import org.bibsonomy.search.index.update.post.CommunityPostIndexUpdateLogic;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchCommunityIndexSyncState;
import org.bibsonomy.search.util.Converter;

import java.net.URI;

/**
 * special implementation for {@link ElasticsearchCommunityPostManager} to update publication specific fields
 * these fields are:
 *
 * - person resource relations
 *
 * @author dzo
 */
public class ElasticsearchCommunityPostPublicationManager<G extends Resource> extends ElasticsearchCommunityPostManager<G> {

	/**
	 * default constructor
	 *
	 * @param systemId
	 * @param disabledIndexing
	 * @param updateEnabled
	 * @param client
	 * @param generator
	 * @param syncStateConverter
	 * @param entityInformationProvider
	 * @param inputLogic
	 * @param communityPostUpdateLogic
	 * @param postUpdateLogic
	 * @param databaseInformationLogic
	 */
	public ElasticsearchCommunityPostPublicationManager(URI systemId, boolean disabledIndexing, boolean updateEnabled, ESClient client, ElasticsearchIndexGenerator<Post<G>, SearchCommunityIndexSyncState> generator, Converter syncStateConverter, EntityInformationProvider entityInformationProvider, SearchDBInterface<G> inputLogic, CommunityPostIndexCommunityUpdateLogic<G> communityPostUpdateLogic, CommunityPostIndexUpdateLogic<G> postUpdateLogic, DatabaseInformationLogic<SearchCommunityIndexSyncState> databaseInformationLogic) {
		super(systemId, disabledIndexing, updateEnabled, client, generator, syncStateConverter, entityInformationProvider, inputLogic, communityPostUpdateLogic, postUpdateLogic, databaseInformationLogic);
	}

	@Override
	protected void updateResourceSpecificFields(String indexName, SearchCommunityIndexSyncState oldState, SearchCommunityIndexSyncState targetState) {
		/*
		 * add new resource relations
		 */

		/*
		 * remove resource relations
		 */
	}
}
