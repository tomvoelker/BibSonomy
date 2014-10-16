package org.bibsonomy.database.params;

import org.bibsonomy.common.enums.Classifier;
import org.bibsonomy.common.enums.SpamStatus;
import org.bibsonomy.common.enums.StatisticsConstraint;
import org.bibsonomy.common.enums.StatisticsUnit;

/**
 * @author Christian Kramer
 */
public class StatisticsParam extends GenericParam {
	private StatisticsConstraint constraint;
	private StatisticsUnit unit;
	
	private Classifier classifier;
	
	private Integer interval;
	
	private SpamStatus spamStatus;

	/**
	 * @return the constraint
	 */
	public StatisticsConstraint getConstraint() {
		return this.constraint;
	}

	/**
	 * @param constraint the constraint to set
	 */
	public void setConstraint(StatisticsConstraint constraint) {
		this.constraint = constraint;
	}

	/**
	 * @return the unit
	 */
	public StatisticsUnit getUnit() {
		return this.unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(StatisticsUnit unit) {
		this.unit = unit;
	}

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
	public Integer getInterval() {
		return this.interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(Integer interval) {
		this.interval = interval;
	}
}