-- This script creates drops the current version of the database.
-- It must be run as the root user of the database.
-- In the examples the name of the root user is 'root'
-- and the root password is 'concordia'.

DROP DATABASE IF EXISTS AQUARIUM;
CREATE DATABASE AQUARIUM;

USE AQUARIUM;

DROP USER IF EXISTS fish@'localhost';
DROP USER IF EXISTS fish@'%';
-- Only allow fish if running on localhost
CREATE USER fish@'localhost' IDENTIFIED BY 'kfstandard';
GRANT ALL ON aquarium.* TO fish@'localhost';
-- Allow fish from any IP address except localhost
CREATE USER fish@'%' IDENTIFIED BY 'kfstandard';
GRANT ALL ON aquarium.* TO fish@'%';

FLUSH PRIVILEGES;
