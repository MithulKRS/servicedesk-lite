-- ServiceDesk Lite — database schema
-- Run once to initialise the database.

CREATE DATABASE IF NOT EXISTS servicedesk;
USE servicedesk;

CREATE TABLE IF NOT EXISTS tickets (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    title       VARCHAR(150) NOT NULL,
    description TEXT,
    requester   VARCHAR(100) NOT NULL,
    priority    ENUM('LOW','MEDIUM','HIGH')              NOT NULL,
    status      ENUM('OPEN','IN_PROGRESS','RESOLVED')   NOT NULL DEFAULT 'OPEN',
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed data for quick demo/testing
INSERT INTO tickets (title, description, requester, priority, status) VALUES
  ('VPN not connecting',      'Cannot reach internal network from home.',  'alice@example.com', 'HIGH',   'OPEN'),
  ('Printer offline on 2F',   'Floor 2 shared printer shows offline.',     'bob@example.com',   'MEDIUM', 'IN_PROGRESS'),
  ('New laptop setup',        'Need dev tools installed on MacBook.',      'carol@example.com', 'LOW',    'OPEN'),
  ('Email password reset',    'Locked out after too many failed logins.',  'dave@example.com',  'HIGH',   'RESOLVED'),
  ('Slack slow on Windows',   'Takes 3+ minutes to load messages.',        'eve@example.com',   'MEDIUM', 'OPEN');
