package de.unikassel.puma.openaccess.classification;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;

import de.unikassel.puma.openaccess.classification.chain.ClassificationChainElement;
import de.unikassel.puma.openaccess.classification.chain.parser.JELClassification;

public class PublicationClassificator {
	private static final Log log = LogFactory.getLog(PublicationClassificator.class);

	private String classificationFilePath;
	
	private final HashMap<String, Classification> classifications = new HashMap<String, Classification>();
	
	public PublicationClassificator() {
		/*
		 * read classifications from file
		 */
		initialise();
	}
	
	public final List<PublicationClassification> getChildren(String classification, String name) {
		return classifications.get(classification).getChildren(name);
	}
	
	public String getDescription(String classification, String name) {
		return classifications.get(classification).getDescription(name);
	}
	
	
	private void initialise() {
		if (!present(classificationFilePath)) {
			log.warn("No path for classification files configured.");
			return;
		}
		/*
		 * proceed with normal operation
		 */
		final File path = new File(classificationFilePath);

		final ClassificationChainElement cce = new ClassificationChainElement(new JELClassification());
		
		if (path.isDirectory()) {
			
			final File[] files = path.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.toString().endsWith(".xml");
				}
			});
			
			for (final File file : files) {
				try {
					/*
					 * FIXME toURL() is deprecated - please use the correct 
					 * method.
					 */
					final Classification c = cce.getClassification(file.toURL());
					
					if (!present(c)) {
						log.error("Unable to parse " +file.getName());
						continue;
					}
					
					log.debug("Found Classification " +c.getClassName());
					classifications.put(c.getClassName(), c);
				} catch (MalformedURLException e) {
					log.error("Could not load classifications", e);
				} catch (IOException e) {
					log.error("Could not load classifications", e);
				}
			}
			
		}
		
	}

	/**
	 * Sets the path where the classification XML files reside. 
	 * 
	 * @param classificationFilePath
	 */
	@Required
	public void setClassificationFilePath(String classificationFilePath) {
		this.classificationFilePath = classificationFilePath;
	}

}
