package org.bibsonomy.testutil;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.services.information.InformationService;

/**
 * @author dzo
  */
public class DummyInformationService implements InformationService {

	@Override
	public void createdPost(String username, Post<? extends Resource> post) {
		// ignore
	}

}
