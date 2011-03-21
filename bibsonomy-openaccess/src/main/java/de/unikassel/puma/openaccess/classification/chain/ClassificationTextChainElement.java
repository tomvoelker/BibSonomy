package de.unikassel.puma.openaccess.classification.chain;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import de.unikassel.puma.openaccess.classification.Classification;
import de.unikassel.puma.openaccess.classification.ClassificationSource;
import de.unikassel.puma.openaccess.classification.ClassificationTextParser;

public class ClassificationTextChainElement implements ClassificationSource {

	private final ClassificationTextParser classificationParser;
	
	private ClassificationSource next = null;

	public ClassificationTextChainElement(ClassificationTextParser cParser) {
		this.classificationParser = cParser;
	}

	public void setNext(ClassificationSource next) {
		this.next = next;
	}
	
	public ClassificationSource getNext() {
		return this.next;
	}
	
	@Override
	public Classification getClassification(URL url) throws IOException {
		BufferedReader in =
			new BufferedReader(new FileReader(url.getPath()));
		
		classificationParser.parse(in);
		
		if(!present(classificationParser.getList())) {
			if(!present(next)) {
				return null;
			} else {
				return next.getClassification(url);
			}
		}
		
		return new Classification(classificationParser.getName(), classificationParser.getList(), classificationParser.getDelimiter());
	}
}
