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
 * @version $Id$
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