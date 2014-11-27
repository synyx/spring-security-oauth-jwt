package de.synyx.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Provides AuthenticationManager with some simple in-memory users, used to
 * authenticate users for {@link AuthorizationServer}
 *
 * In a real application, this should be replaced by an {@link AbstractLdapAuthenticationProvider} or something similar
 *
 */
@EnableWebSecurity
@Configuration
public class AuthenticationProvider extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("hdampf").password("wert123$").roles("USER").and()
                .withUser("fschmidt").password("wert123$").roles("USER", "ADMIN");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
