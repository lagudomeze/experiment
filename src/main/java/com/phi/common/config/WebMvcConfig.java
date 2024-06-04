package com.phi.common.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        messageConverters.add(new StringListConverter());
    }

    @Value("${phi.ui.path-pattern}")
    private String uiPaths;

    @Value("${phi.ui.base-dir}")
    private String uiBaseDir;

    @Value("${phi.storage.local.path-pattern}")
    private String storagePaths;

    @Value("${phi.storage.local.base-dir}")
    private String storageBaseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(uiPaths)
                .addResourceLocations("file:" + uiBaseDir);
        registry.addResourceHandler(storagePaths)
                .addResourceLocations("file:" + storageBaseDir);
    }
}
