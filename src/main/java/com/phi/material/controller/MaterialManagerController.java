package com.phi.material.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/materials")
public class MaterialManagerController {

    public record MaterialUploadResult(String id) {

    }

    @PostMapping()
    public MaterialUploadResult upload() {
        throw new RuntimeException();
    }
}
