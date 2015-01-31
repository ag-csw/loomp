-- IMPORTANT: Each SQL command has to end with ';'. Otherwise the installer is
-- unable to execute them.

-- 
-- Tabellenstruktur für Tabelle `datasets`
--

CREATE TABLE IF NOT EXISTS `datasets` (
  `datasetName` varchar(255) NOT NULL default '',
  `defaultModelUri` varchar(255) NOT NULL default '0',
  PRIMARY KEY  (`datasetName`),
  KEY `datasetName` (`datasetName`)
) DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `dataset_model`
--

CREATE TABLE IF NOT EXISTS `dataset_model` (
  `datasetName` varchar(255) NOT NULL default '0',
  `modelId` bigint(20) NOT NULL default '0',
  `graphURI` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`modelId`,`datasetName`)
) DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `models`
--

CREATE TABLE IF NOT EXISTS `models` (
  `modelID` bigint(20) NOT NULL,
  `modelURI` varchar(255) NOT NULL,
  `baseURI` varchar(255) default '',
  PRIMARY KEY  (`modelID`),
  UNIQUE KEY `m_modURI_idx` (`modelURI`)
) DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `namespaces`
--

CREATE TABLE IF NOT EXISTS `namespaces` (
  `modelID` bigint(20) NOT NULL,
  `namespace` varchar(255) NOT NULL,
  `prefix` varchar(255) NOT NULL,
  PRIMARY KEY  (`modelID`,`namespace`),
  KEY `n_mod_idx` (`modelID`)
) DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `statements`
--

CREATE TABLE IF NOT EXISTS `statements` (
  `modelID` bigint(20) NOT NULL,
  `subject` varchar(255) NOT NULL,
  `predicate` varchar(255) NOT NULL,
  `object` text,
  `l_language` varchar(255) default '',
  `l_datatype` varchar(255) default '',
  `subject_is` varchar(1) NOT NULL,
  `object_is` varchar(1) NOT NULL,
  KEY `s_mod_idx` (`modelID`),
  KEY `s_sub_idx` (`subject`(200)),
  KEY `s_pred_idx` (`predicate`(200)),
  KEY `s_obj_idx` (`object`(250))
) DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL auto_increment,
  `email` varchar(200) NOT NULL,
  `registered` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `password` varchar(100) NOT NULL,
  `activation` varchar(100) NOT NULL,
  `active` tinyint(1) NOT NULL default '0',
  `userlevel` tinyint(4) NOT NULL default '0',
  `uri` varchar(200) NOT NULL,
  `firstname` varchar(100) NOT NULL,
  `lastname` varchar(100) NOT NULL,
  `organisation` varchar(100) default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `unique_uri` (`uri`)
) DEFAULT CHARSET=utf8;


-- Admin default password is "rdfa"
-- INSERT INTO `users` (`id`, `email`, `registered`, `password`, `activation`, `active`, `userlevel`, `uri`, `firstname`, `lastname`, `organisation`) VALUES
-- (1, 'admin@loomp.org', '2009-02-07 15:40:23', '97424609272684e6f0f1aaaef42d3724', '', 1, 10, 'http://www.loomp.org/user/admin', 'Super', 'User', NULL);


-- INSERT INTO `models` (`modelID`, `modelURI`, `baseURI`) VALUES
-- (1, 'http://www.loomp.org/loomp/dbModel/', '');

