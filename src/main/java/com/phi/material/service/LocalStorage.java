package com.phi.material.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service("local")
public class LocalStorage implements Storage {

    private final Path base;

    private static final ThreadLocal<MessageDigest> DIGEST_SHA3_256 = ThreadLocal.withInitial(
            () -> {
                try {
                    return MessageDigest.getInstance("SHA3-256");
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException("SHA3-256 algorithm not found", e);
                }
            }
    );

    public LocalStorage(@Value("${phi.storage.local.base-dir}") Path base) {
        this.base = base;
    }

    @Override
    public Id digest(InputStream in) {
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 16);
        MessageDigest digest = DIGEST_SHA3_256.get();
        digest.reset();

        try (ReadableByteChannel channel = Channels.newChannel(in)) {
            while (channel.read(buffer) != -1) {
                if (!buffer.hasRemaining()) {
                    buffer.flip();
                    digest.update(buffer);
                    buffer.compact();
                }
            }
            buffer.flip();
            if (!buffer.hasRemaining()) {
                digest.update(buffer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Id(HexFormat.of().formatHex(digest.digest()));
    }

    @Override
    public boolean exists(Id id) {
        return Files.exists(this.base.resolve(id.value()));
    }

    @Override
    public void delete(Id id) {
        try (Stream<Path> walk = Files.walk(this.base.resolve(id.value()))) {
            Iterator<Path> iterator = walk.iterator();
            while (iterator.hasNext()) {
                Files.deleteIfExists(iterator.next());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(Id id, String name, InputStream stream) {
        Path path = this.base.resolve(id.value()).resolve(name);
        try {
            Files.createDirectories(path);
            Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UriComponentsBuilder url(Id id, String path) {
        return UriComponentsBuilder.fromUriString("{baseUrl}/storage/" + id.value() + "/" + path);
    }
}
