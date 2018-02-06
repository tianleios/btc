/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50717
 Source Host           : localhost
 Source Database       : btc

 Target Server Type    : MySQL
 Target Server Version : 50717
 File Encoding         : utf-8

 Date: 02/06/2018 20:35:13 PM
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `btc_address`
-- ----------------------------
DROP TABLE IF EXISTS `btc_address`;
CREATE TABLE `btc_address` (
  `address` char(36) NOT NULL COMMENT '地址',
  `privatekey` varchar(64) NOT NULL,
  `type` varchar(4) NOT NULL,
  PRIMARY KEY (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `btc_default_utxo`
-- ----------------------------
DROP TABLE IF EXISTS `btc_default_utxo`;
CREATE TABLE `btc_default_utxo` (
  `txid` char(64) NOT NULL COMMENT '交易id',
  `vout` int(11) NOT NULL,
  `count` decimal(64,0) NOT NULL,
  `script_pub_key` text NOT NULL,
  `address` char(34) NOT NULL,
  `sync_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `block_height` int(11) NOT NULL,
  `status` varchar(4) NOT NULL,
  `spend_txid` char(64) DEFAULT NULL,
  `collection_time` datetime DEFAULT NULL,
  PRIMARY KEY (`txid`,`vout`),
  KEY `address` (`address`),
  KEY `status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `btc_withdraw`
-- ----------------------------

DROP TABLE IF EXISTS `btc_withdraw`;
CREATE TABLE `btc_withdraw` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `to_address` char(34) NOT NULL,
  `count` decimal(64,0) NOT NULL,
  `apply_datetime` datetime NOT NULL,
  `update_datetime` datetime NOT NULL,
  `status` varchar(4) NOT NULL,
  `withdraw_txid` char(64) DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `withdraw_txid_key` (`withdraw_txid`),
  KEY `status_key` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `btc_withdraw_utxo`
-- ----------------------------
DROP TABLE IF EXISTS `btc_withdraw_utxo`;
CREATE TABLE `btc_withdraw_utxo` (
  `txid` char(64) NOT NULL COMMENT '交易id',
  `vout` int(11) NOT NULL,
  `count` decimal(64,0) NOT NULL,
  `script_pub_key` text NOT NULL,
  `address` char(34) NOT NULL,
  `sync_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `block_height` int(11) NOT NULL,
  `status` varchar(4) NOT NULL,
  `withdraw_txid` char(64) DEFAULT NULL,
  PRIMARY KEY (`txid`,`vout`),
  KEY `address` (`address`),
  KEY `status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS = 1;
