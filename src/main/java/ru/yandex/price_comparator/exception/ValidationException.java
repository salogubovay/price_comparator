package ru.yandex.price_comparator.exception;

public class ValidationException extends RuntimeException{
		private static final long serialVesionUID = 1L;
	    
	    public ValidationException() {}
	    
	    public ValidationException(Integer code, String message) {
	        super(message);
	    }
	    
	    public ValidationException(Integer code, String message, Throwable cause) {
	        super(message, cause);
	    }
}
