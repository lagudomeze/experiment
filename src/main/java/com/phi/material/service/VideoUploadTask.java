package com.phi.material.service;

import com.phi.material.MaterialConstants;
import com.phi.material.controller.MaterialUploadController.MaterialUploadEvent;
import com.phi.material.dao.Material;
import com.phi.material.dao.MaterialRepository;
import com.phi.material.dao.MaterialTag;
import com.phi.material.dao.MaterialTagRepository;
import com.phi.material.ffmpeg.FfmpegService;
import com.phi.material.storage.Storage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
public record VideoUploadTask(String userId,
                              MultipartFile file,
                              String description,
                              List<String> tags,
                              SseEmitter emitter,
                              Storage storage,
                              MaterialRepository repository,
                              MaterialTagRepository tagRepository,
                              FfmpegService ffmpegService
) implements Runnable {


    @Override
    public void run() {
        Storage.Id id = null;
        try {
            log.info("start digest: {}", file.getOriginalFilename());
            id = storage.digest(file.getInputStream());
            log.info("end digest: {} with id {}", file.getOriginalFilename(), id.value());
            if (repository.existsById(id.value())) {
                log.warn("{} already exists", file.getName());
                emitter.send(MaterialUploadEvent.alreadyExisted(id.value()));
                emitter.complete();
                return;
            }
            emitter.send(MaterialUploadEvent.wip(id.value(), 2));

            // save raw file
            Path raw = storage.save(id, "raw", file.getInputStream());
            log.info("save raw: {} with id {}", file.getOriginalFilename(), id.value());
            emitter.send(MaterialUploadEvent.wip(id.value(), 15));

            ffmpegService.thumbnail(raw, raw.getParent().resolve("thumbnail.jpeg"));
            log.info("save thumbnail: {} with id {}", file.getOriginalFilename(), id.value());
            emitter.send(MaterialUploadEvent.wip(id.value(), 25));

            String value = id.value();
            int[] progress = new int[]{25};
            ffmpegService.slice(raw, raw.getParent(), string -> {
                log.debug(string);
                try {
                    log.info("id {} progress {}", value, progress[0]);
                    emitter.send(MaterialUploadEvent.wip(value, progress[0]));
                    if (progress[0] < 75) {
                        progress[0]++;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            log.info("save slice: {} with id {}", file.getOriginalFilename(), id.value());
            emitter.send(MaterialUploadEvent.wip(id.value(), 75));
            log.info("save to db: {} with id {} start", file.getOriginalFilename(), id.value());

            Material entity = new Material();
            entity.setId(id.value());
            entity.setName(file.getName());
            entity.setDescription(description);
            entity.setType(MaterialConstants.TYPE_VIDEO);
            entity.setState(0);
            entity.setCreator(userId);
            repository.insert(entity);
            emitter.send(MaterialUploadEvent.wip(id.value(), 80));

            if (tags != null) {
                for (String tag : tags) {
                    MaterialTag materialTag = new MaterialTag();
                    materialTag.setTag(tag);
                    materialTag.setMaterialId(id.value());
                    tagRepository.insert(materialTag);
                }
            }
            emitter.send(MaterialUploadEvent.wip(id.value(), 100));
            log.info("save to db: {} with id {} end", file.getOriginalFilename(), id.value());
            emitter.complete();
        } catch (Exception e) {
            log.warn("Failed to upload file", e);
            if (id != null) {
                storage.delete(id);
            }
            emitter.completeWithError(e);
        } finally {
            log.info("haha");
        }
    }
}
