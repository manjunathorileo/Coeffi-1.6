package com.dfq.coeffi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

@Configuration
@EnableResourceServer
public class ResourceServer extends WebSecurityConfigurerAdapter
{

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	@Qualifier("userDetailsService")
	private UserDetailsService customUserDetailService;

	public ResourceServer() {
		super(true);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception{

		http.requestMatchers().antMatchers("/login", "/swagger-ui")
				.and().authorizeRequests().anyRequest().authenticated();
		http.csrf().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder authBuilder) throws Exception {
		authBuilder.parentAuthenticationManager(authenticationManager)
			.userDetailsService(customUserDetailService);
	}
}