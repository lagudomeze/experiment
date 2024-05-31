CREATE TABLE IF NOT EXISTS users
(
    id          VARCHAR(64) NOT NULL PRIMARY KEY,
    name        VARCHAR(255),
    source      VARCHAR(255),
    create_time INTEGER     NOT NULL DEFAULT (unixepoch() * 1000)
);

CREATE TABLE IF NOT EXISTS materials
(
    id          VARCHAR(36) NOT NULL PRIMARY KEY,
    name        VARCHAR(255),
    description VARCHAR(255),
    creator     VARCHAR(64) NOT NULL,
    state       int         NOT NULL,
    type        int         NOT NULL,
    create_time INTEGER     NOT NULL DEFAULT (unixepoch() * 1000)
);

CREATE TABLE material_tags
(
    material_id VARCHAR(36)  NOT NULL,
    tag         VARCHAR(255) NOT NULL,
    create_time INTEGER      NOT NULL DEFAULT (unixepoch() * 1000),
    CONSTRAINT material_tags_pk PRIMARY KEY (tag, material_id)
);

CREATE INDEX material_tags_material_id_index ON material_tags (material_id);

