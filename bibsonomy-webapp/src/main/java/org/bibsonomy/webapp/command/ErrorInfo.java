package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dng
 */
public class ErrorInfo implements Comparable<ErrorInfo>{
	
	/**
	 * different types of errors
	 */
	public enum ErrorType {
		NO_ERROR,
		FIELD_ERROR,
		POST_ERROR
	}
	
	/**
	 * description of the exact error in the post
	 */
	private String errorField;
	
	/**
	 * list of integers where the error occurs
	 */
	private List<Integer> positions;
	
	/**
	 * type of error (used for sorting)
	 */
	private ErrorType errorType;

	/**
	 * Constructor 
	 * 
	 * @param errorField
	 * @param position - single position where the error occurs
	 * @param errorType
	 */
	public ErrorInfo(final String errorField, int position, ErrorType errorType) {
		this.errorField = errorField;
		this.positions = new ArrayList<>();
		this.positions.add(position);
		this.setErrorType(errorType);
	}
	
	/**
	 * Constructor 
	 * 
	 * @param errorField
	 * @param positions - list of positions where the error occurs
	 * @param errorType
	 */
	public ErrorInfo(final String errorField, List<Integer> positions, ErrorType errorType) {
		this.errorField = errorField;
		this.positions = positions;
		this.setErrorType(errorType);
	}
	
	/**
	 * @return the errorField
	 */
	public String getErrorField() {
		return this.errorField;
	}
	
	public void setErrorField(String errorField) {
		this.errorField = errorField;
	}
	
	/**
	 * 
	 * @param position
	 * adds position to the list of positions
	 */
	public void addPosition(int position) {
		this.positions.add(position);
	}
	
	/**
	 * @return the list of positions
	 */
	public List<Integer> getPositions() {
		return this.positions;
	}
	
	/**
	 * @param positions
	 */
	public void setPositions(final List<Integer> positions) {
		this.positions = positions;
	}
	
	/**
	 * @return error type
	 */
	public ErrorType getErrorType() {
		return errorType;
	}
	
	/**
	 * @param errorType
	 */
	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}

	@Override
	public int compareTo(ErrorInfo o) {
		if (this.errorType.equals(o.getErrorType())) {
			return this.errorField.length() - o.getErrorField().length();
		}

		return this.errorType.compareTo(o.getErrorType());
	}
	
}
