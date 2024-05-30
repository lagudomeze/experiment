package com.phi.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuthenticationEvents {

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        log.info("{}", success);
        if (success.getSource() instanceof OAuth2LoginAuthenticationToken token) {
            OAuth2User user = token.getPrincipal();
            String githubId = user.getAttributes().get("id").toString();
            log.info("id:{}", githubId);
            // save to users table
        }
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
        log.warn("{}", failures);
    }
}