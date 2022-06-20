package ru.yandex.price_comparator.exception;

public class ItemNotFoundException extends RuntimeException{
	private static final long serialVesionUID = 1L;
    
    public ItemNotFoundException() {}
    
    public ItemNotFoundException(Integer code, String message) {
        super(message);
    }
    
    public ItemNotFoundException(Integer code, String message, Throwable cause) {
        super(message, cause);
    }
}
