package org.bibsonomy.database;

import org.apache.shindig.auth.SecurityToken;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.util.ReadOnlyAwareLogicInterfaceFactory;
import org.bibsonomy.model.logic.util.ReadOnlyLogic;

/**
 * a read only aware {@link ShindigLogicInterfaceFactory}
 * @see ReadOnlyAwareLogicInterfaceFactory
 *
 * @author dzo
 */
public class ReadOnlyAwareShindigDBLogicUserInterfaceFactory implements ShindigLogicInterfaceFactory {
	
	private final ShindigLogicInterfaceFactory factory;
	private final boolean readOnly;

	/**
	 * @param factory
	 * @param readOnly
	 */
	public ReadOnlyAwareShindigDBLogicUserInterfaceFactory(ShindigLogicInterfaceFactory factory, boolean readOnly) {
		this.factory = factory;
		this.readOnly = readOnly;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.ShindigLogicInterfaceFactory#getLogicAccess(org.apache.shindig.auth.SecurityToken)
	 */
	@Override
	public LogicInterface getLogicAccess(SecurityToken st) {
		final LogicInterface logicAccess = this.factory.getLogicAccess(st);
		return ReadOnlyLogic.maskLogic(logicAccess, this.readOnly);
	}

}
