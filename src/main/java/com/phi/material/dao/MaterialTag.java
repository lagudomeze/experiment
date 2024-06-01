package com.phi.material.dao;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

@Data
@TableName("material_tags")
public class MaterialTag {

    private String materialId;
    private String tag;


    @TableField(updateStrategy = FieldStrategy.NEVER)
    private Instant createdAt = Instant.now();
}