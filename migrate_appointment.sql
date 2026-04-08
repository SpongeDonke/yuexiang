ALTER TABLE `order` ADD COLUMN appointment_time DATETIME NULL DEFAULT NULL;
UPDATE `order` SET appointment_time = DATE_ADD(created_at, INTERVAL 1 DAY) WHERE appointment_time IS NULL;
