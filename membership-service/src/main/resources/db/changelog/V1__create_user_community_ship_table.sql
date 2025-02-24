CREATE TABLE user_community_ship
(
    id           SERIAL PRIMARY KEY,
    community_id SERIAL NOT NULL,
    user_id      UUID   NOT NULL
);