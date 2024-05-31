package com.phi.auth.dao;

import java.time.Instant;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("users")
public class User {

    @Id
    private String id;
    private String name;
    private String source;

    // 下面两个字段由mysql/mariadb自动管理
    @ReadOnlyProperty
    private Instant createTime;
}