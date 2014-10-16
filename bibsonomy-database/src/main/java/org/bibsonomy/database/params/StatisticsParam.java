package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.SpamStatus;

/**
 * @author Christian Kramer
 */
public class StatisticsParam extends GenericParam {
	private Classifier classifier;
	
	private int interval;
	
	private SpamStatus spamStatus;

	/**
	 * @return the classifier
	 */
	public Classifier getClassifier() {
		return this.classifier;
	}

	/**
	 * @param classifier the classifier to set
	 */
	public void setClassifier(Classifier classifier) {
		this.classifier = classifier;
	}

	/**
	 * @return the spamStatus
	 */
	public SpamStatus getSpamStatus() {
		return this.spamStatus;
	}

	/**
	 * @param spamStatus the spamStatus to set
	 */
	public void setSpamStatus(SpamStatus spamStatus) {
		this.spamStatus = spamStatus;
	}

	/**
	 * @return the interval
	 */
	public int getInterval() {
		return this.interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}
}