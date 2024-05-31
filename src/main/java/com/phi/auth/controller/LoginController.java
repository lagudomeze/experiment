package com.phi.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class LoginController {

    private final OAuth2AuthorizationRequestResolver resolver;

    public LoginController(ClientRegistrationRepository repository) {
        this.resolver = new DefaultOAuth2AuthorizationRequestResolver(repository,
                "/oauth2/authorization");
    }

    @GetMapping("/oauth2/github")
    public String github(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = resolver.resolve(request, "github");
        return authorizationRequest.getAuthorizationRequestUri();
    }


}
