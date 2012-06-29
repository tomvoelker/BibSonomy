package org.bibsonomy.rest.strategy.users;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Document;
import org.bibsonomy.rest.RestServlet;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;
import org.bibsonomy.util.upload.FileDownloadInterface;
import org.bibsonomy.util.upload.impl.HandleFileDownload;

/**
 * Handle a document request
 * 
 * @version $Id$
 * @author Christian Kramer
 */
public class GetPostDocumentStrategy extends Strategy {
	private static final FileTypeMap MIME_TYPES_FILE_TYPE_MAP = new MimetypesFileTypeMap();
	
	private final String userName;
	private final Document document;
	private final Map<String, String> additionalInfos;

	/**
	 * @param context
	 * @param userName
	 * @param resourceHash
	 * @param fileName
	 */
	public GetPostDocumentStrategy(final Context context, final String userName, final String resourceHash, final String fileName) {
		super(context);
		this.userName = userName;
		// request the document from the db
		this.document = this.getLogic().getDocument(userName, resourceHash, fileName);
		
		if (this.document == null) {
			throw new NoSuchResourceException("can't find document!");
		}
		
		this.additionalInfos = context.getAdditionalInfos();
	}
	
	@Override
	protected RenderingFormat getRenderingFormat() {
		final String contentType = MIME_TYPES_FILE_TYPE_MAP.getContentType(this.document.getFileName());
		return RenderingFormat.getMediaType(contentType);
	}
	
	@Override
	public void canAccess() {
		if (!this.userName.equals(this.getLogic().getAuthenticatedUser().getName())) {
			throw new AccessDeniedException();
		}
	}
	
	@Override
	public void perform(final ByteArrayOutputStream outStream){
		try {
			// get the bufferedstream of the file
			final FileDownloadInterface download = new HandleFileDownload(this.additionalInfos.get(RestServlet.DOCUMENTS_PATH_KEY), this.document.getFileHash());
			final BufferedInputStream buf = download.getBuf();
			
			// write the bytes of the file to the writer
			int readBytes = 0;
			while ((readBytes = buf.read()) != -1){
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