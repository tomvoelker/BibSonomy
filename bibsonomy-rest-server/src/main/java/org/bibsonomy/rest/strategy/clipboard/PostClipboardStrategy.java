/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy.clipboard;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

/**
 * @author wla
 */
public class PostClipboardStrategy extends Strategy {

	protected final String resourceHash;
	protected final String userName;

	/**
	 * 
	 * @param context
	 * @param userName of post owner
	 * @param resourceHash of post
	 */
	public PostClipboardStrategy(Context context, String userName, String resourceHash) {
		super(context);
		if (present(resourceHash) && resourceHash.length() == 33) {
			this.resourceHash = resourceHash.substring(1);
		} else {
			this.resourceHash = resourceHash;
		}
		this.userName = userName;
	}

	@Override
	public void perform(ByteArrayOutputStream outStream) throws InternServerException, NoSuchResourceException, ResourceMovedException, ObjectNotFoundException {
		this.getLogic().createClipboardItems(createPost(resourceHash, userName));
		this.getRenderer().serializeOK(this.writer);
	}

	/**
	 * 
	 * Creates a new Collections.singletonList with (empty) post with the given username and resourcehash.
	 * 
	 * @param resourceHash
	 * @param userName
	 * @return
	 */
	protected List<Post<? extends Resource>> createPost(final String resourceHash, final String userName) {
		final Post<BibTex> post = new Post<BibTex>();
		final BibTex publication = new BibTex();

		publication.setIntraHash(resourceHash);
		post.setResource(publication);
		post.setUser(new User(userName));
		return Collections.<Post<? extends Resource>> singletonList(post);
	}

}
