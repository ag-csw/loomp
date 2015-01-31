-- IMPORTANT: Each SQL command has to end with ';'. Otherwise the installer is
-- unable to execute them.

--
-- Tabellenstruktur für Tabelle `sys_parameter`
--

CREATE TABLE IF NOT EXISTS `sys_parameter` (
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY  (`name`)
) DEFAULT CHARSET=utf8;

-- delete the old version value and insert the new one
DELETE FROM `sys_parameter` WHERE `name` = 'db_version';
INSERT INTO `sys_parameter` VALUES ('db_version', '0.3');
