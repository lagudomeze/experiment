package com.phi.common;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;

public interface ResponseWriter {

    default void write(HttpServletResponse response) throws IOException {
        write(response, HttpStatus.OK);
    }

    default void write(HttpServletResponse response, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write(JsonUtil.toJsonString(this));
    }
}
