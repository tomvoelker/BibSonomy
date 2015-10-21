package org.bibsonomy.search.management;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.ResourcePersonRelationLogStub;
import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.update.SearchIndexState;
import org.bibsonomy.search.update.SearchIndexUpdater;
import org.bibsonomy.search.update.SearchPublicationIndexUpdater;
import org.bibsonomy.util.ValidationUtils;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 * @param <P> 
 * @param <T> 
 */
public class SearchPublicationManager<P extends BibTex> extends SearchResourceManagerImpl<P> {
	/**
	 * @param searchDBLogic
	 * @param containers
	 */
	public SearchPublicationManager(SearchDBInterface<P> searchDBLogic, List<SearchIndexContainer<P, ?, ?, ?>> containers) {
		super(searchDBLogic, containers);
	}

	private static final Log log = LogFactory.getLog(SearchPublicationManager.class);
	private static final int UPDATED_INTERHASHES_CACHE_SIZE = 25000;
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchResourceManagerImpl#updateResourceSpecificProperties(org.bibsonomy.search.update.IndexUpdaterState, org.bibsonomy.search.update.IndexUpdaterState, java.util.List)
	 */
	@SuppressWarnings("unchecked") // XXX: any idea to solve this?
	@Override
	protected void updateResourceSpecificProperties(SearchIndexState oldState, SearchIndexState targetState, List<SearchIndexUpdater<P>> indexUpdaters) {
		List<SearchPublicationIndexUpdater<P>> publicationIndexUpdater = new LinkedList<>();
		for (SearchIndexUpdater<P> indexUpdater : indexUpdaters) {
			if (indexUpdater instanceof SearchPublicationIndexUpdater<?>) {
				@SuppressWarnings("rawtypes")
				final SearchPublicationIndexUpdater searchPublicationIndexUpdater = (SearchPublicationIndexUpdater<?>) indexUpdaters;
				publicationIndexUpdater.add(searchPublicationIndexUpdater);
			}
			
		}
		// now the index is up to date with the posts
		updateUpdatedIndexWithPersonChanges(oldState, targetState, publicationIndexUpdater);
	}
	
	private void updateUpdatedIndexWithPersonChanges(SearchIndexState oldState, SearchIndexState targetState, List<SearchPublicationIndexUpdater<P>> indexUpdaters) {
		final LRUMap updatedInterhashes = new LRUMap(UPDATED_INTERHASHES_CACHE_SIZE);
		applyChangesInPubPersonRelationsToIndex(oldState, targetState, indexUpdaters, updatedInterhashes);
		applyPersonChangesToIndex(oldState, targetState, indexUpdaters, updatedInterhashes);
	}

	/**
	 * @param targetState
	 * @param indexUpdaters
	 * @param updatedInterhashes
	 */
	private void applyPersonChangesToIndex(SearchIndexState oldState, SearchIndexState targetState, List<SearchPublicationIndexUpdater<P>> indexUpdaters, LRUMap updatedInterhashes) {
		for (long minPersonChangeId = oldState.getLastPersonChangeId() + 1; minPersonChangeId < targetState.getLastPersonChangeId(); minPersonChangeId = Math.min(targetState.getLastPersonChangeId(), minPersonChangeId + SQL_BLOCKSIZE)) {
			final List<PersonName> personMainNameChanges = this.searchDBLogic.getPersonMainNamesByChangeIdRange(minPersonChangeId, minPersonChangeId + SQL_BLOCKSIZE);
			for (PersonName name : personMainNameChanges) {
				for (SearchPublicationIndexUpdater<P> updater : indexUpdaters) {
					// TODO: remove logic parameter
					updater.updateIndexWithPersonNameInfo(name, updatedInterhashes, this.searchDBLogic);
				}
			}
			personMainNameChanges.clear();
			final List<Person> personChanges = this.searchDBLogic.getPersonByChangeIdRange(minPersonChangeId, minPersonChangeId + SQL_BLOCKSIZE);
			for (Person per : personChanges) {
				for (SearchPublicationIndexUpdater<P> updater : indexUpdaters) {
					// TODO: remove logic parameter
					updater.updateIndexWithPersonInfo(per, updatedInterhashes, this.searchDBLogic);
				}
			}
			personChanges.clear();
		}
	}

	private void applyChangesInPubPersonRelationsToIndex(SearchIndexState oldState, SearchIndexState targetState, List<SearchPublicationIndexUpdater<P>> indexUpdaters, final LRUMap updatedInterhashes) {
		for (long minPersonChangeId = oldState.getLastPersonChangeId() + 1; minPersonChangeId < targetState.getLastPersonChangeId(); minPersonChangeId += SQL_BLOCKSIZE) {
			final List<ResourcePersonRelationLogStub> relChanges = this.searchDBLogic.getPubPersonRelationsByChangeIdRange(minPersonChangeId, minPersonChangeId + SQL_BLOCKSIZE);
			if (log.isDebugEnabled() || ValidationUtils.present(relChanges)) {
				log.info("found " + relChanges.size() + " relation changes to update " + indexUpdaters.toString());
			}
			for (ResourcePersonRelationLogStub rel : relChanges) {
				final String interhash = rel.getPostInterhash();
				if (updatedInterhashes.put(interhash, interhash) == null) {
					List<ResourcePersonRelation> newRels = this.searchDBLogic.getResourcePersonRelationsByPublication(interhash);
					for (SearchPublicationIndexUpdater<P> updater : indexUpdaters) {
						updater.updateIndexWithPersonRelation(interhash, newRels);
					}
				}
			}
		}
	}
}
