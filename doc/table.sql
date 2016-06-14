
Create Table

CREATE TABLE `sys_menu1` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `parentid` int(11) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `url` varchar(100) DEFAULT NULL,
  `sequence` int(11) DEFAULT NULL,
  `creator` int(11) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `updater` int(11) DEFAULT NULL,
  `updatetime` datetime DEFAULT NULL,
  `delflag` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8