package de.unikassel.puma.openaccess.classification.chain.parser;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;

import de.unikassel.puma.openaccess.classification.ClassificationObject;
import de.unikassel.puma.openaccess.classification.ClassificationTextParser;

public class DDCClassification extends ClassificationTextParser {

	private int currentDepth = 0;
	
	public void parse(BufferedReader bf) throws IOException {
		classifications = new LinkedHashMap<String, ClassificationObject>();
		
		while(bf.ready()) {
			
			String line = bf.readLine();
			
			if(!present(line)) {
				currentDepth++;
				continue;
			}
			
			line = line.trim();
			String [] lineArray = line.split(" ", 2);
			try {
				classificate(lineArray[0], lineArray[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				//unable to parse
				classifications = null;
				return;
			}
		}
	}
	
	private void requClassificate(String name, String description, ClassificationObject object, int current) {
		if(!present(name))
			return;
		
		String actual = name.charAt(0) +"";
		name = name.substring(1);
		
		if(current >= currentDepth) {
			if(object.getChildren().containsKey(actual)) {
				object.getChildren().get(actual).setDescription(description);
				requClassificate(name, description, object.getChildren().get(actual), current +1);
			
			} else {
				if(name.isEmpty()) {
					ClassificationObject co = new ClassificationObject(actual, description);
					object.addChild(actual, co);
					
				} else {
					ClassificationObject co = new ClassificationObject(actual, description);
					object.addChild(actual, co);
					requClassificate(name, description, co, current +1);
				}
			}
		} else {
			
			if(object.getChildren().containsKey(actual)) {
				requClassificate(name, description, object.getChildren().get(actual), current +1);
			
			} else {
				if(name.isEmpty()) {
					ClassificationObject co = new ClassificationObject(actual, description);
					object.addChild(actual, co);
					
				} else {
					ClassificationObject co = new ClassificationObject(actual, description);
					object.addChild(actual, co);
					requClassificate(name, description, co, current +1);
				}
			}
			
		}
	}
	

	private void classificate(String name, final String description) {
		String actual = name.charAt(0) +"";
		name = name.substring(1);
	
		if(classifications.containsKey(actual)) {
			requClassificate(name, description, classifications.get(actual), 1);
		} else {
			ClassificationObject co = new ClassificationObject(actual, description);
			classifications.put(actual, co);
			requClassificate(name, description, co, 1);
		}
	}
	
	@Override
	public String getDelimiter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<String, ClassificationObject> getList() {
		return classifications;
	}

}
