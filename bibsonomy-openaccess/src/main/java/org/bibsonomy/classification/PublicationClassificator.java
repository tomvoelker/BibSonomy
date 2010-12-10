package org.bibsonomy.classification;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import org.bibsonomy.classification.chain.ClassificationChainElement;
import org.bibsonomy.classification.chain.parser.JELClassification;
import org.bibsonomy.model.PublicationClassification;

public class PublicationClassificator {
	
	private static final String XML_PATH = "/home/philipp/workspace/KDE/bibsonomy/bibsonomy-openaccess/src/main/resources/classifications";
	
	private HashMap<String, Classification> classifications = new HashMap<String, Classification>();
	
	private static final PublicationClassificator instance = new PublicationClassificator();
	
	public static PublicationClassificator getInstance() {
		return instance;
	}
	
	
	private PublicationClassificator() {
		initialise();
	}
	
	public final List<PublicationClassification> getChildren(String classification, String name) {
		return classifications.get(classification).getChildren(name);
	}
	
	public String getDescription(String classification, String name) {
		return classifications.get(classification).getDescription(name);
	}
	
	
	private void initialise() {
		ClassificationChainElement cce = new ClassificationChainElement(new JELClassification());
		File path = new File(XML_PATH);
		
		if(path.isDirectory()) {
			
			File[] files = path.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					if(pathname.toString().endsWith(".xml")) {
						return true;
					}
					return false;
				}
			});
			
			for(File f : files) {
				try {
					Classification c = cce.getClassification(f.toURL());
					
					if(!present(c)) {
						System.out.println("Unable to parse " +f.getName());
						continue;
					}
					
					System.out.println("Found Classification " +c.getClassName());
					classifications.put(c.getClassName(), c);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}

}
