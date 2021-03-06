
#1.创建用户表
CREATE TABLE IF NOT EXISTS `user`(

  `id` INT AUTO_INCREMENT NOT NULL ,
  `phone` VARCHAR(15) NOT NULL ,
  `username` VARCHAR(15) NOT NULL COMMENT '用户名',
  `password` VARCHAR(32) NOT NULL COMMENT '密码',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
   PRIMARY KEY (`id`)

);

#插入数据
INSERT INTO `user`(phone, username, password, create_time, update_time) VALUES
  ('13738647888','tian','1234455',now(),now()),
  ('13738647888','zhang','1234455',now(),now()),
  ('13782647888','wang','1234455',now(),now()),
  ('13783647822','li','1234455',now(),now());

CREATE TABLE `product`(`id` int,`name` VARCHAR(32),`stock` int,`price` DECIMAL, `create_datetime` DATETIME NOT NULL ,`update_datetime` DATETIME NOT NULL );


#
CREATE  TABLE xs(
  学号 char(6),
  姓名 CHAR(8),
  专业名 char(10),
  性别 TINYINT COMMENT '男1,女0',
  出生日期 DATE,
  总学分 TINYINT,
  PRIMARY KEY (学号)
);

INSERT INTO xs(
  学号 ,
  姓名 ,
  专业名 ,
  性别 ,
  出生日期 ,
  总学分
)
 VALUES
   ('081101','王林','计算机',1,DATE('1990-02-10'),50),
('081102','程明','计算机',1,DATE('1991-02-01'),50),
('081103','王燕','计算机',0,DATE('1989-10-06'),50),
('081104','韦严平','计算机',1,DATE('1990-08-26'),50),
('081106','李方方','计算机',1,DATE('1990-11-20'),50),
('081107','李明','计算机',1,DATE('1990-05-01'),54),
('081108','林一凡','计算机',1,DATE('1989-08-05'),52),
('081109','张强民','计算机',1,DATE('1989-08-11'),50),
('081110','张蔚','计算机',1,DATE('1991-07-22'),50),
('081111','赵琳','计算机',0,DATE('1990-03-18'),50),
('081113','严红','计算机',0,DATE('1989-08-11'),48),

('081201','王敏','通信工程',1,DATE('1989-08-10'),42),
('081202','王林','通信工程',1,DATE('1989-01-29'),40),
('081204','马琳琳','通信工程',0,DATE('1989-02-10'),42),
('081206','李计','通信工程',1,DATE('1989-09-20'),42),
('081210','李红庆','通信工程',1,DATE('1989-05-01'),44),
('081216','孙祥新','通信工程',1,DATE('1989-03-09'),42),
('081218','孙研','通信工程',1,DATE('1990-10-09'),42),
('081220','吴薇化','通信工程',1,DATE('1990-03-18'),42),
('081221','刘燕敏','通信工程',0,DATE('1989-11-12'),42),
('081241','罗琳琳','通信工程',0,DATE('1990-01-30'),50);




#
CREATE  TABLE kc(
  课程号 char(3),课程名 CHAR(16),开学学期 TINYINT,学时 TINYINT,学分 TINYINT,
  PRIMARY KEY (课程号)
);

INSERT INTO kc(
  课程号 ,课程名 ,开学学期 ,学时 ,学分 )
    VALUES
      (101,'计算机基础',1,80,5),
      (102,'程序设计与语言',2,68,4),
      (206,'离散数学',4,68,4),
      (208,'数据结构',5,68,4),
      (209,'操作系统',6,68,4),
      (210,'计算机原理',5,85,5),
      (212,'数据库原理',7,68,4),
      (301,'计算机网络',7,51,3),
      (302,'软件工程',7,51,3);


 #
CREATE  TABLE xs_kc(
  学号 char(6),课程号 CHAR(3),成绩 TINYINT,学分 TINYINT
);

INSERT INTO xs_kc(
  学号 ,课程号 ,成绩
) VALUES
  (081101,101,80),
  (081101,102,78),
  (081101,206,76),
  (081103,101,62),
  (081103,102,70),
  (081103,206,81),
  (081104,101,90),
  (081104,102,84),
  (081104,206,65),
  (081102,102,78),
  (081102,206,78),
  (081106,101,65),
  (081106,102,71),
  (081106,206,80),

  (081107,101,80),
  (081107,102,78),
  (081107,206,76),
  (081108,101,62),
  (081108,102,70),
  (081108,206,81),
  (081109,101,90),
  (081109,102,84),
  (081109,206,65),
  (081110,101,78),
  (081110,102,78),
  (081110,206,65),
  (081111,101,71),
  (081111,102,80),

  (081111,206,76),
  (081113,101,63),
  (081113,102,79),
  (081113,206,60),
  (081201,101,80),
  (081202,101,65),
  (081203,101,87),
  (081204,101,91),
  (081210,101,76),
  (081216,101,81),
  (081218,101,70),
  (081220,101,82),
  (081221,101,76),
  (081241,101,90);

#
select count(*) from xs_kc join kc on kc.`课程号` = xs_kc.`课程号` group by xs_kc.`课程号`;

#
select  count(*) from xs_kc GROUP BY xs_kc.`课程号`;

#
select xs_kc.学号,avg(xs_kc.成绩) from xs_kc where xs_kc.学号 in (

  select xs.学号 from xs where xs.专业名 = '通信工程'

) group by xs_kc.`学号` having avg(xs_kc.成绩) > 85;

select xs_kc.学号,avg(xs_kc.成绩) from xs_kc  group by xs_kc.`学号` having avg(xs_kc.成绩) > 85;

