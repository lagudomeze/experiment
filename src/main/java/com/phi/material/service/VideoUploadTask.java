package com.phi.material.service;


import com.phi.material.MaterialConstants;
import com.phi.material.controller.MaterialUploadController.MaterialUploadEvent;
import com.phi.material.dao.Material;
import com.phi.material.dao.MaterialRepository;
import com.phi.material.dao.MaterialTag;
import com.phi.material.dao.MaterialTagRepository;
import com.phi.material.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
public record VideoUploadTask(String userId,
                              MultipartFile file,
                              String description,
                              List<String> tags,
                              SseEmitter emitter,
                              Storage storage,
                              MaterialRepository repository,
                              MaterialTagRepository tagRepository
) implements Runnable {


    @Override
    public void run() {
        Storage.Id id = null;
        try {
            id = storage.digest(file.getInputStream());
            if (repository.existsById(id.value())) {
                log.warn("{} already exists", file.getName());
                emitter.send(MaterialUploadEvent.alreadyExisted(id.value()));
                emitter.complete();
                return;
            }
            emitter.send(MaterialUploadEvent.wip(id.value(), 2));

            // save raw file
            storage.save(id, "raw", file.getInputStream());
            emitter.send(MaterialUploadEvent.wip(id.value(), 15));

            // todo save thumbnail
            // storage.save(id, "thumbnail", file.getInputStream());

            // todo split file and save slice
            // storage.save(id, "thumbnail", file.getInputStream());

            Material entity = new Material();
            entity.setId(id.value());
            entity.setName(file.getName());
            entity.setDescription(description);
            entity.setType(MaterialConstants.TYPE_VIDEO);
            entity.setState(0);
            entity.setCreator(userId);
            repository.insert(entity);
            emitter.send(MaterialUploadEvent.wip(id.value(), 50));

            if (tags != null) {
                for (String tag : tags) {
                    MaterialTag materialTag = new MaterialTag();
                    materialTag.setTag(tag);
                    materialTag.setMaterialId(id.value());
                    tagRepository.insert(materialTag);
                }
            }
            emitter.send(MaterialUploadEvent.wip(id.value(), 100));
            emitter.complete();
        } catch (Exception e) {
            log.warn("Failed to upload file", e);
            if (id != null ) {
                storage.delete(id);
            }
            emitter.completeWithError(e);
        }
    }
}
