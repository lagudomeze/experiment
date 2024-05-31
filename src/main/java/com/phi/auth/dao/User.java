package com.phi.auth.dao;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("users")
public class User {

    @Id
    private String id;
    private String name;
    private String source;

    @CreatedDate
    @InsertOnlyProperty
    private Instant createdAt = Instant.now();
}