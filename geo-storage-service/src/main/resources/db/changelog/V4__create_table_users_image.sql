ALTER TABLE users DROP COLUMN image_id CASCADE;

CREATE TABLE user_images
(
    user_id  UUID,
    image_id VARCHAR(255),
    PRIMARY KEY (user_id, image_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (image_id) REFERENCES image (id) ON DELETE CASCADE
);