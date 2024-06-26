package com.personal.beans.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userService;

    public SecurityConfig(UserDetailsService userService) {
        this.userService = userService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(this.userService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/resources/**").hasAnyAuthority("user", "admin")
                .antMatchers("/bean/{beanName}", "/resources/**").hasAnyAuthority("user", "admin")
                .antMatchers("/beans", "/resources/**").hasAnyAuthority("user", "admin")
                .antMatchers("/bean/{beanName}/comment", "/resources/**").hasAnyAuthority("user", "admin")
                .antMatchers("/bean", "/resources/**").hasAnyAuthority("user", "admin")
                .antMatchers("/bean", "/resources/**").hasAnyAuthority("user", "admin")
                .antMatchers("/beans/unapproved", "/resources/**").hasAnyAuthority("admin")
                .antMatchers("/bean/{beanName}/versions/unapproved", "/resources/**").hasAnyAuthority("admin")
                .antMatchers("/bean/version/approve", "/resources/**").hasAnyAuthority("admin")
                .antMatchers("/users", "/resources/**").hasAnyAuthority("admin")
                .anyRequest().authenticated()
                .and()
                .formLogin().permitAll()
                .and()
                .logout().permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/403");
    }
}
