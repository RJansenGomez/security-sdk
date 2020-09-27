package org.rjansen.sdk.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    protected SecurityFilter filter;

    public WebSecurityConfig(final SecurityFilter filter) {
        this.filter = filter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests().anyRequest().authenticated();
        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
