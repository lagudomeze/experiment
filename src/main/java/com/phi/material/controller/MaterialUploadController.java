package com.phi.material.controller;

import com.phi.material.service.MaterialService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Controller
@RequestMapping("/api/v1/")
public class MaterialUploadController {

    private final MaterialService service;

    public MaterialUploadController(MaterialService service) {
        this.service = service;
    }

    public record MaterialUploadEvent(String id, int progress, String state) {

        public static MaterialUploadEvent alreadyExisted(String id) {
            return new MaterialUploadEvent(id, -1, "already_existed");
        }

        public static MaterialUploadEvent wip(String id, int progress) {
            return new MaterialUploadEvent(id, progress, "wip");
        }
    }


    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(anyOf = MaterialUploadEvent.class))
    )
    @PostMapping(value = "/materials/video",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter upload(
            @RequestPart MultipartFile file,
            @RequestParam String description,
            @RequestParam(required = false) List<String> tags) {
        SseEmitter emitter = new SseEmitter();
        service.save(file, description, tags, emitter);
        return emitter;
    }
}
