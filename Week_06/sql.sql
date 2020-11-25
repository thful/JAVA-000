-- ----------------------------
-- 用户表
-- ----------------------------
CREATE DATABASE if not exists `store`;
USE `初始化`;

-- ----------------------------
-- 用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `name` varchar(16) NOT NULL COMMENT '名称',
  `pass` varchar(16) NOT NULL COMMENT '密码',
  `phone` varchar(15) NOT NULL COMMENT '手机号',
  `identify` varchar(16) NOT NULL COMMENT '身份证号',
  `money` int(11) NOT NULL COMMENT '账户余额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- 店铺表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `stores` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `name` varchar(16) NOT NULL COMMENT '名称',
    `description` varchar(1024) NOT NULL COMMENT '描述',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 商品表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `goods` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `name` varchar(16) NOT NULL  COMMENT '名称',
    `description` varchar(1024) NOT NULL  COMMENT '描述',
    `price` int(11) NOT NULL  COMMENT '价格',
    `weight` int(11) NOT NULL  COMMENT '重量',
    `store_id` int(11) NOT NULL  COMMENT '所属店铺id',
    `store_name` varchar(16) NOT NULL  COMMENT '所属店铺名称',
    `status` int(1) NOT NULL  COMMENT '商品状态',
    PRIMARY KEY (`id`),
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS `orders` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
    `user_id` int(11) NOT NULL COMMENT '用户id',
    `goods_id` varchar(10024) NOT NULL COMMENT '商品编号',
    `status` int(1) NOT NULL COMMENT '订单状态 已下单 已支付 已寄出 已验收 已评价',
    `total_price` int(11) NOT NULL COMMENT '总价',
    PRIMARY KEY (`id`),
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;