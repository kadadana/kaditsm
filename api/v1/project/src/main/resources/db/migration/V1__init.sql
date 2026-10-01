CREATE TABLE projects (
    id           UUID PRIMARY KEY,
    project_key  VARCHAR(10)  NOT NULL UNIQUE,
    name         VARCHAR(100) NOT NULL,
    description  TEXT,
    owner_id     UUID         NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE project_members (
    id          UUID PRIMARY KEY,
    project_id  UUID        NOT NULL REFERENCES projects(id),
    user_id     UUID        NOT NULL,
    role        VARCHAR(20) NOT NULL,
    UNIQUE (project_id, user_id)
);

CREATE INDEX idx_members_user ON project_members(user_id);