BEGIN;
###########

DELETE FROM btc_withdraw;
DELETE FROM btc_withdraw_utxo;

insert into `btc_withdraw_utxo` ( `txid`, `vout`, `count`, `script_pub_key`, `address`, `sync_time`, `block_height`, `status`, `withdraw_txid`) values
( '3ff8f92c33f192f300d2abd92a51a4bfc4e808be025a66b23d1b17e47e7a03ab', '0', '100000000', '76a9143afe4954cc1b0026296e550770a2018fedf368b888ac', 'mktt7K5TH6aieW2xUV6fBjJyEbxPs6QjgG', '2018-01-31 16:08:47', '1', '0', null),

( '3ff8f92c33f192f300d2abd92a51a4bfc4e808be025a66b23d1b17e47e7a03ab', '1', '50000000', '76a9143afe4954cc1b0026296e550770a2018fedf368b888ac', 'mktt7K5TH6aieW2xUV6fBjJyEbxPs6QjgG', '2018-01-31 16:08:47', '1', '0', null),

( '3ff8f92c33f192f300d2abd92a51a4bfc4e808be025a66b23d1b17e47e7a03ab', '2', '70000000', '76a9143afe4954cc1b0026296e550770a2018fedf368b888ac', 'mktt7K5TH6aieW2xUV6fBjJyEbxPs6QjgG', '2018-01-31 16:08:47', '1', '0', null);




INSERT INTO btc_withdraw VALUES
  (1, 'mnB1TPWm7vtaqycZGXBkjwtkVq6Vzb6Nci', 100000000, now(), now(), '0',NULL ),
  (2, 'mnB1TPWm7vtaqycZGXBkjwtkVq6Vzb6Nci', 100000000, now(), now(), '0',NULL );

###########
COMMIT;