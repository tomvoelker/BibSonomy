package org.bibsonomy.rest.validation;

import org.bibsonomy.bibtex.util.BibtexParserUtils;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.BibTexUtils;

/**
 * Validates the given model.
 * 
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class ServersideModelValidator implements ModelValidator {

	private static ServersideModelValidator modelValidator;

	private ServersideModelValidator() {}

	/** Get an instance of this model validator
	 * 
	 * @return An instance of the model validator.
	 */
	public static ServersideModelValidator getInstance() {
		if (ServersideModelValidator.modelValidator == null) {
			ServersideModelValidator.modelValidator = new ServersideModelValidator();
		}
		return ServersideModelValidator.modelValidator;
	}

	
	public void checkPublication(final BibTex publication) {
		// parse Bibtex so see whether the entry is valid
		final BibtexParserUtils bibutil = new BibtexParserUtils( BibTexUtils.toBibtexString(publication) );	
		
		/*
		 * FIXME: validator is modifying the publication
		 */
		publication.setAuthor( bibutil.getFormattedAuthorString() );
		publication.setEditor( bibutil.getFormattedEditorString() );
	}
}