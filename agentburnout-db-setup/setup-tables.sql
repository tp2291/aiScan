CREATE SCHEMA IF NOT EXISTS ab;

CREATE TABLE IF NOT EXISTS ab.burnout
(
    interaction_id        VARCHAR(255) PRIMARY KEY UNIQUE NOT NULL,
    interaction_date_time TIMESTAMP WITH TIME ZONE        NOT NULL,
    agent_id              VARCHAR(255)                    NOT NULL,
    org_id                VARCHAR(255)                    NOT NULL,
    agent_session_id      VARCHAR(255)                    NOT NULL,
    burnout_index         FLOAT(4)                        NOT NULL,
    action_taken          BOOLEAN                         NOT NULL,
    action_date_time      TIMESTAMP WITH TIME ZONE,
    action_type           VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ab.automated_breaks
(
    agent_id                VARCHAR(255) PRIMARY KEY NOT NULL,
    org_id                  VARCHAR(255)             NOT NULL,
    automated_breaks_status BOOLEAN                  NOT NULL
);

CREATE TABLE IF NOT EXISTS ab.subscription_info
(
    agent_id        VARCHAR(255) PRIMARY KEY NOT NULL,
    org_id          VARCHAR(255)             NOT NULL,
    subscription_id VARCHAR(255)             NOT NULL
);

CREATE TABLE IF NOT EXISTS ab.model
(
    agent_id               VARCHAR(255) PRIMARY KEY NOT NULL,
    org_id                 VARCHAR(255)             NOT NULL,
    last_trained_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    model                  BYTEA                    NOT NULL
);

ALTER TABLE ab.model ALTER COLUMN last_trained_date_time DROP NOT NULL;

ALTER TABLE ab.model ALTER COLUMN model DROP NOT NULL;

-- Status column values : Needs_training = 0, Insufficient_data = 1, Trained = 2
ALTER TABLE ab.model ADD COLUMN IF NOT EXISTS status SMALLINT;

CREATE TABLE IF NOT EXISTS ab.config
(
    org_id              VARCHAR(255) PRIMARY KEY UNIQUE NOT NULL,
    agent_ids           text[],
    team_ids            text[],
	idle_codes          text[]
);

CREATE TABLE IF NOT EXISTS ab.actions
(
    id                     SERIAL                       PRIMARY KEY,
    interaction_id         VARCHAR(255)                    NOT NULL,
	agent_id               VARCHAR(255)                    NOT NULL,
    org_id                 VARCHAR(255)                    NOT NULL,
    client_id              VARCHAR(255)                    NOT NULL,
	action_type            VARCHAR(255),
	action_date_time       BIGINT,
    created_date_time      BIGINT
);


SELECT table_name, column_name, data_type, character_maximum_length, is_nullable
FROM information_schema.columns
WHERE table_schema = 'ab';
