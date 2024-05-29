package com.phi.material.controller;

import com.fasterxml.jackson.databind.json.JsonMapper;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/materials")
@Slf4j
public class MaterialUploadrController {

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public record MaterialUploadEvent(String id, int progress) {

    }

    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(anyOf = MaterialUploadEvent.class))
    )
    @PostMapping(value = "/video",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter upload(@RequestPart MultipartFile file, @RequestPart String remark) {
        SseEmitter emitter = new SseEmitter();

        String id = UUID.randomUUID().toString();

        executor.submit(() -> {
            for (int i = 0; i < 25; i++) {
                MaterialUploadEvent object = new MaterialUploadEvent(id, 4 * i);
                try {
                    emitter.send(object, MediaType.APPLICATION_JSON);
                    TimeUnit.SECONDS.sleep(1);
                } catch (IOException e) {
                    log.warn("send sse failed", e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            emitter.completeWithError(new RuntimeException("hahaha"));
        });
        return emitter;
    }


}
