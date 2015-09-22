package org.bibsonomy.model.logic.util;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.LogicInterfaceFactory;

/**
 * read only aware {@link LogicInterfaceFactory}
 * @author dzo
 */
public class ReadOnlyAwareLogicInterfaceFactory implements LogicInterfaceFactory {
	
	private final boolean readOnly;
	private final LogicInterfaceFactory logicInterfaceFactory;
	
	/**
	 * @param logicInterfaceFactory
	 * @param readOnly
	 */
	public ReadOnlyAwareLogicInterfaceFactory(LogicInterfaceFactory logicInterfaceFactory, boolean readOnly) {
		super();
		this.readOnly = readOnly;
		this.logicInterfaceFactory = logicInterfaceFactory;
	}


	/* (non-Javadoc)
	 * @see org.bibsonomy.model.logic.LogicInterfaceFactory#getLogicAccess(java.lang.String, java.lang.String)
	 */
	@Override
	public LogicInterface getLogicAccess(String loginName, String apiKey) {
		final LogicInterface logic = this.logicInterfaceFactory.getLogicAccess(loginName, apiKey);
		return ReadOnlyLogic.maskLogic(logic, this.readOnly);
	}

}
