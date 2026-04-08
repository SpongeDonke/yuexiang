-- 确保所有店家都有店铺
USE wash_platform;

-- 查看当前所有店家
SELECT u.id, u.username, u.name, s.id as shop_id, s.name as shop_name
FROM user u
LEFT JOIN shop s ON u.id = s.user_id
WHERE u.role_id = 1;

-- 为没有店铺的店家创建默认店铺
INSERT INTO shop (user_id, name, address, phone, description)
SELECT u.id, CONCAT(u.name, '的洗护店'), '待填写地址', '待填写电话', '暂无描述'
FROM user u
LEFT JOIN shop s ON u.id = s.user_id
WHERE u.role_id = 1 AND s.id IS NULL;

-- 再次查看确认
SELECT u.id, u.username, u.name, s.id as shop_id, s.name as shop_name
FROM user u
JOIN shop s ON u.id = s.user_id
WHERE u.role_id = 1;
