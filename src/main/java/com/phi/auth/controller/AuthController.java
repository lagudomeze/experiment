package com.phi.auth.controller;

import com.phi.auth.dao.User;
import com.phi.auth.dao.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manager")
@Slf4j
public class AuthController {

    private final UserRepository repository;

    public AuthController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/users")
    public List<User> allUsers() {
        return repository.selectList(null);
    }

}
