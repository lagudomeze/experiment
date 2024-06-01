package com.phi.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class AuthService {

    @Value("${spring.security.user.name}")
    private String defaultUser;

    public String userId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2AuthenticationToken token) {
            OAuth2User user = token.getPrincipal();
            return "gh_" + user.getAttribute("id");
        } else {
            if (Objects.equals(authentication.getName(), defaultUser)) {
                return authentication.getName();
            } else {
                throw new RuntimeException("not here");
            }
        }
    }
}
