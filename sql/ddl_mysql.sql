CREATE TABLE MEMBER_TABLE (
	id BIGINT AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	email VARCHAR(255) NOT NULL,
	password VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE BOARD_TABLE (
    id BIGINT AUTO_INCREMENT,
    title VARCHAR(31) NOT NULL,
    contents LONGTEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    views BIGINT DEFAULT 0 NOT NULL,
    member_id BIGINT,

    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES MEMBER_TABLE(id) ON DELETE CASCADE
);

CREATE TABLE ROLE_TABLE (
    id BIGINT AUTO_INCREMENT,
    member_id BIGINT,
    role VARCHAR(10) DEFAULT 'MEMBER',

    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES MEMBER_TABLE(id) ON DELETE CASCADE,
    CONSTRAINT check_role CHECK (role IN ('MEMBER', 'ADMIN'))
);

CREATE TABLE FILE_TABLE (
    id BIGINT AUTO_INCREMENT,
    board_id BIGINT,
    originalFilename VARCHAR(255) NOT NULL,
    currentFilename VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (board_id) REFERENCES BOARD_TABLE(id) ON DELETE CASCADE
);

CREATE TABLE LIKES_TABLE (
    id BIGINT AUTO_INCREMENT,
    member_id BIGINT,
    board_id BIGINT,

    PRIMARY KEY (id),
    FOREIGN KEY (member_id) REFERENCES MEMBER_TABLE(id) ON DELETE CASCADE,
    FOREIGN KEY (board_id) REFERENCES BOARD_TABLE(id) ON DELETE CASCADE,
    UNIQUE (member_id, board_id)
);

CREATE TABLE COMMENT_TABLE (
    id BIGINT AUTO_INCREMENT,
    parent_id BIGINT DEFAULT NULL,
    member_id BIGINT,
    board_id BIGINT,
    comments VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (parent_id) REFERENCES COMMENT_TABLE(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES MEMBER_TABLE(id) ON DELETE CASCADE,
    FOREIGN KEY (board_id) REFERENCES BOARD_TABLE(id) ON DELETE CASCADE
);