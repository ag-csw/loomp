<?php
error_reporting(E_ALL);
ini_set('display_errors', '1');

// ----
define("RDFAPI_INCLUDE_DIR", 'C:/xampp/htdocs/loomp/library/loomp-backend/lib/rdfapi-php/api/');

// RAP main classes
require_once RDFAPI_INCLUDE_DIR . 'RdfAPI.php';
require_once RDFAPI_INCLUDE_DIR . PACKAGE_SPARQL;


function getResFromDbpedia ($label, $language="en") {
    
    $c = ModelFactory::getSparqlClient("http://dbpedia.org/sparql");
    $q = new ClientQuery();

    $qs = "SELECT ?x, ?comment
            WHERE { ?x rdfs:comment ?comment .
            FILTER (lang(?comment) = '$language')
            ?x rdfs:label '$label'@$language
    }";

    $q->query($qs);
    $r=$c->query($q);
        
    $res = array();
    foreach($r as $i){
        $res[$i['?x']->getLabel()] = $i['?comment']->getLabel();
    }
    return $res;
}

$r=getResFromDbpedia("Tempelhof");

foreach ($r as $k=>$v) {
    
    echo $k ." : " .$v;
}

//SPARQLEngine::writeQueryResultAsHtmlTable($r);
?>