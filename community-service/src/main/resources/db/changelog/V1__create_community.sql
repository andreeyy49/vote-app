CREATE TABLE community
(
    id           SERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT         NOT NULL,
    private_flag BOOLEAN      NOT NULL,
    admin        UUID         NOT NULL
);

CREATE TABLE community_moderators
(
    community_id BIGINT NOT NULL,
    moderator_id UUID   NOT NULL,
    FOREIGN KEY (community_id) REFERENCES community (id) ON DELETE CASCADE
);
