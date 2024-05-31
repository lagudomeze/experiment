package com.phi.material.service;

import com.phi.material.dao.MaterialRepository;
import com.phi.material.storage.Storage;

public class MaterialService {

    private final MaterialRepository repository;

    private final Storage storage;

    public MaterialService(MaterialRepository repository, Storage storage) {
        this.repository = repository;
        this.storage = storage;
    }
}
