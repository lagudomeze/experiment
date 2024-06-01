package com.phi.material.service;


import com.phi.material.MaterialConstants;
import com.phi.material.controller.MaterialController;
import com.phi.material.dao.Material;
import com.phi.material.dao.MaterialRepository;
import com.phi.material.dao.MaterialTag;
import com.phi.material.dao.MaterialTagRepository;
import com.phi.material.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
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
        try {
            Storage.Id id = storage.digest(file.getInputStream());
            if (repository.existsById(id.value())) {
                log.warn("{} already exists", file.getName());
                emitter.send(MaterialController.MaterialUploadEvent.alreadyExisted(id.value()));
                emitter.complete();
                return;
            }

            // save raw file
            storage.save(id, "raw", file.getInputStream());

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

            if (tags != null) {
                for (String tag : tags) {
                    MaterialTag materialTag = new MaterialTag();
                    materialTag.setTag(tag);
                    materialTag.setMaterialId(id.value());
                    tagRepository.insert(new MaterialTag());
                }
            }
        } catch (IOException e) {
            log.warn("Failed to upload file", e);
            emitter.completeWithError(e);
        }
    }
}
