-- 数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS wash_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE wash_platform;

-- 角色表
CREATE TABLE IF NOT EXISTS role (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    description VARCHAR(255) COMMENT '角色描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role_id INT NOT NULL COMMENT '角色ID',
    name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES role(id)
);

-- 权限表
CREATE TABLE IF NOT EXISTS permission (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '权限编码',
    description VARCHAR(255) COMMENT '权限描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 角色权限表
CREATE TABLE IF NOT EXISTS role_permission (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id),
    FOREIGN KEY (permission_id) REFERENCES permission(id)
);

-- 店铺表
CREATE TABLE IF NOT EXISTS shop (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL COMMENT '店家用户ID',
    name VARCHAR(100) NOT NULL COMMENT '店铺名称',
    address VARCHAR(255) COMMENT '店铺地址',
    phone VARCHAR(20) COMMENT '店铺电话',
    description TEXT COMMENT '店铺描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 洗护类型表
CREATE TABLE IF NOT EXISTS wash_type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '类型名称',
    description VARCHAR(255) COMMENT '类型描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 洗护服务表
CREATE TABLE IF NOT EXISTS wash_service (
    id INT PRIMARY KEY AUTO_INCREMENT,
    shop_id INT NOT NULL COMMENT '店铺ID',
    type_id INT NOT NULL COMMENT '洗护类型ID',
    name VARCHAR(100) NOT NULL COMMENT '服务名称',
    price DECIMAL(10,2) NOT NULL COMMENT '服务价格',
    description TEXT COMMENT '服务描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (shop_id) REFERENCES shop(id),
    FOREIGN KEY (type_id) REFERENCES wash_type(id)
);

-- 订单表
CREATE TABLE IF NOT EXISTS `order` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL COMMENT '顾客ID',
    shop_id INT NOT NULL COMMENT '店铺ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status VARCHAR(20) NOT NULL COMMENT '订单状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES user(id),
    FOREIGN KEY (shop_id) REFERENCES shop(id)
);

-- 订单详情表
CREATE TABLE IF NOT EXISTS order_detail (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL COMMENT '订单ID',
    service_id INT NOT NULL COMMENT '服务ID',
    quantity INT NOT NULL COMMENT '数量',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    FOREIGN KEY (order_id) REFERENCES `order`(id),
    FOREIGN KEY (service_id) REFERENCES wash_service(id)
);

-- 订单进度表
CREATE TABLE IF NOT EXISTS order_progress (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL COMMENT '订单ID',
    status VARCHAR(20) NOT NULL COMMENT '进度状态',
    description VARCHAR(255) COMMENT '进度描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES `order`(id)
);

