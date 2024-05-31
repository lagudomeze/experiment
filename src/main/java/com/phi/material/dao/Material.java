package com.phi.material.dao;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.InsertOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("materials")
public class Material {

    @Id
    private String id;
    private String name;
    private String description;
    @CreatedBy
    private String creator;
    private int state;

    private int type;

    @CreatedDate
    @InsertOnlyProperty
    private Instant createdAt = Instant.now();
}