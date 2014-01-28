package de.unikassel.puma.openaccess.classification.chain;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import de.unikassel.puma.openaccess.classification.Classification;
import de.unikassel.puma.openaccess.classification.ClassificationSource;
import de.unikassel.puma.openaccess.classification.ClassificationTextParser;

/**
 * @author philipp
 */
public class ClassificationTextChainElement implements ClassificationSource {

	private final ClassificationTextParser classificationParser;
	
	private ClassificationSource next = null;

	/**
	 * constructor to set the parser
	 * @param cParser
	 */
	public ClassificationTextChainElement(final ClassificationTextParser cParser) {
		this.classificationParser = cParser;
	}
	
	/**
	 * @param next the next classification source to set
	 */
	public void setNext(final ClassificationSource next) {
		this.next = next;
	}
	
	@Override
	public Classification getClassification(final URL url) throws IOException {
		final BufferedReader in =
			new BufferedReader(new FileReader(url.getPath()));
		
		this.classificationParser.parse(in);
		
		if (!present(this.classificationParser.getList())) {
			if (!present(this.next)) {
				return null;
			}
			
			return this.next.getClassification(url);
		}
		
		return new Classification(this.classificationParser.getName(), this.classificationParser.getList(), this.classificationParser.getDelimiter());
	}
}
