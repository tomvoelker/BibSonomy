package org.bibsonomy.search.management;

import java.util.List;

import org.bibsonomy.model.Resource;
import org.bibsonomy.search.model.SearchIndexInfo;

/**
 *
 * @author dzo
 */
public class SearchResourceManagerImpl implements SearchResourceManagerInterface {

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchResourceManagerInterface#generateIndexForResource(java.lang.Class)
	 */
	@Override
	public void generateIndexForResource(Class<? extends Resource> resourceType) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchResourceManagerInterface#getInfomationOfIndexForResource(java.lang.Class)
	 */
	@Override
	public List<SearchIndexInfo> getInfomationOfIndexForResource(Class<? extends Resource> resourceType) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.management.SearchResourceManagerInterface#updateIndex(java.lang.Class)
	 */
	@Override
	public void updateIndex(Class<? extends Resource> resourceType) {
		// TODO Auto-generated method stub

	}

}
