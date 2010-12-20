package de.unikassel.puma.openaccess.classification;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class Classification {
	
	private final String className;
	
	private final  LinkedHashMap<String , ClassificationObject> classifications;
	
	public Classification(String className, LinkedHashMap<String , ClassificationObject> classifications) {
		this.className = className;
		this.classifications = classifications;
	}
	
	public String getClassName() {
		return className;
	}
	
	public final List<PublicationClassification> getChildren(String name) {
		ArrayList<PublicationClassification> erg = new ArrayList<PublicationClassification>();
		
		String actual, tempName = name;
		
		LinkedHashMap<String , ClassificationObject> children = classifications;
		ClassificationObject actualObject = null;
		
		while(!tempName.isEmpty()) {			
			actual = tempName.charAt(0) +"";
			tempName = tempName.substring(1);
			actualObject = children.get(actual);
			
			children = actualObject.getChildren();

		}
		//TODO sort
		Set<String> keys = children.keySet();
		for(String s : keys) {
			PublicationClassification co = new PublicationClassification(s, getDescription(name +s));
			erg.add(co);
		}
		return erg;
	}
	
	public String getDescription(String name) {
		String actual = name.charAt(0) +"";
		name = name.substring(1);
		
		LinkedHashMap<String , ClassificationObject> children = classifications;
		ClassificationObject actualObject = null;
		
		while(!children.isEmpty()) {
			if(!actual.isEmpty()) {
				actualObject = children.get(actual);
			} else {
				actualObject = children.values().iterator().next();
			}
			
			if(!name.isEmpty()) {
				actual = name.charAt(0) +"";
				name = name.substring(1);
			} else {
				actual = "";
			}
			children = actualObject.getChildren();
		}
		
		//TODO if present ? 
		return actualObject.getDescription();
	}

	
	
	

}
