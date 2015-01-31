<?php

define('SEARCH_INDEX_LOCATION', APPLICATION_PATH . '/data/loomp-index');
define('SEARCH_IMPORT_SIZE', 100);
define('SEARCH_RESULTS_SIZE', 50);
define('INDEX_CHARSET','utf-8');

setlocale(LC_CTYPE, 'de_DE.utf-8') or die('unable to set locale');
Zend_Search_Lucene_Analysis_Analyzer::setDefault(new Zend_Search_Lucene_Analysis_Analyzer_Common_Utf8_CaseInsensitive());
Zend_Search_Lucene_Search_QueryParser::setDefaultEncoding(INDEX_CHARSET);

function searchGetIndex() {
	$index = null;
	try {
		$index = Zend_Search_Lucene::open(SEARCH_INDEX_LOCATION);	
	}
	catch (Exception $e) {
		try {
			$index = Zend_Search_Lucene::create(SEARCH_INDEX_LOCATION);
			searchImportAll($index);
		}
		catch (Exception $e) {
			print "something really bad happened.";	
		}
	}
	return $index;
}

function searchQuery($query, $size = SEARCH_RESULTS_SIZE) {
	$index = searchGetIndex();
	Zend_Search_Lucene::setResultSetLimit($size);
	return $index->find($query);
}

function searchMashups($query, $start = 0, $limit = 20) {
	$results = searchQuery("searchtext:\"$query\"~0.8",0);
	$mashups = array();
	$results = array_slice($results,$start,$limit);

	$la = getLA();
	foreach ($results as $result) {
		$ms = $la->getMashupsForFragment($result->uri);
		foreach ($ms as $m_uri) {
			if (!array_key_exists($m_uri, $mashups)) $mashups[$m_uri] = $la->loadMashup($m_uri);
		}
	}
	return $mashups;
}

function searchFragmentsForUser($user, $query) {
	$results = searchQuery("user: \"$user\" AND searchtext:\"$query\"~0.8");
	$fragments = array();
	foreach ($results as $result) {
		$fragments[] = new Fragment($result->uri, $result->user, $result->created, $result->modified, $result->title, $result->content);
	}
	return $fragments;
}

function searchImportAll($index) {
	$start = 0;
	$fragments = array();
	do {
		$fragments = getLa()->getAllFragments($start, SEARCH_IMPORT_SIZE);
		foreach ($fragments as $fragment) {
			if ($fragment->getType() == "text") searchImportFragment($fragment, $index);
		}
		$start = $start + SEARCH_IMPORT_SIZE;
	} while (count($fragments) > 0);
	$index->commit();
}

function searchImportFragment($fragment, $index) {
	if ($fragment->getType() != "text") return;
	$doc = new Zend_Search_Lucene_Document();
	$doc->addField(Zend_Search_Lucene_Field::text('uri', $fragment->getUri(),INDEX_CHARSET));
	$doc->addField(Zend_Search_Lucene_Field::text('user', $fragment->getCreatorId(),INDEX_CHARSET));
	$doc->addField(Zend_Search_Lucene_Field::text('created', $fragment->getCreateDate(),INDEX_CHARSET));
	$doc->addField(Zend_Search_Lucene_Field::text('modified', $fragment->getModifyDate(),INDEX_CHARSET));
	$doc->addField(Zend_Search_Lucene_Field::text('title', $fragment->getTitle(),INDEX_CHARSET));
	$doc->addField(Zend_Search_Lucene_Field::text('content', $fragment->getSaveContent(),INDEX_CHARSET));
	$doc->addField(Zend_Search_Lucene_Field::text('searchtext', $fragment->getTitle() . " " . $fragment->getContent(),INDEX_CHARSET));
	$ret = $index->addDocument($doc);
}

function searchUpdateFragment($fragment) {
	$index = searchGetIndex();
	searchDeleteFragment($fragment);
	searchImportFragment($fragment, $index);
	$index->commit();
}

function searchDeleteFragment($fragment) {
	$index = searchGetIndex();
	$remove = $index->find('uri: "' . $fragment->getUri().'"');
	foreach ($remove as $old_fragment) {
	    $index->delete($old_fragment->id);
	}
	$index->commit();
}

function getLA() {
	return Zend_Registry::getInstance()->loompApi;
}
?>
