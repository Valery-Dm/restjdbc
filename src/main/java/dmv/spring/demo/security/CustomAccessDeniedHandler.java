package dmv.spring.demo.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import dmv.spring.demo.rest.exceptionhandler.ErrorInfo;

/**
 * Handles 403 errors sending JSON-formatted output
 * @author dmv
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

	/* (non-Javadoc)
	 * @see org.springframework.security.web.access.AccessDeniedHandler#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.access.AccessDeniedException)
	 */
	@Override
	public void handle(HttpServletRequest request,
			           HttpServletResponse response,
			           AccessDeniedException accessDeniedException)
			        		   throws IOException, ServletException {
		ObjectMapper mapper = new ObjectMapper();
		HttpServletResponse httpResponse = response;
		httpResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
	    ErrorInfo info = new ErrorInfo(request.getRequestURI(), accessDeniedException);
	    mapper.writeValue(httpResponse.getOutputStream(), info);
	}

}
