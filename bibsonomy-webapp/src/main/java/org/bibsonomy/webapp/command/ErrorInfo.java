package org.bibsonomy.webapp.command;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dng
 */
@Setter
@Getter
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
	 * 
	 * @param position
	 * adds position to the list of positions
	 */
	public void addPosition(int position) {
		this.positions.add(position);
	}

	@Override
	public int compareTo(ErrorInfo o) {
		if (this.errorType.equals(o.getErrorType())) {
			return this.errorField.length() - o.getErrorField().length();
		}

		return this.errorType.compareTo(o.getErrorType());
	}
	
}
