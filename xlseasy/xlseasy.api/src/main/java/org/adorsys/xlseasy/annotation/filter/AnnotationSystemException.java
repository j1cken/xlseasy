package org.adorsys.xlseasy.annotation.filter;

import java.util.HashMap;
import java.util.Map;

import org.adorsys.xlseasy.annotation.ErrorCodeSheet;

/**
 * @author Sandro Sonntag
 */
public class AnnotationSystemException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private ErrorCodeSheet errorCode;

	private Throwable cause;
	
	private Map<String, Object> values;
	
	/**
	 * @param code
	 * @param cause
	 * @param values
	 */
	public AnnotationSystemException(ErrorCodeSheet code, Throwable cause, Map<String, Object> values) {
		this.errorCode = code == null ? ErrorCodeSheet.UNKNOWN : code;
		this.cause = cause;
		this.values = values == null ? new HashMap<String, Object>() : values;
	}

	/**
	 * @param code
	 * @param cause
	 */
	public AnnotationSystemException(ErrorCodeSheet code, Throwable cause) {
		this(code, cause, null);
	}

	/**
	 * @param code
	 */
	public AnnotationSystemException(ErrorCodeSheet code) {
		this(code, null, null);
	}

	public AnnotationSystemException addValue(String descriptor, Object value) {
		values.put(descriptor, value);
		return this;
	}

	public ErrorCodeSheet getErrorCode() {
		return errorCode;
	}

	@Override
	public Throwable getCause() {
		return this.cause;
	}

	public String dump() {
		return values.toString();
	}

}
