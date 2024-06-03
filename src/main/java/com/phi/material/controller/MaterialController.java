package com.phi.material.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.phi.common.config.StringListConverter;
import com.phi.material.service.MaterialService;
import com.phi.material.service.VideoSlices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class MaterialController {

    private final MaterialService service;

    public MaterialController(MaterialService service) {
        this.service = service;
    }

    public record PageVo(@Schema(requiredMode = RequiredMode.REQUIRED)
                         int pageNo,
                         @Schema(requiredMode = RequiredMode.REQUIRED)
                         int pageSize) {

        public <T> Page<T> pageable() {
            return PageDTO.of(pageNo, pageSize);
        }
    }

    public record PageResult(
            @JsonUnwrapped
            PageVo page,
            @Schema(requiredMode = RequiredMode.REQUIRED)
            long totalRecords) {

        @JsonCreator
        public PageResult(int pageNo, int pageSize, long totalRecords) {
            this(new PageVo(pageNo, pageSize), totalRecords);
        }
    }

    public record SearchCondition(
            @Schema(description = "标签分组，不穿表示无限制")
            List<String> tags,
            @JsonUnwrapped
            PageVo page,
            @Schema(description = "描述的检索词, 暂时不做复杂分词检索，只做前后缀匹配. 不传表示无限制")
            String query
    ) {

        @JsonCreator
        public SearchCondition(
                @Schema(defaultValue = "0")
                int pageNo,
                @Schema(description = "10")
                int pageSize,
                List<String> tags,
                String query) {
            this(tags, new PageVo(pageNo, pageSize), query);
        }
    }

    public record SearchResponse(@JsonUnwrapped PageResult pageResult,
                                 @Schema(requiredMode = RequiredMode.REQUIRED)
                                 List<MaterialVo> records) {

    }

    @PostMapping("/materials:search")
    public SearchResponse search(@RequestBody SearchCondition condition) {
        return service.search(condition);
    }


    public record DetailResponse(
            @JsonUnwrapped
            MaterialVo vo,
            @JsonUnwrapped
            VideoSlices slices
    ) {

    }

    @GetMapping("/materials/{id}")
    public DetailResponse detail(@PathVariable String id) {
        return service.detail(id);
    }

    @Operation(description = "视频上传接口")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(anyOf = MaterialUploadEvent.class)))
    @PostMapping(value = "/materials/video",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter upload(
            @RequestPart MultipartFile file,
            @RequestPart String description,
            @RequestPart(required = false) StringListConverter.StringList tags) {
        SseEmitter emitter = new SseEmitter();
        service.save(file, description, tags, emitter);
        return emitter;
    }
}
