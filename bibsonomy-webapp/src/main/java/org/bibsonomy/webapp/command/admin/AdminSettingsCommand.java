package org.bibsonomy.webapp.command.admin;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.enums.ClassifierAlgorithm;
import org.bibsonomy.common.enums.ClassifierMode;
import org.bibsonomy.common.enums.ClassifierSettings;

/**
 * Bean for classifier settings
 * 
 * @author Stefan Stützer
 * @author Beate Krause
 * @version $Id: AdminSettingsCommand.java,v 1.2 2008-04-07 13:25:48
 *          ss05sstuetzer Exp $
 */
public class AdminSettingsCommand {

	/** Day or night mode of classifier */
	private String mode;

	/** Algorithm of classifier */
	private String algorithm;

	/** Length of training period */
	private String trainingPeriod;

	/** Length of classification period */
	private String classifyPeriod;

	/** Probability limit (threshold for sure / not sure classification) */
	private String probabilityLimit;
	
	/** Costs of false positives */
	private String classificationCosts;
	
	/** White list expression to add into database */
	private String whitelistExp;

	/** Options to present different algorithms */
	private Map<String, String> algorithmOptions;
	
	/** Options to present different modes */
	private Map<String, String> modeOptions;

	public AdminSettingsCommand() {
		
		/**
		 * initialize options
		 */
		
		//algorithm options
		// TODO enum in options übergeben
		algorithmOptions = new HashMap<String, String>();
		algorithmOptions.put(ClassifierAlgorithm.IBK.name(), ClassifierAlgorithm.IBK.getDescription());
		algorithmOptions.put(ClassifierAlgorithm.NAIVE_BAYES.name(), ClassifierAlgorithm.NAIVE_BAYES.getDescription());
		algorithmOptions.put(ClassifierAlgorithm.J48.name(), ClassifierAlgorithm.J48.getDescription());
		algorithmOptions.put(ClassifierAlgorithm.ONER.name(), ClassifierAlgorithm.ONER.getDescription());
		algorithmOptions.put(ClassifierAlgorithm.LOGISTIC.name(), ClassifierAlgorithm.LOGISTIC.getDescription());
		algorithmOptions.put(ClassifierAlgorithm.LIBSVM.name(), ClassifierAlgorithm.LIBSVM.getDescription());
		algorithmOptions.put(ClassifierAlgorithm.SMO.name(), ClassifierAlgorithm.SMO.getDescription());
		
		//mode options
		modeOptions = new HashMap<String, String>();
		modeOptions.put(ClassifierMode.DAY.name(),ClassifierMode.DAY.getAbbreviation());
		modeOptions.put(ClassifierMode.NIGHT.name(), ClassifierMode.NIGHT.getAbbreviation());
		
	}

	/**
	 * set options for classification task
	 * @param setting
	 * @param value
	 */
	public void setAdminSetting(final ClassifierSettings setting, final String value) {
		switch (setting) {
		case ALGORITHM:
			this.algorithm = value;
			break;
		case MODE:
			this.mode = value;
			break;
		case TRAINING_PERIOD:
			this.trainingPeriod = value;
			break;
		case CLASSIFY_PERIOD:
			this.classifyPeriod = value;
			break;
		case PROBABILITY_LIMIT:
			this.probabilityLimit = value;
			break;
		case CLASSIFY_COST:
			this.classificationCosts = value;
			break;
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

	public Map<String,String> getAlgorithmOptions() {
		return this.algorithmOptions;
	}

	public void setAlgorithmOptions(Map<String,String> algorithmOptions) {
		this.algorithmOptions = algorithmOptions;
	}

	public Map<String, String> getModeOptions() {
		return this.modeOptions;
	}

	public void setModeOptions(Map<String, String> modeOptions) {
		this.modeOptions = modeOptions;
	}
	
	public String getClassificationCosts() {
		return this.classificationCosts;
	}

	public void setClassificationCosts(String classificationCosts) {
		this.classificationCosts = classificationCosts;
	}
	
	public String getWhitelistExp() {
		return this.whitelistExp;
	}

	public void setWhitelistExp(String whitelistExp) {
		this.whitelistExp = whitelistExp;
	}
	
	public void setClassifyPeriod(String classifyPeriod) {
		this.classifyPeriod = classifyPeriod;
	}

	public void setProbabilityLimit(String probabilityLimit) {
		this.probabilityLimit = probabilityLimit;
	}
}