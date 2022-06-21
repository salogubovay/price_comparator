package ru.yandex.price_comparator.handler;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ru.yandex.price_comparator.error.Error;
import ru.yandex.price_comparator.exception.ItemNotFoundException;
import ru.yandex.price_comparator.exception.ValidationException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	 @ExceptionHandler(ValidationException.class)
	    public ResponseEntity<?> handleValidationException(ValidationException ve, HttpServletRequest request){
	        Error error = new Error();
	        error.setCode(HttpStatus.BAD_REQUEST.value());
	        error.setMessage("Validation Failed");
	        return new ResponseEntity<>(error, null, HttpStatus.BAD_REQUEST); 
	    }
	 @ExceptionHandler(ItemNotFoundException.class)
	    public ResponseEntity<?> handleItemNotFoundException(ItemNotFoundException ve, HttpServletRequest request){
	        Error error = new Error();
	        error.setCode(HttpStatus.NOT_FOUND.value());
	        error.setMessage("Item not found");
	        return new ResponseEntity<>(error, null, HttpStatus.NOT_FOUND); 
	    }
}
