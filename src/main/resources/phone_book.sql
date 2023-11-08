--Mysql version: 8.0.31

-- Host: localhost    Database: phonebook


DROP TABLE IF EXISTS `contacts`;



CREATE TABLE `contacts` (
  `phone_number` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(50) NOT NULL,
  `street` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `last_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`phone_number`)
)


