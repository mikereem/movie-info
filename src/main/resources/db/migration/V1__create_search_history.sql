CREATE TABLE search_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_title VARCHAR(255) NOT NULL,
    api_name VARCHAR(50) NOT NULL,
    page INT NOT NULL,
    request_time TIMESTAMP NOT NULL
);