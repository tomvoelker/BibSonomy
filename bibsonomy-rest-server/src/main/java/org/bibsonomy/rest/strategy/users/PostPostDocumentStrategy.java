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
package org.bibsonomy.rest.strategy.users;

import java.io.Writer;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Document;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.util.file.ServerUploadedFile;
import org.springframework.web.multipart.MultipartFile;


/**
 * Handle the request to post a document
 * 
 * @author Christian Kramer
 */
public class PostPostDocumentStrategy extends AbstractCreateStrategy {
	private final String userName;
	private final String resourceHash;
	private final MultipartFile file;
	private final FileLogic fileLogic;
	
	/**
	 * @param context
	 * @param userName
	 * @param resourceHash
	 */
	public PostPostDocumentStrategy(final Context context, final String userName, final String resourceHash) {
		super(context);
		this.userName = userName;
		this.resourceHash = resourceHash;
		this.file = context.getUploadAccessor().getUploadedFileByName("file");
		
		this.fileLogic = context.getFileLogic();
	}
	
	@Override
	public void canAccess() {
		if (!this.userName.equalsIgnoreCase(this.getLogic().getAuthenticatedUser().getName())) {
			throw new AccessDeniedException();
		}
	}

	@Override
	protected String create() {
		try {
			final Document document = this.fileLogic.saveDocumentFile(this.userName, new ServerUploadedFile(this.file));
			return this.getLogic().createDocument(document, this.resourceHash);
		} catch (final Exception ex) {
			throw new BadRequestOrResponseException(ex.getMessage());
		}
	}

	@Override
	protected void render(final Writer writer, final String uri) {
		this.getRenderer().serializeResourceHash(writer, uri);
	}
}