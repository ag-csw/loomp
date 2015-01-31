-- IMPORTANT: Each SQL command has to end with ';'. Otherwise the installer is
-- unable to execute them.

--
-- Tabellenstruktur für Tabelle `access`
--

CREATE TABLE IF NOT EXISTS `access` (
  `mashup` varchar(255) NOT NULL,
  `useruri` varchar(255) NOT NULL,
  `accesskey` varchar(100) NOT NULL,
  PRIMARY KEY  (`mashup`)
) DEFAULT CHARSET=utf8;

--
-- Tabellenstruktur für Tabelle `viewconfig`
--

CREATE TABLE IF NOT EXISTS `viewconfig` (
  `name` varchar(100) NOT NULL,
  `fmt` varchar(100) NOT NULL,
  `key` varchar(100) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY  (`name`,`fmt`,`key`)
) DEFAULT CHARSET=utf8;
