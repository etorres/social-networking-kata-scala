CREATE TABLE IF NOT EXISTS timeline_events (
    id SERIAL PRIMARY KEY,
    received_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    sender VARCHAR(24) NOT NULL,
    addressee VARCHAR(24) NOT NULL,
    body VARCHAR(256) NOT NULL
);

CREATE TABLE IF NOT EXISTS subscriptions (
    subscriber VARCHAR(24) NOT NULL,
    subscription VARCHAR(24) NOT NULL,
    PRIMARY KEY (subscriber, subscription)
);