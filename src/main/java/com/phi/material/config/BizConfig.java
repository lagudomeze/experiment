package com.phi.material.config;

import com.phi.auth.service.AuthService;
import com.phi.material.dao.MaterialRepository;
import com.phi.material.dao.MaterialTagRepository;
import com.phi.material.service.MaterialService;
import com.phi.material.storage.Storage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BizConfig {

    @Bean
    public MaterialService materialService(MaterialRepository repo,
                                           MaterialTagRepository tagRepo,
                                           @Value("${phi.storage.choose}") String choose,
                                           ApplicationContext ctx,
                                           AuthService auth) {
        Storage storage = ctx.getBean(choose, Storage.class);
        return new MaterialService(repo, tagRepo, storage, auth);
    }
}
