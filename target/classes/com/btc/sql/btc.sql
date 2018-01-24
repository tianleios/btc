
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
  `count` DECIMAL(64) NOT NULL,
  `script_pub_key` TEXT NOT NULL,
  `address` CHAR(34) NOT NULL ,
  `sync_time` TIMESTAMP NOT NULL ,
  `block_height` INT NOT NULL ,
  `status` VARCHAR(4) NOT NULL ,
  `spend_txid` CHAR(64) NOT NULL,

  PRIMARY KEY (`txid`, `vout`),
  KEY `address`(address),
  KEY `status`(status)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

##提现utxo
CREATE TABLE `btc_withdraw_utxo` (

  `txid` CHAR(64) NOT NULL
  COMMENT '交易id',
  `vout` INT      NOT NULL,
  `count` DECIMAL(64) NOT NULL,
  `script_pub_key` TEXT NOT NULL,
  `address` CHAR(34) NOT NULL ,
  `sync_time` TIMESTAMP NOT NULL ,
  `block_height` INT NOT NULL ,
  `status` VARCHAR(4) NOT NULL ,
  `withdraw_txid` CHAR(64) NOT NULL,
  PRIMARY KEY (`txid`, `vout`),
  KEY `address`(address),
  KEY `status`(status)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;