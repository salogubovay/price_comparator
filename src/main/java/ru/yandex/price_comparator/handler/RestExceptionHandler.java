package ru.yandex.price_comparator.handler;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ru.yandex.price_comparator.dto.error.Error;
import ru.yandex.price_comparator.exception.ValidationException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
	 @ExceptionHandler(ValidationException.class)
	    public ResponseEntity<?> handleValidationError(ValidationException ve, HttpServletRequest request){
	        Error error = new Error();
	        error.setCode(HttpStatus.BAD_REQUEST.value());
	        error.setMessage("Validation Failed");
	        return new ResponseEntity<>(error, null, HttpStatus.BAD_REQUEST); 
	    }
}
