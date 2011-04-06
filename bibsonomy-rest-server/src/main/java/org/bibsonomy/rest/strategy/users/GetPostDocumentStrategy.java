package org.bibsonomy.rest.strategy.users;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.bibsonomy.common.exceptions.AccessDeniedException;
import org.bibsonomy.model.Document;
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
	private final String userName;
	private final String resourceHash;
	private final String fileName;
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
		this.resourceHash = resourceHash;
		this.fileName = fileName;
		this.additionalInfos = context.getAdditionalInfos();
	}
	
	@Override
	protected RenderingFormat getRenderingFormat() {
		return RenderingFormat.PDF;
	}
	
	@Override
	public void canAccess() {
		if (!this.userName.equals(this.getLogic().getAuthenticatedUser().getName())) throw new AccessDeniedException();
	}
	
	@Override
	public void perform(final ByteArrayOutputStream outStream){
		// request the document from the db
		final Document doc = this.getLogic().getDocument(userName, resourceHash, fileName);
		
		if (doc == null) {
			throw new NoSuchResourceException("can't find document!");
		}
		
		try {
			// get the bufferedstream of the file
			final FileDownloadInterface download = new HandleFileDownload(additionalInfos.get("docPath"), doc.getFileHash());
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