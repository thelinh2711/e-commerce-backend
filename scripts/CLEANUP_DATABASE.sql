-- =====================================================
-- XÓA HOÀN TOÀN DATABASE CŨ
-- Chạy script này TRƯỚC KHI chạy databaseStruct.sql
-- =====================================================

DROP DATABASE IF EXISTS shopbackend_db;

CREATE DATABASE shopbackend_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

SELECT 'Database cleaned successfully! Now run databaseStruct.sql' AS status;
