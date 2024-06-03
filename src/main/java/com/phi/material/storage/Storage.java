package com.phi.material.storage;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@SuppressWarnings("unused")
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

    Path save(Id id, String path, InputStream resource);

    UriComponentsBuilder urlBuilder(Id id, String path);

    default String url(Id id, String path) {
        return baseUrl().resolve(urlBuilder(id, path).build().toUri()).toString();
    }

    static URI baseUrl() {
        if (RequestContextHolder.currentRequestAttributes() instanceof ServletRequestAttributes attributes
            && attributes.getRequest() instanceof HttpServletRequest request) {
            return UriComponentsBuilder.fromHttpUrl(
                            UrlUtils.buildFullRequestUrl(request))
                    .replacePath(request.getContextPath())
                    .replaceQuery(null)
                    .fragment(null)
                    .build()
                    .toUri()
                    .resolve("storage/");
        } else {
            return URI.create("");
        }
    }
}
