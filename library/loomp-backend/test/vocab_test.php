<?php
error_reporting(E_ALL);
ini_set('display_errors', '1');

// ----
define("RDFAPI_INCLUDE_DIR", 'C:/xampp/htdocs/loomp/library/loomp-backend/lib/rdfapi-php/api/');

// RAP main classes
require_once RDFAPI_INCLUDE_DIR . 'RdfAPI.php';

// RAP extensions
require_once RDFAPI_INCLUDE_DIR . PACKAGE_RDQL;
require_once RDFAPI_INCLUDE_DIR . 'vocabulary/DC_C.php';
require_once RDFAPI_INCLUDE_DIR . 'vocabulary/RDF_C.php';

// ARC
require_once './../lib/arc/ARC2.php';

// loomp vocabulary class
require_once './../LOOMP_C.php';
// ---------------


require_once "./../api.php";
require_once "./../mashup.php";
require_once "./../fragment.php";
require_once "./../vocabulary.php";
require_once "./../annotation.php";


/*
$folder=dir("./../../../application/annotations/");

while($folderEntry=$folder->read()){
echo $folderEntry."<br>";
}

$folder->close();
*/


$api = new LoompApi();



$v = $api->getVocabularies("de");
foreach ($v as $vo)
    foreach ($vo as $k=>$voo) {
        echo $k ." " .$voo ."<br>";
    }


$vs = $api->getVocabulary("geo.rdf");
$vs->_htmlOutput();

/*
foreach ($v->getAnnotations() as $ann) {
    $ann->_htmlOutput();
}
*/

?>