package com.phi.auth.service;

import com.phi.auth.dao.UserRepository;
import com.phi.auth.dao.UserRepository.NewUser;
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

    private final UserRepository repository;

    public AuthenticationEvents(UserRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        if (success.getSource() instanceof OAuth2LoginAuthenticationToken token &&
            "github".equals(token.getClientRegistration().getRegistrationId())) {
            OAuth2User user = token.getPrincipal();
            String userId = "gh_" + user.getAttribute("id");
            if (!repository.existsById(userId)) {
                NewUser entity = new NewUser();
                entity.setId(userId);
                entity.setName(user.getAttribute("name"));
                entity.setSource(user.getAttribute("email"));
                repository.save(entity);
                log.info("user:{} id:{} created", user.getName(), userId);
            }
        }
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
        log.warn("{}", failures);
    }
}