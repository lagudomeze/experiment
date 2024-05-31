package com.phi.material.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.phi.material.service.VideoSlices;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/")
public class MaterialController {

    public record Page(@Schema(requiredMode = RequiredMode.REQUIRED)
                       long pageNo,
                       @Schema(requiredMode = RequiredMode.REQUIRED)
                       long pageSize) {

    }

    public record PageResult(
            @JsonUnwrapped
            Page page,
            @Schema(requiredMode = RequiredMode.REQUIRED)
            long totalPage,
            @Schema(requiredMode = RequiredMode.REQUIRED)
            long totalRecords) {

        @JsonCreator
        public PageResult(long pageNo, long pageSize, long totalPage, long totalRecords) {
            this(new Page(pageNo, pageSize), totalPage, totalRecords);
        }
    }

    public record SearchCondition(
            @Schema(description = "标签分组，不穿表示无限制")
            List<String> tags,
            @JsonUnwrapped
            Page page,
            @Schema(description = "描述的检索词, 暂时不做复杂分词检索，只做前后缀匹配. 不传表示无限制")
            String query
    ) {

        @JsonCreator
        public SearchCondition(long pageNo, long pageSize, List<String> tags, String query) {
            this(tags, new Page(pageNo, pageSize), query);
        }
    }

    public record SearchResponse(@JsonUnwrapped PageResult pageResult,
                                 @Schema(requiredMode = RequiredMode.REQUIRED)
                                 List<MaterialVo> records) {

    }

    @PostMapping("/materials:search")
    public SearchResponse search(@RequestBody SearchCondition condition) {
        throw new RuntimeException();
    }


    public record DetailResponse(
            @JsonUnwrapped
            MaterialVo vo,
            VideoSlices slices
    ) {

    }

    @GetMapping("/materials/{id}")
    public DetailResponse detail(@PathVariable String id) {
        throw new RuntimeException();
    }

    public record MaterialUploadEvent(String id, int progress) {

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
            @RequestPart String description,
            @RequestPart(required = false) List<String> tags) {
        throw new RuntimeException();
    }
}
