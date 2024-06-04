package com.phi.material.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
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
        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024 * 8);
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
            if (buffer.hasRemaining()) {
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
        try {
            Files.walkFileTree(this.base.resolve(id.value()),
                    new SimpleFileVisitor<>() {
                        @Override
                        public FileVisitResult postVisitDirectory(
                                Path dir, IOException exc) throws IOException {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                                throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path save(Id id, String name, InputStream stream) {
        Path path = this.base.resolve(id.value()).resolve(name);
        try {
            Files.createDirectories(path);
            Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
            return path;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UriComponentsBuilder urlBuilder(Id id, String path) {
        return UriComponentsBuilder.fromUriString(id.value() + "/" + path);
    }
}
