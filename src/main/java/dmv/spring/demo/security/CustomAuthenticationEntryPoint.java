package dmv.spring.demo.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import dmv.spring.demo.rest.exceptionhandler.ErrorInfo;

/**
 * Yield JSON-formatted error message for code 401
 * @author dmv
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/* (non-Javadoc)
	 * @see org.springframework.security.web.AuthenticationEntryPoint#commence(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		ObjectMapper mapper = new ObjectMapper();
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setContentType("application/json");
		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    ErrorInfo info = new ErrorInfo(request.getRequestURI(), authException);
	    mapper.writeValue(httpResponse.getOutputStream(), info);
	}

}
