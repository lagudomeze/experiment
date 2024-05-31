package com.phi.common;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class BizException extends RuntimeException implements ResponseWriter {

    private final HttpStatus status;

    private final BizError error;

    public BizException(HttpStatus status, BizError error) {
        this.status = status;
        this.error = error;
    }

    public BizException(HttpStatus status, BizError error, Throwable e) {
        super(e);
        this.status = status;
        this.error = error;
    }

    public static BizException badRequest(BizError error) {
        return new BizException(HttpStatus.BAD_REQUEST, error);
    }

    public static BizException notFound(BizError error) {
        return new BizException(HttpStatus.NOT_FOUND, error);
    }

    public static BizException unauthorized(BizError error, AuthenticationException exception) {
        return new BizException(HttpStatus.UNAUTHORIZED, error, exception);
    }

    public void write(HttpServletResponse response) throws IOException {
        write(response, this.status);
    }

}