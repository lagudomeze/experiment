package com.phi.material.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.base.MPJBaseMapper;

public interface MaterialRepository extends MPJBaseMapper<Material> {

    @InterceptorIgnore
    default boolean existsById(String id) {
        return exists(Wrappers
                .lambdaQuery(Material.class)
                .eq(Material::getId, id)
        );
    }
}