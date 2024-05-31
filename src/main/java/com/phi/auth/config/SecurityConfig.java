package com.phi.auth.config;

import com.phi.auth.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableConfigurationProperties(JwtProperties.class)
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    private final JwtService service;

    public SecurityConfig(JwtService service) {
        this.service = service;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthHandler handler = new AuthHandler(service);
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(
                                "/actuator/**",
                                "/manager/**",
                                "/api/v1/auth/oauth2/github",
                                "/welcome",
                                "/error"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .formLogin(configurer -> configurer
                        .successHandler(handler)
                        .failureHandler(handler));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        UserDetails user = User.withUsername("user")
                .password(encoder.encode("123456"))
                .roles("USER")
                .build();

        UserDetails userDetails = User.withUserDetails(user)
                .build();

        return new InMemoryUserDetailsManager(userDetails);
    }

}