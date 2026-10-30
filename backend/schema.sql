-- MotoBuddy Production MySQL Database Schema
CREATE DATABASE IF NOT EXISTS motobuddy;
USE motobuddy;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    token VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Motorcycles / Garage Table
CREATE TABLE IF NOT EXISTS motorcycles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    year INT NOT NULL,
    odometer_km INT NOT NULL,
    owner_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Trips Table
CREATE TABLE IF NOT EXISTS trips (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    start_location VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    distance_km DOUBLE NOT NULL,
    est_duration_hours DOUBLE NOT NULL,
    fuel_cost_est DOUBLE NOT NULL,
    date VARCHAR(50) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Maintenance Records Table
CREATE TABLE IF NOT EXISTS maintenance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    date VARCHAR(50) NOT NULL,
    odometer_km INT NOT NULL,
    notes TEXT,
    is_completed TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Community Group Rides Table
CREATE TABLE IF NOT EXISTS group_rides (
    id INT AUTO_INCREMENT PRIMARY KEY,
    host_user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    route_description VARCHAR(255) NOT NULL,
    meetup_location VARCHAR(150) NOT NULL,
    meetup_time VARCHAR(50) NOT NULL,
    max_riders INT DEFAULT 8,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (host_user_id) REFERENCES users(id) ON DELETE CASCADE
);
