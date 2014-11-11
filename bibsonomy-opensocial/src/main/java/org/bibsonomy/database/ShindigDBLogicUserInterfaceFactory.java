package org.bibsonomy.database;

import static org.bibsonomy.util.ValidationUtils.present;

import org.apache.shindig.auth.SecurityToken;
import org.bibsonomy.model.logic.LogicInterface;

/**
 * @author fmi
 */
public class ShindigDBLogicUserInterfaceFactory extends DBLogicNoAuthInterfaceFactory implements ShindigLogicInterfaceFactory {
	
	@Override
	public LogicInterface getLogicAccess(final SecurityToken st) {
		if (present(st) && !st.isAnonymous()) {
			return this.getLogicAccess(st.getViewerId(), null);
		}
		
		return this.getLogicAccess(null, null);
	}
}
