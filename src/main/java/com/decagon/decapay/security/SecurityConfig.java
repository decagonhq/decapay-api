package com.decagon.decapay.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * For user login
     */
    @Configuration
    @Order(1)
    public static class ClientWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

//        @Value("${api.basepath-api}")
        private final String path = "/api/v1";

        @Autowired
        private JwtRequestFilter jwtRequestFilter;
        @Autowired
        private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
        
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        CustomUserDetailsService userDetailsService;

        private final String[] AUTH_WHITELIST = {
                path +"/signin",
                path + "/forgot-password",
                path + "/reset-password",
                path + "/register",
                path + "/verify-code"
        };

        @Override
        public void configure(WebSecurity web) {
        }

        public ClientWebSecurityConfigurerAdapter() {
            super();
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.cors();
            http.csrf().disable()
                    .antMatcher(path+"/**").authorizeRequests()
                    .antMatchers(AUTH_WHITELIST).permitAll()
                    .anyRequest().authenticated().and().exceptionHandling().authenticationEntryPoint(restAuthenticationEntryPoint)
                    .and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                    .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        }

        @Bean("userAuthenticationManager")
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Override
        @Autowired
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        }
    }
}