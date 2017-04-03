package dmv.spring.demo.rest.exceptionhandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
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
 * Maps Persistence Layer Exceptions to HTTP codes
 * @author dmv
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
	
	/*
	 * Illegal characters in address line could be caught before any Validations, like this one:
	 * Invalid character found in the request target. The valid characters are defined in RFC 7230 and RFC 3986
	 * org.apache.coyote.http11.Http11InputBuffer.parseRequestLine(Http11InputBuffer.java:471) ~[tomcat-embed-core-8.5.11.jar!/:8.5.11]
	 * Which resulted in IllegalArgumentException.
	 * IllegalArgumentException should be an 'internal' exception meaning that developer has done something wrong.
	 * But here it mitigates the issue with user's input.
	 * Write now I don't know how to overcome this issue with Tomcat.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(BAD_REQUEST)
	public @ResponseBody ErrorInfo badRequest(HttpServletRequest req,
			                                  IllegalArgumentException ex) {
		return new ErrorInfo(req.getRequestURI(), ex);
	}
}
