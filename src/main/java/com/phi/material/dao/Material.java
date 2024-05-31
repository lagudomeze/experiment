package com.phi.material.dao;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("materials")
public class Material {

    @Id
    private String id;
    private String name;
    private String description;
    private String creator;
    private int state;
    private int type;

    // 下面两个字段由mysql/mariadb自动管理
    @ReadOnlyProperty
    private Instant createdAt;
}