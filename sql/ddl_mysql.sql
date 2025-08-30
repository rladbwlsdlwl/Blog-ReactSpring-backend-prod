CREATE TABLE role_table (
    id BIGINT AUTO_INCREMENT,
    role VARCHAR(10) DEFAULT 'MEMBER',

    PRIMARY KEY (id),
    CONSTRAINT check_role CHECK (role IN ('MEMBER', 'ADMIN'))
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE member_table (
	id BIGINT AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL,
	password VARCHAR(255),
	role_id BIGINT NOT NULL,

	PRIMARY KEY (id),
	FOREIGN KEY (role_id) REFERENCES role_table(id) ON DELETE CASCADE,
	UNIQUE(name),
	UNIQUE(email)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE board_table (
    id BIGINT AUTO_INCREMENT,
    title VARCHAR(31) NOT NULL,
    contents LONGTEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    views BIGINT DEFAULT 0 NOT NULL,
    member_id BIGINT NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member_table(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE file_table (
    id BIGINT AUTO_INCREMENT,
    board_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    current_filename VARCHAR(255) NOT NULL,
--    data MEDIUMBLOB NOT NULL,

    UNIQUE(current_filename),
    PRIMARY KEY (id),
    FOREIGN KEY (board_id) REFERENCES board_table(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE likes_table (
    id BIGINT AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    board_id BIGINT NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES member_table(id) ON DELETE CASCADE,
    FOREIGN KEY (board_id) REFERENCES board_table(id) ON DELETE CASCADE,
    UNIQUE (member_id, board_id)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE comment_table (
    id BIGINT AUTO_INCREMENT,
    parent_id BIGINT DEFAULT NULL,
    member_id BIGINT NOT NULL,
    board_id BIGINT NOT NULL,
    comments VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (parent_id) REFERENCES comment_table(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES member_table(id) ON DELETE CASCADE,
    FOREIGN KEY (board_id) REFERENCES board_table(id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;