-- 评论表
CREATE TABLE IF NOT EXISTS comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL COMMENT '订单ID',
    user_id INT NOT NULL COMMENT '评论用户ID',
    content TEXT NOT NULL COMMENT '评论内容',
    rating INT NOT NULL COMMENT '评分',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES `order`(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 支付凭证表
CREATE TABLE IF NOT EXISTS payment_evidence (
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL COMMENT '订单ID',
    user_id INT NOT NULL COMMENT '用户ID',
    file_path VARCHAR(255) NOT NULL COMMENT '凭证文件路径',
    file_name VARCHAR(100) NOT NULL COMMENT '凭证文件名称',
    file_size INT COMMENT '文件大小',
    file_type VARCHAR(50) COMMENT '文件类型',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES `order`(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 插入初始数据

-- 角色数据
INSERT INTO role (name, description) VALUES
('店家', '店铺经营者'),
('管理员', '系统管理员'),
('顾客', '普通用户');

-- 权限数据
INSERT INTO permission (name, code, description) VALUES
-- 店家权限
('个人中心', 'shop_personal', '店家个人中心'),
('店铺信息管理', 'shop_manage', '管理店铺信息'),
('洗护类型管理', 'wash_type_manage', '管理洗护类型'),
('洗护信息管理', 'wash_service_manage', '管理洗护服务'),
('订单信息管理', 'order_info_manage', '管理订单信息'),
('订单进度管理', 'order_progress_manage', '管理订单进度'),
('查看评论', 'comment_view', '查看用户评论'),

-- 管理员权限
('个人中心', 'admin_personal', '管理员个人中心'),
('顾客管理', 'customer_manage', '管理顾客信息'),
('店家管理', 'shopkeeper_manage', '管理店家信息'),
('店铺信息管理', 'admin_shop_manage', '管理所有店铺信息'),
('洗护信息管理', 'admin_wash_manage', '管理所有洗护服务'),
('订单进度管理', 'admin_order_progress', '管理所有订单进度'),
('系统管理', 'system_manage', '系统设置管理'),
('评论管理', 'admin_comment_manage', '管理所有评论'),

-- 顾客权限
('首页查看', 'customer_home', '查看首页'),
('个人中心', 'customer_personal', '顾客个人中心'),
('洗护信息管理', 'customer_wash_manage', '查看洗护服务'),
('订单信息管理', 'customer_order_info', '管理个人订单'),
('订单进度管理', 'customer_order_progress', '查看订单进度'),
('评论管理', 'customer_comment_manage', '管理个人评论');

-- 角色权限关联
-- 店家权限
INSERT INTO role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7);

-- 管理员权限
INSERT INTO role_permission (role_id, permission_id) VALUES
(2, 8), (2, 9), (2, 10), (2, 11), (2, 12), (2, 13), (2, 14), (2, 15);

-- 顾客权限
INSERT INTO role_permission (role_id, permission_id) VALUES
(3, 16), (3, 17), (3, 18), (3, 19), (3, 20), (3, 21);

-- 初始用户数据
-- 密码使用明文形式存储
INSERT INTO user (username, password, role_id, name, phone) VALUES
('zhangwei', '123456', 1, '张伟', '13811112222'),
('admin', '123456', 2, '系统管理员', '13800138000'),
('liming', '123456', 3, '李明', '13933334444'),
('wangfang', '123456', 3, '王芳', '13755556666'),
('zhaojun', '123456', 3, '赵军', '13677778888');

-- 店铺数据
INSERT INTO shop (user_id, name, address, phone, description) VALUES
(1, '洁净洗护店', '北京市朝阳区建国路88号', '010-65432100', '专业洗护服务，10年品质保证，使用进口洗涤剂'),
(1, '清新洗衣店', '北京市海淀区中关村大街1号', '010-62345678', '快捷洗衣，24小时取件，会员享8折优惠'),
(1, '奢护之家', '北京市西城区西单北大街120号', '010-66123456', '奢侈品专业护理，国际认证技师');

-- 洗护类型数据
INSERT INTO wash_type (name, description) VALUES
('衣物洗护', '各类衣物的清洗护理'),
('鞋类洗护', '各类鞋子的清洗护理'),
('家居用品', '家居用品的清洗护理'),
('奢侈品护理', '奢侈品的专业护理');

-- 洗护服务数据
INSERT INTO wash_service (shop_id, type_id, name, price, description) VALUES
-- 洁净洗护店服务
(1, 1, '普通衣物清洗', 25.00, '普通衣物的日常清洗，包含脱水、烘干'),
(1, 1, '高端衣物护理', 60.00, '高端衣物的专业护理，使用进口洗涤剂'),
(1, 1, '羽绒服清洗', 45.00, '羽绒服的专业清洗，保持保暖性'),
(1, 1, '羊毛衫护理', 35.00, '羊毛衫的专业护理，防止缩水变形'),
(1, 2, '普通鞋类清洗', 35.00, '普通鞋子的清洗，包含鞋底清洁'),
(1, 2, '运动鞋清洗', 40.00, '运动鞋的深度清洗，去除顽固污渍'),
(1, 2, '高端鞋类护理', 90.00, '高端鞋子的专业护理，保持皮革光泽'),
(1, 3, '床上用品清洗', 120.00, '床上用品的深度清洗，去除螨虫'),
(1, 3, '窗帘清洗', 150.00, '窗帘的专业清洗，包含安装服务'),
(1, 4, '奢侈品包包护理', 220.00, '奢侈品包包的专业护理，恢复原有光泽'),
(1, 4, '奢侈品鞋护理', 180.00, '奢侈品鞋子的专业护理，延长使用寿命'),

-- 清新洗衣店服务
(2, 1, '普通衣物清洗', 20.00, '普通衣物的日常清洗，经济实惠'),
(2, 1, '快速洗衣', 30.00, '2小时快速洗衣服务，紧急情况下适用'),
(2, 2, '普通鞋类清洗', 30.00, '普通鞋子的清洗服务'),
(2, 3, '床上用品清洗', 100.00, '床上用品的清洗服务'),

-- 奢护之家服务
(3, 4, '奢侈品包包护理', 280.00, '奢侈品包包的专业护理，国际认证技师'),
(3, 4, '奢侈品鞋护理', 220.00, '奢侈品鞋子的专业护理，使用进口护理产品'),
(3, 4, '奢侈品衣物护理', 180.00, '奢侈品衣物的专业护理，保持原有质感');

-- 订单数据
INSERT INTO `order` (customer_id, shop_id, total_amount, status) VALUES
(3, 1, 95.00, '已完成'),
(3, 1, 150.00, '处理中'),
(4, 2, 50.00, '已完成'),
(5, 3, 280.00, '已下单'),
(3, 2, 30.00, '已完成');

-- 订单详情数据
INSERT INTO order_detail (order_id, service_id, quantity, price) VALUES
(1, 1, 2, 25.00),
(1, 5, 1, 35.00),
(1, 8, 1, 120.00),
(2, 2, 1, 60.00),
(2, 7, 1, 90.00),
(3, 12, 1, 20.00),
(3, 13, 1, 30.00),
(4, 16, 1, 280.00),
(5, 13, 1, 30.00);

-- 订单进度数据
INSERT INTO order_progress (order_id, status, description) VALUES
(1, '已下单', '订单已创建，等待处理'),
(1, '处理中', '店铺正在处理，预计24小时完成'),
(1, '已完成', '订单已完成，可到店取件'),
(2, '已下单', '订单已创建，等待处理'),
(2, '处理中', '店铺正在处理，预计48小时完成'),
(3, '已下单', '订单已创建，等待处理'),
(3, '处理中', '店铺正在处理，预计12小时完成'),
(3, '已完成', '订单已完成，可到店取件'),
(4, '已下单', '订单已创建，等待处理'),
(5, '已下单', '订单已创建，等待处理'),
(5, '处理中', '店铺正在处理，预计6小时完成'),
(5, '已完成', '订单已完成，可到店取件');

-- 评论数据
INSERT INTO comment (order_id, user_id, content, rating) VALUES
(1, 3, '服务很好，洗得很干净，店员态度也很棒，下次还会来', 5),
(3, 4, '速度很快，2小时就取到了，衣服洗得很干净', 4),
(5, 3, '价格实惠，服务周到，值得推荐', 5);