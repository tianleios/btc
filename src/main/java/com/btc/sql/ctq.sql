# CREATE TABLE `tctq_eth_address` (
#   `id` LONG NOT NULL,
#   `address` char(42) NOT NULL,
#   `type` varchar(3) NOT NULL,
#   `create_datetime` datetime NOT NULL,
#   PRIMARY KEY (`code`),
#   KEY `address_index` (`address`)
# ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

## btc地址表
CREATE TABLE `tctq_btc_address` (
  `id`              INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `address`         CHAR(36)        NOT NULL
  COMMENT '地址',
  `type`            VARCHAR(4)      NOT NULL,
  `create_datetime` DATETIME        NOT NULL,
  KEY `address_index` (`address`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

## utxo 表
CREATE TABLE `tctq_btc_utxo` (

  `id`             INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
  `txid`           CHAR(64)                       NOT NULL,
  `vout`           INT                            NOT NULL,
  `count`          DECIMAL(64, 0)                 NOT NULL,
  `script_pub_key` TEXT                           NOT NULL,
  `address`        CHAR(34)                       NOT NULL,
  `sync_time`      TIMESTAMP                      NOT NULL,
  `block_height`   INT                            NOT NULL,
  `status`         VARCHAR(4)                     NOT NULL
  COMMENT '1-out未推送，2-out已推送，3-in未推送，4-in已推送',
  UNIQUE KEY `txid_vout_unique_key`(`txid`, `vout`),
  KEY `address`(address),
  KEY `status`(status)

)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

# INSERT INTO `tsys_config`(type, ckey, cvalue, updater, update_datetime, remark) VALUES ('0','curBtcBlockNumber','0','code',now(),'BTC下次从哪个区块开始扫描');