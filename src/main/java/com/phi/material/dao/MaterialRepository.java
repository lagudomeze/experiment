package com.phi.material.dao;

import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;

public interface MaterialRepository extends CrudRepository<Material, String> {

    class NewMaterial extends Material implements Persistable<String> {

        @Override
        public boolean isNew() {
            return true;
        }
    }
}