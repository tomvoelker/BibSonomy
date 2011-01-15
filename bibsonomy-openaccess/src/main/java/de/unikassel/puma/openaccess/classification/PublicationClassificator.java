package de.unikassel.puma.openaccess.classification;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.unikassel.puma.openaccess.classification.chain.ClassificationChainElement;
import de.unikassel.puma.openaccess.classification.chain.parser.ACMClassification;
import de.unikassel.puma.openaccess.classification.chain.parser.JELClassification;
import de.unikassel.puma.openaccess.classification.PublicationClassification;

public class PublicationClassificator {
	
	private final String xmlPath;
	
	private HashMap<String, Classification> classifications = new HashMap<String, Classification>();
	
	public PublicationClassificator(String xmlPath) {
		this.xmlPath = xmlPath;
		initialise();
	}
	
	public final List<PublicationClassification> getChildren(String classification, String name) {
		Classification c = classifications.get(classification);
		
		if(present(c)) {
			return c.getChildren(name);
		} else {
			return new ArrayList<PublicationClassification>();
		}
	}
	
	public Set<String> getAvailableClassifications() {
		return classifications.keySet();
	}
	
	public String getDescription(String classification, String name) {
		Classification c = classifications.get(classification);
		
		if(present(c)) {
			return c.getDescription(name);
		} else {
			return "";
		}	
	}
	
	private void initialise() {
		ArrayList<ClassificationChainElement> cceList = new ArrayList<ClassificationChainElement>();
		cceList.add(new ClassificationChainElement(new JELClassification()));
		cceList.add(new ClassificationChainElement(new ACMClassification()));
		
		
		File path = new File(xmlPath);
		
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
					Classification c = null;
					
					for(int i = 0; i < cceList.size() && !present(c); ++i) {
						c = cceList.get(i).getClassification(f.toURL());
					}
					
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
	
	public int getNumberOfClassifications() {
		return classifications.size();
	}

}
