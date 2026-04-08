-- 添加预约时间字段到订单表
ALTER TABLE `order` ADD COLUMN `appointment_time` DATETIME NULL DEFAULT NULL;

-- 为已有订单设置默认预约时间为下单后24小时
UPDATE `order` SET `appointment_time` = DATE_ADD(`created_at`, INTERVAL 1 DAY) WHERE `appointment_time` IS NULL;
