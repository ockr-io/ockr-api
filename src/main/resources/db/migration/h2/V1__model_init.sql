DROP TABLE IF EXISTS model;

CREATE TABLE model (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    url VARCHAR(255) NOT NULL,
    port INT NOT NULL
);

CREATE INDEX idx_model_name ON model(name);