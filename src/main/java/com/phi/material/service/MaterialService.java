package com.phi.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.phi.auth.service.AuthService;
import com.phi.material.controller.MaterialController.DetailResponse;
import com.phi.material.controller.MaterialController.PageResult;
import com.phi.material.controller.MaterialController.SearchCondition;
import com.phi.material.controller.MaterialController.SearchResponse;
import com.phi.material.controller.MaterialVo;
import com.phi.material.dao.Material;
import com.phi.material.dao.MaterialRepository;
import com.phi.material.dao.MaterialTag;
import com.phi.material.dao.MaterialTagRepository;
import com.phi.material.storage.Storage;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MaterialService {

    private final MaterialRepository repository;

    private final MaterialTagRepository tagRepository;

    private final Storage storage;

    private final AuthService auth;

    public MaterialService(MaterialRepository repository,
                           MaterialTagRepository tagRepository,
                           Storage storage,
                           AuthService auth) {
        this.repository = repository;
        this.tagRepository = tagRepository;
        this.storage = storage;
        this.auth = auth;
    }

    private MaterialVo apply(Material material) {
        MaterialVo vo = new MaterialVo();

        Storage.Id id = new Storage.Id(material.getId());
        String raw = storage.url(id, "raw");
        String thumbnail = storage.url(id, "thumbnail");

        MaterialVideo video = new MaterialVideo(material.getId(), material.getName(), raw, thumbnail, material.getDescription());

        vo.setVideo(video);
        return vo;
    }

    public SearchResponse search(SearchCondition condition) {
        var wrapper = new MPJLambdaWrapper<>(Material.class);
        wrapper.eq(Material::getCreator, auth.userId());

        if (condition.tags() instanceof List<String> tags && !tags.isEmpty()) {
            wrapper.innerJoin(MaterialTag.class, MaterialTag::getMaterialId, Material::getId)
                    .in(MaterialTag::getTag, tags)
                    .distinct();
        }
        if (condition.query() instanceof String query && StringUtils.hasText(query)) {
            wrapper.like(Material::getDescription, "%" + query + "%");
        }

        Page<Material> result = repository.selectPage(condition.page().pageable(), wrapper);

        List<MaterialVo> materials = result.convert(this::apply).getRecords();
        PageResult pageResult = new PageResult(condition.page(), result.getTotal());
        return new SearchResponse(pageResult, materials);


    }

    public DetailResponse detail(String id) {
        return null;
    }

    private final ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();

    @SneakyThrows
    public void save(MultipartFile file, String description, List<String> tags, SseEmitter emitter) {
        String userId = auth.userId();
        service.submit(new VideoUploadTask(userId, file, description, tags, emitter,  storage, repository, tagRepository));


    }
}
