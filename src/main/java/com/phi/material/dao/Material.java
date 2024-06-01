package com.phi.material.dao;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("materials")
public class Material {

    @TableId
    private String id;
    private String name;
    private String description;
    private String creator;
    private int state;

    private int type;

    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Instant createdAt = Instant.now();
}