CREATE TABLE IF NOT EXISTS recommendations (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    lesson_id  BIGINT NOT NULL,
    score      DOUBLE PRECISION,
    reason     TEXT,
    created_at TIMESTAMP
);
