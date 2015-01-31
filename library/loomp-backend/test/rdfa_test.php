<pre>
<?php
require_once '../lib/arc/ARC2.php';

$url = "http://www.w3.org/TR/xhtml-rdfa-primer/alice-example.html";

$parser = ARC2::getSemHTMLParser();
$parser->parse('',file_get_contents('test.html'));
$parser->extractRDF('rdfa');

$triples = $parser->getTriples();
$rdfxml = $parser->toRDFXML($triples);

echo htmlentities($rdfxml);



?>