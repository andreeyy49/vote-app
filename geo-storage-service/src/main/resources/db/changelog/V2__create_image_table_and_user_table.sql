CREATE TABLE image
(
    id   VARCHAR(255) PRIMARY KEY,
    hash VARCHAR(255)
);

CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    image_id VARCHAR(255),
    FOREIGN KEY (image_id) REFERENCES image(id) ON DELETE CASCADE
);