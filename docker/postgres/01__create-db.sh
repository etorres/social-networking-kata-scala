#!/usr/bin/env bash

set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER social_network;
    CREATE DATABASE social_network;
    GRANT ALL PRIVILEGES ON DATABASE social_network TO social_network;
    \c social_network
    CREATE TABLE messages (
      id SERIAL PRIMARY KEY,
      received_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
      sender VARCHAR(24) NOT NULL,
      addressee VARCHAR(24) NOT NULL,
      body VARCHAR(256) NOT NULL
    );
EOSQL