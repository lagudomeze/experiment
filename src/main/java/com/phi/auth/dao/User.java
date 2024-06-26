package com.phi.auth.dao;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("users")
public class User {

    @TableId
    private String id;
    private String name;
    private String source;

    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Instant createdAt = Instant.now();
}