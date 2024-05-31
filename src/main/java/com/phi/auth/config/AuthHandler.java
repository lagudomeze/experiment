package com.phi.auth.config;

import com.phi.auth.service.JwtService;
import com.phi.common.BizError;
import com.phi.common.BizException;
import com.phi.common.ResponseWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class AuthHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private final JwtService service;

    public AuthHandler(JwtService service) {
        this.service = service;
    }

    private String buildToken(Authentication authentication) {
        if (authentication.isAuthenticated()) {
            return service.encode(authentication);
        }
        throw BizException.badRequest(BizError.error("authentication is not isAuthenticated"));
    }

    public record SuccessResponse(String token) implements ResponseWriter {

        public static SuccessResponse of(String token) {
            return new SuccessResponse(token);
        }
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        String token = buildToken(authentication);
        SuccessResponse.of(token).write(response);
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        BizError error = BizError.error("认证失败");
        BizException.unauthorized(error, exception).write(response);
    }
}
