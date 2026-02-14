CREATE TABLE reservations (
    id          UUID            PRIMARY KEY,
    member_id   BIGINT          NOT NULL,
    resource_id BIGINT          NOT NULL,
    start_time  TIMESTAMP       NOT NULL,
    end_time    TIMESTAMP       NOT NULL,
    status      VARCHAR(20)     NOT NULL,
    version     BIGINT          NOT NULL DEFAULT 0
);

CREATE INDEX idx_reservations_resource_time
    ON reservations (resource_id, start_time, end_time);
