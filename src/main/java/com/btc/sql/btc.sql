
##地址
CREATE TABLE `btc_address` (
  `address` char(36) NOT NULL COMMENT '地址',
  `privatekey` varchar(64) NOT NULL,
  `type` varchar(4) NOT NULL,
  PRIMARY KEY (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##归集utxo
CREATE TABLE `btc_default_utxo` (
  `txid` CHAR(64) NOT NULL
  COMMENT '交易id',
  `vout` INT      NOT NULL,
  `count` DECIMAL(64,0) NOT NULL,
  `script_pub_key` TEXT NOT NULL,
  `address` CHAR(34) NOT NULL ,
  `sync_time` TIMESTAMP NOT NULL ,
  `block_height` INT NOT NULL ,
  `status` VARCHAR(4) NOT NULL ,
  `spend_txid` CHAR(64) NOT NULL,
  `collection_time` DATETIME NULL COMMENT '归集时间',

  PRIMARY KEY (`txid`, `vout`),
  KEY `address`(address),
  KEY `status`(status)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##提现utxo
CREATE TABLE `btc_withdraw_utxo` (

  `txid` CHAR(64) NOT NULL
  COMMENT '交易id',
  `vout` INT      NOT NULL,
  `count` DECIMAL(64,0) NOT NULL,
  `script_pub_key` TEXT NOT NULL,
  `address` VARCHAR(40) NOT NULL ,
  `sync_time` TIMESTAMP NOT NULL ,
  `block_height` INT NOT NULL ,
  `status` VARCHAR(4) NOT NULL ,
  `withdraw_txid` CHAR(64) NOT NULL,
  PRIMARY KEY (`txid`, `vout`),
  KEY `address`(address),
  KEY `status`(status)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##提现申请表
CREATE TABLE `btc_withdraw` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `to_address` CHAR(34) NOT NULL ,
  `count` DECIMAL(64,0) NOT NULL ,
  `apply_datetime` DATETIME NOT NULL,
  `update_datetime` DATETIME NOT NULL,
  `status` VARCHAR(4) NOT NULL ,
  `withdraw_txid` CHAR(64) NOT NULL DEFAULT '',

  PRIMARY KEY (`id`),
  KEY `withdraw_txid_key`(`withdraw_txid`),
  KEY `status_key`(`status`)


) ENGINE=InnoDB DEFAULT CHARSET=utf8;