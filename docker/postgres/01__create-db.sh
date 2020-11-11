#!/usr/bin/env bash

set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER social_network;
    CREATE DATABASE social_network;
    GRANT ALL PRIVILEGES ON DATABASE social_network TO social_network;
EOSQL