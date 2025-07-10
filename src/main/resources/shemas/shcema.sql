-- The main dev scheme edited as we go

-- Creating the task table
CREATE TABLE task (
    id SERIAL PRIMARY KEY NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    completed BOOLEAN NOT NULL
);

-- Creating the user table
CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- Setting the task ownership by adding user_id as foreign key
ALTER TABLE task
    Add COLUMN user_id INTEGER NOT NULL,
    ADD CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES app_user(id)
        ON DELETE CASCADE;

-- Audit fields
ALTER TABLE task
    ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    ADD COLUMN updated_at TIMESTAMP NOT NULL DEFAULT NOW();

-- For soft delete
ALTER TABLE task
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;