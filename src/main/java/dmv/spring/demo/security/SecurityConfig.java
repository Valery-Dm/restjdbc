package dmv.spring.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private AuthenticationEntryPoint customAuthenticationEntryPoint;
	
	@Autowired
	private AccessDeniedHandler customAccessDeniedHandler;
	
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@Autowired
	public DaoAuthenticationProvider getAuthenticationProvider(
									UserDetailsService userDetailsService,
									PasswordEncoder passwordEncoder) {
		return new CustomAuthenticationProvider(userDetailsService, passwordEncoder);
	}

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authManagerBuilder,
    		                    DaoAuthenticationProvider authProvider) throws Exception {
    	authManagerBuilder.authenticationProvider(authProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http
            .httpBasic()
            .authenticationEntryPoint(customAuthenticationEntryPoint)
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .authorizeRequests()
            .antMatchers("/logout").anonymous()
            .antMatchers("/").anonymous()
            .antMatchers("/v2/").anonymous()
            .antMatchers(HttpMethod.GET, "/rest/**")
            .hasAuthority("ADM")
            .antMatchers(HttpMethod.POST, "/rest/**")
            .hasAuthority("ADM")
            .antMatchers(HttpMethod.PUT, "/rest/**")
            .hasAuthority("ADM")
            .antMatchers(HttpMethod.DELETE, "/rest/**")
            .hasAuthority("ADM")
        .and()
	    	.exceptionHandling()
	    	.accessDeniedHandler(customAccessDeniedHandler)
	    	.authenticationEntryPoint(customAuthenticationEntryPoint)
        .and()
            .csrf().disable();
    }

}
