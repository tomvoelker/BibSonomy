package org.bibsonomy.model.logic.exception;

/**
 * Abstract base class for exceptions arising from logical constraints on the business logic. This is handy for checking the model state in the logic layer and properly reacting in the controller and view. 
 *
 * @author jensi
 */
public abstract class LogicException extends Exception {

	private static final long serialVersionUID = 8173305639911279277L;

}
