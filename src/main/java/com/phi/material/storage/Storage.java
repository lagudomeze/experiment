package com.phi.material.storage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public interface Storage {

    default Id digest(Resource resource) {
        try (InputStream in = resource.getInputStream()) {
            return digest(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Id digest(InputStream in);

    record Id(String value) {

    }

    boolean exists(Id id);

    void delete(Id id);

    default void save(Id id, String path, Resource resource) {
        try (InputStream in = resource.getInputStream()) {
            save(id, path, in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void save(Id id, String path, InputStream resource);

    UriComponentsBuilder urlBuilder(Id id, String path);

    default String url(Id id, String path) {
        return urlBuilder(id, path).build(Map.of("baseUrl", baseUrl())).toString();
    }

    static String baseUrl() {
        if (RequestContextHolder.currentRequestAttributes() instanceof HttpServletRequest request) {
            UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(UrlUtils.buildFullRequestUrl(request))
                    .replacePath(request.getContextPath())
                    .replaceQuery(null)
                    .fragment(null)
                    .build();
            return uriComponents.toUriString();
        } else {
            return "";
        }
    }
}
