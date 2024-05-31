package com.phi.auth.dao;

import org.springframework.data.domain.Persistable;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {

    class NewUser extends User implements Persistable<String> {

        @Override
        public boolean isNew() {
            return true;
        }
    }
}