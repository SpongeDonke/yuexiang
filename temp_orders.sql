USE wash_platform;

-- 插入订单信息
INSERT INTO `order` (customer_id, shop_id, total_amount, status, contact_name, contact_phone, address, remark, created_at, updated_at)
VALUES 
(3, 1, 70.00, 'pending', '张三', '13812345678', '北京市朝阳区建国路88号', '需要加急处理', NOW(), NOW()),
(4, 2, 50.00, 'washing', '李四', '13987654321', '上海市浦东新区张江高科技园区', '普通处理', NOW(), NOW()),
(5, 3, 280.00, 'ready', '王五', '13765432198', '广州市天河区珠江新城', '奢侈品护理', NOW(), NOW());

-- 插入订单进度
INSERT INTO order_progress (order_id, status, description, created_at)
VALUES
(1, 'pending', '订单已创建', NOW()),
(1, 'pending', '等待处理', NOW()),
(2, 'washing', '订单已创建', NOW()),
(2, 'washing', '正在洗涤', NOW()),
(3, 'ready', '订单已创建', NOW()),
(3, 'ready', '等待取件', NOW());

-- 插入支付凭证
INSERT INTO payment_evidence (order_id, user_id, file_path, file_name, file_size, file_type, created_at)
VALUES
(1, 3, 'uploads/payment-evidence/123456.jpg', 'payment1.jpg', 102400, 'image/jpeg', NOW()),
(2, 4, 'uploads/payment-evidence/789012.jpg', 'payment2.jpg', 153600, 'image/jpeg', NOW()),
(3, 5, 'uploads/payment-evidence/345678.jpg', 'payment3.jpg', 204800, 'image/jpeg', NOW());