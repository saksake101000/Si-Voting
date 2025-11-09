-- SI-VOTING Database Schema
-- Database: sivotingdb

CREATE DATABASE IF NOT EXISTS sivotingdb;
USE sivotingdb;

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: events
CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(12) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_public BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_is_active (is_active),
    INDEX idx_dates (start_date, end_date),
    INDEX idx_created_by (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: candidates
CREATE TABLE IF NOT EXISTS candidates (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    photo_url VARCHAR(255),
    event_id BIGINT NOT NULL,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    INDEX idx_event_id (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: votes
CREATE TABLE IF NOT EXISTS votes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_event (user_id, event_id),
    INDEX idx_user_id (user_id),
    INDEX idx_candidate_id (candidate_id),
    INDEX idx_event_id (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Sample Data
-- Insert Admin User (password: admin123)
INSERT INTO users (username, email, password, role) VALUES
('admin', 'admin@sivoting.com', '$2a$10$xqWqN2zY5P5Z5Z5Z5Z5Z5eO5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z', 'ADMIN');

-- Insert Organizer User (password: organizer123)
INSERT INTO users (username, email, password, role) VALUES
('organizer', 'organizer@sivoting.com', '$2a$10$xqWqN2zY5P5Z5Z5Z5Z5Z5eO5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z', 'ORGANIZER');

-- Insert Voter User (password: voter123)
INSERT INTO users (username, email, password, role) VALUES
('voter1', 'voter1@sivoting.com', '$2a$10$xqWqN2zY5P5Z5Z5Z5Z5Z5eO5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z5Z', 'VOTER');

-- Insert Sample Event
INSERT INTO events (code, title, description, start_date, end_date, is_active, created_by) VALUES
('ABCD1234', 'Pemilihan Ketua OSIS 2025', 'Pemilihan ketua OSIS periode 2025-2026', '2025-01-01 08:00:00', '2025-12-31 17:00:00', TRUE, 1);

-- Insert Sample Candidates
INSERT INTO candidates (name, description, photo_url, event_id) VALUES
('Budi Santoso', 'Visi: Mewujudkan OSIS yang Kreatif dan Inovatif', 'https://via.placeholder.com/300x400?text=Budi', 1),
('Siti Nurhaliza', 'Visi: OSIS Peduli dan Berkarya untuk Siswa', 'https://via.placeholder.com/300x400?text=Siti', 1),
('Ahmad Fauzi', 'Visi: Membangun OSIS yang Solid dan Produktif', 'https://via.placeholder.com/300x400?text=Ahmad', 1);

-- Verification Queries
SELECT 'Users Created:' as Info, COUNT(*) as Count FROM users;
SELECT 'Events Created:' as Info, COUNT(*) as Count FROM events;
SELECT 'Candidates Created:' as Info, COUNT(*) as Count FROM candidates;
SELECT 'Votes Created:' as Info, COUNT(*) as Count FROM votes;
