package de.unikassel.puma.openaccess.classification;

import static org.bibsonomy.util.ValidationUtils.present;

public class PublicationClassificatorSingleton {
	
	private String classificationFilePath;
	
	
	 /** singleton instance */
    private PublicationClassificator singleton;
    /**
     * Gets singleton
     * @return singleton
     */
    public PublicationClassificator getInstance(){
        
    	if(!present(singleton)) {
    		singleton = new PublicationClassificator(classificationFilePath);
    	}
    	
    	return singleton;
    }
    
    public void setClassificationFilePath(String classPath) {
    	classificationFilePath = classPath;
    }


}
