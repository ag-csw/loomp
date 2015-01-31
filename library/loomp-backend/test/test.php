<pre>
<?php
error_reporting(E_ALL);
ini_set('display_errors', '1');

define("RDFAPI_INCLUDE_DIR", "C:/xampp/htdocs/loomp/library/loomp-backend/lib/rdfapi-php/api/");
include(RDFAPI_INCLUDE_DIR . "RDFAPI.php");


require_once "./../api.php";
require_once "./../mashup.php";
require_once "./../fragment.php";
require_once "./../vocabulary.php";
require_once "./../annotation.php";


$la = new LoompApi();

$la->getResDescrSortByPropLabel('asdf');


?>