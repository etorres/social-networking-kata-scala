#!/usr/bin/env bash

set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER social_network WITH PASSWORD 'changeme';
    CREATE DATABASE social_network;
    GRANT ALL PRIVILEGES ON DATABASE social_network TO social_network;
    \connect social_network social_network
    DO \$\$
    DECLARE
      schema_names TEXT[] := ARRAY['public', 'test_subscriptions','test_timelines'];
      schema_name TEXT;
    BEGIN
      FOREACH schema_name IN ARRAY schema_names
      LOOP
        EXECUTE 'CREATE SCHEMA IF NOT EXISTS ' || quote_ident(schema_name);
        EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.subscriptions (
          subscriber VARCHAR(24) NOT NULL,
          subscription VARCHAR(24) NOT NULL,
          PRIMARY KEY (subscriber, subscription)
        )';
        EXECUTE 'CREATE TABLE IF NOT EXISTS ' || quote_ident(schema_name) || '.timeline_events (
          id SERIAL PRIMARY KEY,
          received_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
          sender VARCHAR(24) NOT NULL,
          addressee VARCHAR(24) NOT NULL,
          body VARCHAR(256) NOT NULL
        )';
      END LOOP;
    END\$\$;
EOSQL