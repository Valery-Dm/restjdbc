package dmv.spring.demo.rest.exceptionhandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import dmv.spring.demo.model.exceptions.AccessDataBaseException;
import dmv.spring.demo.model.exceptions.EntityAlreadyExistsException;
import dmv.spring.demo.model.exceptions.EntityDoesNotExistException;

/**
 * Maps Persistence Layer Exceptions to HTTP codes.
 * Also other errors will be caught and mapped.
 * @author dmv
 */
@RestControllerAdvice
public class ExceptionHandlers {

	@ExceptionHandler({ EntityDoesNotExistException.class,
		                NoHandlerFoundException.class })
	@ResponseStatus(NOT_FOUND)
	public @ResponseBody ErrorInfo notFound(HttpServletRequest req,
			                                Exception ex) {

		return new ErrorInfo(req.getRequestURI(), ex);
	}

	@ExceptionHandler(EntityAlreadyExistsException.class)
	@ResponseStatus(CONFLICT)
	public @ResponseBody ErrorInfo conflict(HttpServletRequest req,
			                                EntityAlreadyExistsException ex) {

		return new ErrorInfo(req.getRequestURI(), ex);
	}

	/*
	 * SQL connection problems
	 */
	@ExceptionHandler({ AccessDataBaseException.class,
		                SQLException.class })
	@ResponseStatus(INTERNAL_SERVER_ERROR)
	public @ResponseBody ErrorInfo sqlErrors(HttpServletRequest req) {

		return new ErrorInfo(req.getRequestURI(), "We have Database problems. Please try again later");
	}

	/*
	 * We are not exposing internal errors
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(INTERNAL_SERVER_ERROR)
	public @ResponseBody ErrorInfo otherErrors(HttpServletRequest req) {

		return new ErrorInfo(req.getRequestURI(), "Something wrong has happened. Please try again later");
	}

	/*
	 * Badly formatted HTTP requests may throw IllegalArgumentException.
	 * (This is not for my App exceptions - those mean bugs, as they should do).
	 *
	 * UriUtils decode method throws IllegalArgumentException when the given
	 * source contains invalid encoded sequences (rest/users/?email=mail@1%%%@).
	 *
	 * Tomcat (raw url 'rest/users/?email=mail@@\r\n'):
	 * Illegal characters in address line could be caught before any Spring Validations, like this one:
	 * Invalid character found in the request target. The valid characters are defined in RFC 7230 and RFC 3986
	 * org.apache.coyote.http11.Http11InputBuffer.parseRequestLine(Http11InputBuffer.java:471) ~[tomcat-embed-core-8.5.11.jar!/:8.5.11]
	 * Which resulted in IllegalArgumentException.
	 * IllegalArgumentException should be an 'internal' exception meaning that developer has done something wrong.
	 * But here it mitigates the issue with user's input. This exception can't be caught at this level.
	 * And, it's not my purpose here to dealing with Tomcat configurations.
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(BAD_REQUEST)
	public @ResponseBody ErrorInfo badRequest(HttpServletRequest req,
			                                  IllegalArgumentException ex) {

		return new ErrorInfo(req.getRequestURI(), ex);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(BAD_REQUEST)
	public @ResponseBody ErrorInfo malformedJSON(HttpServletRequest req,
			                                     HttpMessageNotReadableException ex) {

		return new ErrorInfo(req.getRequestURI(), ex.getMostSpecificCause().getLocalizedMessage());
    }

	@ExceptionHandler({ TypeMismatchException.class,
		                HttpMediaTypeException.class,
		                UnsupportedEncodingException.class,
		                ServletException.class })
	@ResponseStatus(BAD_REQUEST)
	public @ResponseBody ErrorInfo methodArgumentNotValid(HttpServletRequest req,
			                                              Exception ex) {

		return new ErrorInfo(req.getRequestURI(), ex);
	}

	/*
	 * Simplify Spring validation error output
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(BAD_REQUEST)
	public @ResponseBody ErrorInfo methodArgumentNotValid(HttpServletRequest req,
                                                   MethodArgumentNotValidException ex) {

        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return new ErrorInfo(req.getRequestURI(), processFieldErrors(fieldErrors));
    }

    private String processFieldErrors(List<FieldError> fieldErrors) {
    	if (fieldErrors == null || fieldErrors.size() == 0) return "unknown cause";
        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError: fieldErrors) {
            builder.append(fieldError.getField())
            .append(": ")
            .append(fieldError.getDefaultMessage())
            .append("; ");
        }
        builder.delete(builder.length() - 2, builder.length());
        return builder.toString();
    }
}
