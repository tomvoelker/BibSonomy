package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.enums.ClassifierSettings;

/**
 * Bean for classifier settings
 * 
 * @author Stefan Stützer
 * @version $Id: AdminSettingsCommand.java,v 1.2 2008-04-07 13:25:48
 *          ss05sstuetzer Exp $
 */
public class AdminSettingsCommand {

	private String mode;

	private String algorithm;

	private String trainingPeriod;

	public void setClassifyPeriod(String classifyPeriod) {
		this.classifyPeriod = classifyPeriod;
	}

	public void setProbabilityLimit(String probabilityLimit) {
		this.probabilityLimit = probabilityLimit;
	}

	private String classifyPeriod;

	private String probabilityLimit;

	private String testing;

	private Map<String, String> algorithmOptions;
	
	private Map<String, String> testingOptions;
	
	private Map<String, String> modeOptions;

	public AdminSettingsCommand() {
		
		/**
		 * initialize options
		 */
		
		//algorithm options
		algorithmOptions = new HashMap<String, String>();
		algorithmOptions.put("IBk", "weka.classifiers.lazy.IBk");
		algorithmOptions.put("NaiveBayes", "weka.classifiers.bayes.NaiveBayes");
		algorithmOptions.put("J48", "weka.classifiers.trees.J48");
		algorithmOptions.put("OneR", "weka.classifiers.rules.OneR");
		algorithmOptions.put("Logistic", "weka.classifiers.functions.Logistic");
		algorithmOptions.put("LibSVM", "weka.classifiers.functions.LibSVM");
		algorithmOptions.put("SMO", "weka.classifiers.functions.SMO");
		
		//testing options
		testingOptions = new HashMap<String, String>();
		testingOptions.put("on", "on");
		testingOptions.put("off", "off");
		
		//mode options
		modeOptions = new HashMap<String, String>();
		modeOptions.put("Day", "d");
		modeOptions.put("Night", "n");
		
	}

	public void setAdminSetting(final ClassifierSettings setting, final String value) {
		if (setting.equals(ClassifierSettings.ALGORITHM)) {
			this.algorithm = value;
		} else if (setting.equals(ClassifierSettings.MODE)) {
			this.mode = value;
		} else if (setting.equals(ClassifierSettings.TRAINING_PERIOD)) {
			this.trainingPeriod = value;
		} else if (setting.equals(ClassifierSettings.CLASSIFY_PERIOD)) {
			this.classifyPeriod = value;
		} else if (setting.equals(ClassifierSettings.PROBABILITY_LIMIT)) {
			this.probabilityLimit = value;
		} else if (setting.equals(ClassifierSettings.TESTING)) {
			this.testing = value;
		}
	}

	public String getMode() {
		return this.mode;
	}

	public String getAlgorithm() {
		return this.algorithm;
	}

	public String getTrainingPeriod() {
		return this.trainingPeriod;
	}

	public void setTrainingPeriod(String trainingPeriod) {
		this.trainingPeriod = trainingPeriod;
	}

	public String getClassifyPeriod() {
		return this.classifyPeriod;
	}

	public String getProbabilityLimit() {
		return this.probabilityLimit;
	}

	public String getTesting() {
		return this.testing;
	}

	public Map<String,String> getAlgorithmOptions() {
		return this.algorithmOptions;
	}

	public void setAlgorithmOptions(Map<String,String> algorithmOptions) {
		this.algorithmOptions = algorithmOptions;
	}

	public Map<String, String> getTestingOptions() {
		return this.testingOptions;
	}

	public void setTestingOptions(Map<String, String> testingOptions) {
		this.testingOptions = testingOptions;
	}

	public Map<String, String> getModeOptions() {
		return this.modeOptions;
	}

	public void setModeOptions(Map<String, String> modeOptions) {
		this.modeOptions = modeOptions;
	}
}