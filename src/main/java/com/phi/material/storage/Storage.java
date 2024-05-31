package com.phi.material.storage;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.Resource;
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

    UriComponentsBuilder url(Id id, String path);
}
