/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.bibsonomy.common.enums.PreviewSize;
import org.bibsonomy.model.Document;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;
import org.bibsonomy.services.filesystem.FileLogic;
import org.bibsonomy.util.ValidationUtils;

/**
 * Handle a document request
 * 
 * @author Christian Kramer
 */
public class GetPostDocumentStrategy extends Strategy {
	private static final FileTypeMap MIME_TYPES_FILE_TYPE_MAP = new MimetypesFileTypeMap();
	
	private final Document document;
	private final FileLogic fileLogic;
	private final PreviewSize preview;

	/**
	 * @param context
	 * @param userName
	 * @param resourceHash
	 * @param fileName
	 */
	public GetPostDocumentStrategy(final Context context, final String userName, final String resourceHash, final String fileName) {
		super(context);
		// request the document from the db
		this.document = this.getLogic().getDocument(userName, resourceHash, fileName);
		
		if (this.document == null) {
			throw new NoSuchResourceException("can't find document!");
		}
		this.fileLogic = context.getFileLogic();

		final String previewValue = context.getStringAttribute("preview", null);
		if (ValidationUtils.present(previewValue)) {
			PreviewSize previewEnumValue;
			try {
				previewEnumValue = Enum.valueOf(PreviewSize.class, previewValue.toUpperCase());
			} catch (IllegalArgumentException e) {
				// If parameter was given, but without a proper value, render a
				// LARGE preview image.
				previewEnumValue = PreviewSize.LARGE;
			}
			this.preview = previewEnumValue;
		} else {
			this.preview = null;
		}
	}
	
	@Override
	protected RenderingFormat getRenderingFormat() {
		final String contentType;
		// PDF is requested
		if (this.preview == null) {
			contentType = MIME_TYPES_FILE_TYPE_MAP.getContentType(this.document.getFileName());
		} else {
			contentType = MIME_TYPES_FILE_TYPE_MAP.getContentType(this.fileLogic.getPreviewFile(document, preview));
		}
		return RenderingFormat.getMediaType(contentType);
	}
	
	@Override
	public void canAccess() {
		// empty here, because logic.getDocument in constructor already throws AcessDeniedException if not allowed
	}
	
	@Override
	public void perform(final ByteArrayOutputStream outStream){
		try {
			final File file;
			if (this.preview != null) {
				file = this.fileLogic.getPreviewFile(document, this.preview);
			} else {
				file = this.fileLogic.getFileForDocument(this.document);
			}
			// get the bufferedstream of the file
			final BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
			
			// write the bytes of the file to the writer
			int readBytes = 0;
			while ((readBytes = buf.read()) != -1) {
				outStream.write(readBytes);
			}
			
			buf.close();
		} catch (final FileNotFoundException ex) {
			throw new BadRequestOrResponseException("The requested file doesn't exists");
		} catch (final IOException ex) {
			throw new BadRequestOrResponseException("Can't write the file");
		}
	}
}