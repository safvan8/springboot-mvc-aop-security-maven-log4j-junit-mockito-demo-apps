package com.safvan.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
				// create database connection
				.dataSource(dataSource).usersByUsernameQuery("select uname,upwd,uenabled from usertab where uname=?")
				.authoritiesByUsernameQuery("select uname,urole from usertab where uname=?")
				.passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.authorizeRequests()
	
		.antMatchers("/home").permitAll()
		.antMatchers("/welcome").authenticated()
		.antMatchers("/admin").hasAuthority("ADMIN")
		.antMatchers("/emp").hasAuthority("EMPLOYEE")
		.antMatchers("/std").hasAuthority("STUDENT")
		.anyRequest().authenticated()
		
		.and()
		.formLogin()
		.defaultSuccessUrl("/welcome",true)
		
		.and()
		.logout()
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		.and()
		.exceptionHandling()
		.accessDeniedPage("/denied")
		;
	}
}
