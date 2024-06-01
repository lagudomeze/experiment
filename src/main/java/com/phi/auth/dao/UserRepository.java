package com.phi.auth.dao;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

public interface UserRepository extends BaseMapper<User> {

    @InterceptorIgnore
    default boolean existsById(String id) {
        return exists(Wrappers
                .lambdaQuery(User.class)
                .eq(User::getId, id)
        );
    }
}