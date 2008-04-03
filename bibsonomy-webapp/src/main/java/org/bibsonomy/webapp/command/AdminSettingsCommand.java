package org.bibsonomy.webapp.command;

import org.bibsonomy.common.enums.ClassifierSettings;

/**
 * Bean for classifier settings 
 * 
 * @author Stefan Stützer
 * @version $Id$
 */
public class AdminSettingsCommand {

	private String mode;
	
	private String algorithm;
	
	private String trainingPeriod;
	
	private String classifyPeriod;
	
	private String probabilityLimit;

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

	public String getClassifyPeriod() {
		return this.classifyPeriod;
	}

	public String getProbabilityLimit() {
		return this.probabilityLimit;
	}	
}