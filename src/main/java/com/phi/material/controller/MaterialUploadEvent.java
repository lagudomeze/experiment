package com.phi.material.controller;

import io.swagger.v3.oas.annotations.media.Schema;

public record MaterialUploadEvent(
        @Schema(description = "物料的id")
        String id,
        @Schema(description = "state=wip(0-99), state=ok(100), 其他异常(-1) ")
        int progress,
        @Schema(description = "wip-处理中 ok-完成 其他是异常")
        String state) {

    public static MaterialUploadEvent alreadyExisted(String id) {
        return new MaterialUploadEvent(id, -1, "already_existed");
    }

    public static MaterialUploadEvent wip(String id, int progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("progress must be between 0 and 100");
        }
        return new MaterialUploadEvent(id, progress, "wip");
    }

    public static MaterialUploadEvent ok(String id) {
        return new MaterialUploadEvent(id, 100, "ok");
    }
}
