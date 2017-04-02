/**
 * 
 */
package dmv.spring.demo.rest.exceptionhandler;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

/**
 * Maps Exceptions to HTTP codes
 * @author user
 */
@RestControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler(EntityDoesNotExistException.class)
	@ResponseStatus(NOT_FOUND)
	public @ResponseBody ErrorInfo notFound(HttpServletRequest req, 
			                                EntityDoesNotExistException ex) {
		return new ErrorInfo(req.getRequestURI(), ex);
	}
	
	@ExceptionHandler(EntityAlreadyExistsException.class)
	@ResponseStatus(CONFLICT)
	public @ResponseBody ErrorInfo conflict(HttpServletRequest req, 
			                                EntityAlreadyExistsException ex) {
		return new ErrorInfo(req.getRequestURI(), ex);
	}
}
