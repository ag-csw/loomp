<?php
require_once "mashup.php";
require_once "fragment.php";
require_once "vocabulary.php";
require_once "annotation.php";
require_once "lib/specialchars.php";

//@hannes: todo: move to config file
require_once RDFAPI_INCLUDE_DIR . 'vocabulary/RDFS_C.php';
require_once RDFAPI_INCLUDE_DIR . PACKAGE_SPARQL;

error_reporting(E_ALL);

class LoompApi {
	
	private $rdfModel;
	private $lpUsedUris = array();
	
	//@hannes: todo: define a constant !!!
	private $annotationsDirPath;
	
	/**
	 * Loomp backend API
	 * Function Calls
	 * @package Loomp_Backend
	 * @author Hannes Muehleisen, Radoslaw Oldakowski
	 *
	 * */
	 


	public function __construct() {
		$this->annotationsDirPath = APPLICATION_PATH . "/annotations/";
		$this->rdfModel = Zend_Registry::getInstance()->rdfModel;
	}
	
	/**
	 * get Fragments from RDF Store
	 *
	 * @param  string    $userId    RDF URI for user
	 * @return array                Array of Fragment objects
	 */
	public function getFragmentsForUser($userId) {
	
		$fragments = array();
	
		$queryString = "SELECT ?fURI " .
			       "WHERE (?fURI rdf:type <" .LOOMP::FRAGMENT()->getURI() .">)" .
			       "(?fURI dc:creator <" .$userId .">)";
	
		$it = $this->rdfModel->rdqlQueryAsIterator($queryString);
		while ($it->hasNext()) {
			$r = $it->next();
			$fragments[] = $this->loadFragment($r['?fURI']->getURI());
		}
	
		return $fragments;
	}
	
	/**
	 * get Fragments from RDF Store for indexing
	 * @param  int    $start    Start index
	 * @param  int    $limit    Max. fragment count
	 * @return array            Array of Fragment objects
	 */
	function getAllFragments($start = 0, $limit = 100) {

		$fragments = array();
	
		$m = $this->rdfModel->find(NULL, RDF::TYPE(), LOOMP::FRAGMENT());
		$it = $m->getStatementIterator();
	
		if ($start>0) {
			$it->moveTo($start);
		}
	
		$i = 0;
		while ($it->hasNext() and ($i < $limit)) {
			$s = $it->next();
			$i++;
			$fragments[] = $this->loadFragment($s->getLabelSubject());
		}
		return $fragments;
	}
	
	/**
	 * get Mashups from RDF Store for indexing
	 * @param  int    $start    Start index
	 * @param  int    $limit    Max. fragment count
	 * @return array            Array of Fragment objects
	 */
	function getAllMashups($start = 0, $limit = 20) {
		$mashup = array();
	
		$m = $this->rdfModel->find(NULL, RDF::TYPE(), LOOMP::MASHUP());
		$it = $m->getStatementIterator();
	
		if ($start>0) {
			$it->moveTo($start);
		}
	
		$i = 0;
		while ($it->hasNext() and ($i < $limit)) {
			$s = $it->next();
			$i++;
			$mashup[] = $this->loadMashup($s->getLabelSubject());
		}
		return $mashup;
	}
	
	/**
	 * get Mashups from RDF Store
	 *
	 * @param  string    $userId    RDF URI for user
	 * @return array                Array of Mashup objects
	 */
	function getMashupsForUser($userId) {
		$mashups = array();
	
		$queryString = "SELECT ?mURI " .
			       "WHERE (?mURI rdf:type <" .LOOMP::MASHUP()->getURI() .">)" .
			       "(?mURI dc:creator <" .$userId .">)";
	
		$it = $this->rdfModel->rdqlQueryAsIterator($queryString);
		while ($it->hasNext()) {
			$r = $it->next();
			$mashups[] = $this->loadMashup($r['?mURI']->getURI());
		}
	
		return $mashups;
	}
	
	
	/**
	 * get newest n Mashups from RDF Store
	 *
	 * @param  string    $count    mashup count
	 * @return array                Array of Mashup objects
	 */
	 // TODO
	function getLastMashups($count) {
		return array_slice($this->getMashupsForUser("http://www.loomp.org/users/hannes"),0,$count);
	}
	
	/**
	 * loads a Mashup from the RDF Store
	 *
	 * @param  string    $uri	      Mashup URI
	 * @return Mashup                     Mashup Object
	 * @return boolean		      false if failed
	 */
	function loadMashup($uri) {	
		$queryString = "SELECT ?creatorUri ?cDate ?mDate ?title " .
				"WHERE " .
				"(<" .$uri ."> <" .DC::CREATOR()->getURI() ."> ?creatorUri) " .
				"(<" .$uri ."> <" .DC::CREATED()->getURI() ."> ?cDate) " .
				"(<" .$uri ."> <" .DC::MODIFIED()->getURI() ."> ?mDate) " .
				"(<" .$uri ."> <" .DC::TITLE()->getURI() ."> ?title) " ;
	
		$it = $this->rdfModel->rdqlQueryAsIterator($queryString);
		if ($it->hasNext()) {
			$r = $it->next();
	
			$creatorUri = $r['?creatorUri']->getURI();
			$createDate = $r['?cDate']->getLabel();
			$modifyDate = $r['?mDate']->getLabel();
			$title = $r['?title']->getLabel();
		}
		else {		
			return false;
		}
		
		$queryString = "SELECT ?fragment ?p " .
				"WHERE " .
				"(<" .$uri ."> ?p ?fragment) " .
				"(?fragment rdf:type <" .LOOMP::FRAGMENT()->getURI() .">) ";
	
		$it = $this->rdfModel->rdqlQueryAsIterator($queryString);
		
		$f = array();		
		while ($it->hasNext()) {
			$r = $it->next();
			// 1..n from rdf:_1 .. rdf:_n as array key
			$f[substr($r['?p']->getURI(), strlen($r['?p']->getURI())-1)] = $r['?fragment']->getURI();
		}
		ksort($f);
		
		$fragments = array();
		foreach ($f as $fURI) {
			$fragments[] = 	$this->loadFragment($fURI);
		}
	
		return new Mashup($uri, $creatorUri, $createDate, $modifyDate, $title, $fragments);
	}
	
	/**
	 * saves a Mashup to the RDF Store
	 * An existing mashup is simply overwritten (remove existing, save a new one)
	 *
	 * @param  Mashup    $mashup           Mashup Object (possibly containing Chunks)
	 * @param  array     $deletedChunks    Array of Chunk objects to be deleted from this Mashup (optional)
	 * @return boolean                     true if operation was successful
	 */
	function saveMashup($mashup, $deletedChunks = array()) {
		$this->removeMashup($mashup);
		$mashupRes = new Resource($mashup->getUri());
	
		$this->rdfModel->add(new Statement($mashupRes, RDF::TYPE(), LOOMP::MASHUP()));
		$this->rdfModel->add(new Statement($mashupRes, DC::CREATOR(), new Resource($mashup->getCreatorId())));
		$this->rdfModel->add(new Statement($mashupRes, DC::TITLE(), new Literal($mashup->getTitle())));
		$this->rdfModel->add(new Statement($mashupRes, DC::CREATED(), new Literal($mashup->getCreateDate())));
		$this->rdfModel->add(new Statement($mashupRes, DC::MODIFIED(), new Literal($mashup->getModifyDate())));
	
		$this->rdfModel->add(new Statement($mashupRes, RDF::TYPE(), RDF::SEQ()));
	
		foreach ($mashup->getFragments() as $k => $fragment) {
	
			$this->rdfModel->add(new Statement($mashupRes, new Resource(RDF_NAMESPACE_URI."_".($k+1)), new Resource($fragment->getUri())));
			$this->_saveFragment($fragment);
		}
	
		// remove fragments which are not associated with any other mashup
		foreach ($deletedChunks as $f) {
			if ($this->_fragmentCount($f->getUri()) == 0) {
				$this->_removeFragment($f);
			}
		}
	
		return true;
	}
	
	/**
	 * gets Vocabluaries from store
	 *
	 * @return array        []['ID']   ID -> filename
	 * 			  ['label']
	 * 			  ['comment]
	 */
	function getVocabularies ($lang="en") {
		
		$v = array();		
		$folder=dir($this->annotationsDirPath);
	
			$i=0;
			while($folderEntry=$folder->read()){
				if ($folderEntry != "." && $folderEntry != ".." && !is_dir($folderEntry)) {
					$v[$i]['ID'] = $folderEntry;
					$a = $this->_getVocabLabelAndComment($folderEntry, $lang);
					$v[$i]['label'] = $a['label'];
					$v[$i++]['comment'] = $a['comment'];
				}				
			}
		$folder->close();
		
		return $v;	
	}
	
	
	function getVocabulary ($id, $lang="en") {
		
		$l = '';
		$d = '';
		
		$m = ModelFactory::getDefaultModel();
		$m->load($this->annotationsDirPath .$id);

		$it = $m->findAsIterator(NULL, RDF::TYPE(), LOOMP::ANNOTATION_SET());		
		if ($it->hasNext()) {
			$s = $it->next()->getSubject();
			
			$l = $this->_getLiteralInLang($m->find($s, RDFS::LABEL(), NULL), $lang);			
			$d = $this->_getLiteralInLang($m->find($s, RDFS::COMMENT(), NULL), $lang);			
		}
		
		$ann = $this->_getAnnotations($m, $lang);		
		return new Vocabulary($id, $l, $d, $ann);	
	}
	
	
	/**
	 * gets an RDF URI
	 * WARNING: conflict detection is only possible for those loomp users who created and saved at least one mashup/fragment !!!
	 * 	  : If such a conflict is detected a new URI will be generated by appending "_" .i++
	 *
	 * @param  string    $title    identifier to get uri for (may be an e-Mail address)
	 * @param  Resource  $type	   RDF Type
	 * @return string              RDF URI for this item
	 */
	
	
	public function getUriForTitle($title, $type) {
		$conflict = true;
		
		if (trim($title) == "" || true) { // always use hashes
			$title = md5(microtime() . $type . rand(0,time()));
		}
		$baseuri = LOOMP_BASE_PATH . "/" . strtolower(basename($type->getURI())) . "/" . substr(specialchars_replace($title),0,70);
		$uri = $baseuri;
		$i = 1;
		do {
			$uri_res = new Resource($uri);
			if (!$this->rdfModel->findFirstMatchingStatement($uri_res, NULL, NULL) &&
				!$this->rdfModel->findFirstMatchingStatement(NULL, NULL, $uri_res) &&
				!in_array($uri,$this->lpUsedUris)) {
				$conflict = false;
			}else {
				$uri = $baseuri .'_' .$i;
				$i++;
			}
		} while ($conflict);
		$this->lpUsedUris[] = $uri;
		return $uri;
	}
	
	
	public function getDBConn() {
		return $this->rdfModel->getDbConn();
	}
	
	/**
	 * Adds a new described resource to the triple store
	 * eg. $uri = 'Berlin', $type_uri = 'http://www.loomp.org/ontology/geography#cityName'
	 */
	public function createResource($uri,$type_uri) {
		$this->rdfModel->add(new Statement(new Resource($uri), RDF::TYPE(), new Resource($type_uri)));
		return true;
	}
	
	/**
	 * Get all Resources and their properties for a given type
	 */
	public function getResourcesAndProperties($type_uri) {
		$res = array();
		$it = $this->rdfModel->find(NULL, RDF::TYPE(), new Resource($type_uri))->getStatementIterator();
		while ($it->hasNext()) {
			$props = array();
			$s = $it->next();
			$item_uri = $s->getSubject()->getUri();
			$ita = $this->rdfModel->find($s->getSubject(), NULL, NULL)->getStatementIterator();
			while ($ita->hasNext()) {
				$a = $ita->next();
				$obj = trim(strip_tags($a->getLabelObject()));
				if (!empty($obj)) $props[$a->getPredicate()->getUri()] = "Label is:".$obj;
			}
			$res[$item_uri] = $props;
		}
		return $res;
	}

	
	/* SACH
	 * return matches where object == type_uri */ /*
	public function allMatchingResources($type_uri, $resLabel) {
	
		$res = array();
		$it = $this->rdfModel->find(NULL, RDF::TYPE(), new Resource($type_uri))->getStatementIterator();
		//$it = $this->rdfModel->find(NULL, new Resource($type_uri), NULL)->getStatementIterator();
		while ($it->hasNext()) {
			$props = array();
			$s = $it->next();
			$item_uri = $s->getSubject()->getUri();
			$ita = $this->rdfModel->find($s->getSubject(), NULL, NULL)->getStatementIterator();
			while ($ita->hasNext()) {
				$a = $ita->next();
				$obj = trim(strip_tags($a->getLabelObject()));
				if (!empty($obj) && $resLabel == $obj) $props[$a->getPredicate()->getUri()] = "Your Label ".$obj;
				else if (!empty($obj)) $props[$a->getPredicate()->getUri()] = $obj;
			}			
			$res[$item_uri] = $props;
		}
		return $res;
		
	}
	
	*/
	
	
		
	
	/* SACH */
	/** All matching Ressources by label */
	public function allMatchingResourcesByType($resType) {
	
			
		$res = array();
		
		//if($resUri == "none" && $resType != "none" && $resLabel!="none")
			//$it = $this->rdfModel->find(NULL, NULL, new Resource($resType))->getStatementIterator();
		/*
		if($resUri == "none" && $resType == "none" && $resLabel!="none")
			$it = $this->rdfModel->find(NULL, NULL, new Literal($resLabel))->getStatementIterator();
		
		else if($resUri != "none")
			$it = $this->rdfModel->find(new Resource($resUri), NULL, NULL)->getStatementIterator();
		
		else if($resType != "none")*/
			$it = $this->rdfModel->find(NULL, RDF::TYPE(), new Resource($resType))->getStatementIterator();
		
		//$it = $this->rdfModel->find(NULL, new Resource($type_uri), NULL)->getStatementIterator();
		//if($it->hasNext()) return "found something";
		//return $resLabel;
		while ($it->hasNext()) {
			$props = array();
			$s = $it->next();
			$item_uri = $s->getSubject()->getUri();
			$ita = $this->rdfModel->find($s->getSubject(), NULL, NULL)->getStatementIterator();
			
			while ($ita->hasNext()) {
				$a = $ita->next();
				
				//$this->isUri($a->getLabelObject())
				/* check, if labelobject is an uri */
				$target = $this->rdfModel->find(new Resource($a->getLabelObject()), NULL, NULL)->getStatementIterator();
				if($target->hasNext()) $isUri = true;
				else $isUri = false;
				
				if($isUri) {
					$m = $this->rdfModel->find(new Resource($a->getLabelObject()), NULL, NULL); 
					//$result = ModelFactory::getDefaultModel();
					$iter = $m->getStatementIterator();
					while ($iter->hasNext()) {
						$s = $iter->next();
						if (is_a($s->getObject(), "Literal")) {
							$obj = $s->getLabelObject();
							break;
						}
					}	
				} else $obj = trim(strip_tags($a->getLabelObject()));
				//if (!empty($obj)) $props[$a->getPredicate()->getUri()] = $obj;
				if (!empty($obj)) {
					if(!array_key_exists($a->getPredicate()->getUri(), $props)) $props[$a->getPredicate()->getUri()] = array();
					array_push($props[$a->getPredicate()->getUri()], $obj);
				}
			}			
			$res[$item_uri] = $props;
		}
		return $res;
		
	}
	
	
	
	/* SACH */
	public function findResourcesByPredicateAndLabel($resPred, $resLabel) {
	
		$res = array();
	
		if($resLabel=="<all>") $it = $this->rdfModel->find(NULL, new Resource($resPred), NULL)->getStatementIterator();
		
		else $it = $this->rdfModel->find(NULL, new Resource($resPred), new Literal($resLabel))->getStatementIterator();

		while ($it->hasNext()) {
			$props = array();
			$s = $it->next();
			$item_uri = $s->getSubject()->getUri();
			$ita = $this->rdfModel->find($s->getSubject(), NULL, NULL)->getStatementIterator();
			
			while ($ita->hasNext()) {
				$a = $ita->next();
				
				//$this->isUri($a->getLabelObject())
				/* check, if labelobject is an uri */
				$target = $this->rdfModel->find(new Resource($a->getLabelObject()), NULL, NULL)->getStatementIterator();
				if($target->hasNext()) $isUri = true;
				else $isUri = false;
				
				if($isUri) {
					$m = $this->rdfModel->find(new Resource($a->getLabelObject()), NULL, NULL); 
					//$result = ModelFactory::getDefaultModel();
					$iter = $m->getStatementIterator();
					while ($iter->hasNext()) {
						$s = $iter->next();
						if (is_a($s->getObject(), "Literal")) {
							$obj = $s->getLabelObject();
							break;
						}
					}	
				} else $obj = trim(strip_tags($a->getLabelObject()));
				//if (!empty($obj) && $resLabel == $obj) $props[$a->getPredicate()->getUri()] = $obj;
				//else 
				//if (!empty($obj)) $props[$a->getPredicate()->getUri()] = $obj;
				if (!empty($obj)) {
					if(!array_key_exists($a->getPredicate()->getUri(), $props)) $props[$a->getPredicate()->getUri()] = array();
					array_push($props[$a->getPredicate()->getUri()], $obj);
				}
			}			
			$res[$item_uri] = $props;
		}
		return $res;	
	
	}
	
	
	
	/* SACH 
		Return an array of possible predicates for the given Subject type 
	*/
		
	public function getPredicatesForSubject($subjectType) {
	
		
		
	}
	
	
	/* SACH 
		Return an array of possible Object types for the given predicate 
	*/
	public function getObjectsForPredicate($predicateType) {
	
	}
	
	
	
	
	
	
	/* SACH */
	
	public function findResourcesInFragment($annotURI) {
	
		$res = array();
		
		$containsPredicate = 'http://www.loomp.org/loomp/0.1/contains';
		//$it = $this->rdfModel->find(new Resource($fragmentURI), new Resource($containsPredicate), NULL)->getStatementIterator();
		$it = $this->rdfModel->find(NULL, new Resource($containsPredicate), new Resource($annotURI))->getStatementIterator();
		$fragment = $it->next();
		$fragmentURI = $fragment->getSubject()->getUri();
		
		$it = $this->rdfModel->find(new Resource($fragmentURI), new Resource($containsPredicate), NULL)->getStatementIterator();
		
		while ($it->hasNext()) {
			$props = array();
			$s = $it->next();
			$item_uri = $s->getObject()->getUri();
			$ita = $this->rdfModel->find($s->getObject(), NULL, NULL)->getStatementIterator();
			
			$predCount = 0;
			
			while ($ita->hasNext() && $predCount<=5) {
				$a = $ita->next();

				/* check, if labelobject is an uri */ 
				$target = $this->rdfModel->find(new Resource($a->getLabelObject()), NULL, NULL)->getStatementIterator();
				if($target->hasNext()) $isUri = true;
				else $isUri = false;
				
				if($isUri) {
					$m = $this->rdfModel->find(new Resource($a->getLabelObject()), NULL, NULL); 
					//$result = ModelFactory::getDefaultModel();
					$iter = $m->getStatementIterator();
					while ($iter->hasNext()) {
						$s = $iter->next();
						if (is_a($s->getObject(), "Literal")) {
							$obj = $s->getLabelObject();
							break;
						}
					}	
				} else $obj = trim(strip_tags($a->getLabelObject()));
				if (!empty($obj)) {
					//if(!is_array($props[$a->getPredicate()->getUri()])) $props[$a->getPredicate()->getUri()] = array();
					if(!array_key_exists($a->getPredicate()->getUri(), $props)) $props[$a->getPredicate()->getUri()] = array();
					array_push($props[$a->getPredicate()->getUri()], $obj);
				}	
				
				$predCount++;
			}			
			$res[$item_uri] = $props;
		}
		return $res;	
		
	
	}
	
	
	
	
	
	
	/* SACH */
	
	public function findResourcesByUri($resUri) {
	
		$res = array();
	
		$it = $this->rdfModel->find(new Resource($resUri), NULL, NULL)->getStatementIterator();

		while ($it->hasNext()) {
			$props = array();
			$s = $it->next();
			$item_uri = $s->getSubject()->getUri();
			$ita = $this->rdfModel->find($s->getSubject(), NULL, NULL)->getStatementIterator();
			
			while ($ita->hasNext()) {
				$a = $ita->next();

				/* check, if labelobject is an uri */ 
				$target = $this->rdfModel->find(new Resource($a->getLabelObject()), NULL, NULL)->getStatementIterator();
				if($target->hasNext()) $isUri = true;
				else $isUri = false;
				
				if($isUri) {
					$m = $this->rdfModel->find(new Resource($a->getLabelObject()), NULL, NULL); 
					//$result = ModelFactory::getDefaultModel();
					$iter = $m->getStatementIterator();
					while ($iter->hasNext()) {
						$s = $iter->next();
						if (is_a($s->getObject(), "Literal")) {
							$obj = $s->getLabelObject();
							break;
						}
					}	
				} else $obj = trim(strip_tags($a->getLabelObject()));
				//if (!empty($obj)) $props[$a->getPredicate()->getUri()] = $obj;
				if (!empty($obj)) {
					if(!array_key_exists($a->getPredicate()->getUri(), $props)) $props[$a->getPredicate()->getUri()] = array();
					array_push($props[$a->getPredicate()->getUri()], $obj);
				}	
			}			
			$res[$item_uri] = $props;
		}
		return $res;	
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	public function isUri($target_label) {	
		$target = $this->rdfModel->find(NULL, NULL, new Resource($target_label))->getStatementIterator();
		if($target->hasNext()) return true;
		return false;
	}
	*/
	
	
	
	
	
	/** SACH  Connect Ressources */
	public function connectResources ($subject_uri, $predicate_uri, $object_uri) {
		$this->rdfModel->add(new Statement(new Resource($subject_uri), new Resource($predicate_uri), new Resource($object_uri)));
		return true;
	}
	
	
	
	
	
	
	
	/**
	 * Retrieves a single Fragment from RDF Store
	 *
	 * @param  string    $uri  	Fragment URI
	 * @return Fragment		Fragment Object
	 * @return boolean		false if failed
	 */
	public function loadFragment($uri) {
		$queryString = "SELECT ?creatorUri ?cDate ?mDate ?title ?rdfa ?type " .
				"WHERE " .
				"(<" .$uri ."> <" .DC::CREATOR()->getURI() ."> ?creatorUri) " .
				"(<" .$uri ."> <" .DC::CREATED()->getURI() ."> ?cDate) " .
				"(<" .$uri ."> <" .DC::MODIFIED()->getURI() ."> ?mDate) " .
				"(<" .$uri ."> <" .DC::TITLE()->getURI() ."> ?title) " .
				"(<" .$uri ."> <" .LOOMP::RDFA()->getURI() ."> ?rdfa) " .
				"(<" .$uri ."> <" .LOOMP::TYPE()->getURI() ."> ?type) ";
	
		$it = $this->rdfModel->rdqlQueryAsIterator($queryString);
		if ($it->hasNext()) {
			$r = $it->next();
			if (isset($r['?type']) && $r['?type'] != "") $type = $r['?type']->getLabel();
			else $type = "text";
			$class = "Fragment_".$type;
			return new $class($uri, $r['?creatorUri']->getURI(), $r['?cDate']->getLabel(),
					$r['?mDate']->getLabel(), $r['?title']->getLabel(), $r['?rdfa']->getLabel(),$type);
		}
		else {
			return false;
		}
	
	}
	
	
	/**
	 * Removes a single Mashup from RDF Store by deleting all triples describing the mashup URI
	 * as well as all fragments associated with this mashup which are not part of other mashups
	 *
	 * @param Mashup	$mashup		Mashup Object
	 */
	public function removeMashup($mashup) {
		$this->_removeResWithAllProps($mashup->getUri());
		foreach ($mashup->getFragments() as $f) {
			// consistency check
			if ($this->_fragmentCount($f->getUri()) == 0) {
				$this->_removeFragment($f);
				searchDeleteFragment($f);
			}
		}
	}
	

	public function getMashupsForFragment($uri) {		
		$queryString = "SELECT ?mashup " .
				"WHERE " .
				"(?mashup ?p <" .$uri .">) " .
				"(?mashup rdf:type <" .LOOMP::MASHUP()->getURI() .">) ";
		
		$ret = array();		
		$it = $this->rdfModel->rdqlQueryAsIterator($queryString);
		while ($it->hasNext()) {
			$r = $it->next();
			
			$ret[] = $r['?mashup']->getURI();
		}
		
		return $ret;
	}
	
	
	/**
	 * Retrieves all resources from Dbpedia having the same label as $label
	 * ---- NOTICE -----
	 * $qs should look something like this:
	 * $qs = "SELECT ?x, ?comment
	 *	    WHERE {?x rdfs:label ?label .         
         *           FILTER langMatches(lang(?label), '*') .
         *           FILTER (str(?label) = '$label')
         *           ?x rdfs:comment ?comment .
         *           FILTER (lang(?comment) = '$language')
         *   }";
         *
         *  however the dbpedia endpoint returns a timeout after 60 sec. There must be some problems with
         *  the implementation of the str() filter expression
	 * ---- NOTICE -----
	 * @param String	$label
	 * @param String	$language	language tag
	 * @return array	[URI] = rdfs:comment
	 */
	function getResFromDbpedia ($label, $language="en") {
    
	   $c = ModelFactory::getSparqlClient("http://dbpedia.org/sparql");
	   $q = new ClientQuery();

	   $qs = "SELECT ?x, ?comment
		WHERE {{?x rdfs:label '$label'@en}
		UNION  {?x rdfs:label '$label'@de} 
		UNION  {?x rdfs:label '$label'@fr} 
		UNION  {?x rdfs:label '$label'@pl}
		UNION  {?x rdfs:label '$label'@es}
		UNION  {?x rdfs:label '$label'@it}
		UNION  {?x rdfs:label '$label'@nl}
                    ?x rdfs:comment ?comment
                    FILTER (lang(?comment) = '$language')
            }";

	   $q->query($qs);
	   $r=$c->query($q);
        
	   $res = array();
	   foreach($r as $i){
	       $res[$i['?x']->getLabel()] = $i['?comment']->getLabel();
	   }
	   return $res;
	}
	
	function getFragmentsForResource ($uri) {

		$fragments = array();
		/* 
		 $queryString = "SELECT ?fURI " .
		 "WHERE (?fURI rdf:type <" .LOOMP::FRAGMENT()->getURI() .">)" .
		 "(?fURI <" .LOOMP::CONTAINS()->getURI() ."> <" .$uri .">)"; 
		 
		 $it = $rdfModel->rdqlQueryAsIterator($queryString);
		 while ($it->hasNext()) {
		 $r = $it->next();
		 $fragments[] = _loadFragment($r['?fURI']->getURI());
		 } 
		 */
		
		$it = $this->rdfModel->find(NULL, LOOMP::CONTAINS(), new Resource($uri))->getStatementIterator();
		while ($it->hasNext()) {
			$s = $it->next();
			$fragments[] = $this->loadFragment($s->getLabelSubject());
		}
		
		return $fragments;
		
	}
	
	
	// -------------------------------------------------------------
	// helper methods
	// -------------------------------------------------------------
	
	/**
	 * Currently, this method simply overwrites an existing fragment !!!
	 * WARNING: this influences all mashups having integrated this fragment
	 * Possible solution: create a new copy of the fragment, however fragment URIs are currently provided by the loomp client
	 * therefore some modification in the loomp architecture are required
	 *
	 * @param  Fragment    $fragment          Fragment Object
	 */
	private function _saveFragment($fragment) {	
		$this->_removeFragment($fragment);
	
		$fragmentRes = new Resource($fragment->getUri());
	
		$this->rdfModel->add(new Statement($fragmentRes, RDF::TYPE(), LOOMP::FRAGMENT()));
		$this->rdfModel->add(new Statement($fragmentRes, DC::CREATOR(), new Resource($fragment->getCreatorId())));
		$this->rdfModel->add(new Statement($fragmentRes, DC::TITLE(), new Literal($fragment->getTitle())));
		$this->rdfModel->add(new Statement($fragmentRes, DC::CREATED(), new Literal($fragment->getCreateDate())));
		$this->rdfModel->add(new Statement($fragmentRes, DC::MODIFIED(), new Literal($fragment->getModifyDate())));
		$this->rdfModel->add(new Statement($fragmentRes, LOOMP::RDFA(), new Literal($fragment->getSaveContent())));
		$this->rdfModel->add(new Statement($fragmentRes, LOOMP::TYPE(), new Literal($fragment->getType())));
		
		$this->_saveResFromRDFa($fragmentRes,'<html xmlns="http://www.w3.org/1999/xhtml"><body about="'.$fragment->getURI().'">'.$fragment->getSaveContent().'</body></html>');
	}
	
	
	/**
	 * Stores all subjects of RDF triples extracted from the RDFa representation of a given fragment in this format:
	 * fragmentURI LOOMP:contains resourceURI .
	 *
	 * @param  Resource    $fragmentRes          Resource Object
	 * @param  string      $rdfa          		 RDFa representation of a given fragment
	 */
	private function _saveResFromRDFa ($fragmentRes, $rdfa) {
		$parser = ARC2::getSemHTMLParser();
		$parser->parse('', $rdfa);
		$parser->extractRDF('rdfa');
	
		$triples = $parser->getTriples();
	
		$res = array();
		for ($i = 0, $i_max = count($triples); $i < $i_max; $i++) {
	
			$triple = $triples[$i];
			if ($triple['s_type'] == "uri") {
				if (!in_array($triple['s'], $res)) {
	
			  		$res[] = $triple['s'];
		  			$this->rdfModel->add(new Statement($fragmentRes, LOOMP::CONTAINS(), new Resource($triple['s'])));
				}
			}
	
			if ($triple['o_type'] == 'literal') {
				$obj = new Literal($triple['o'], $triple['o_lang']);
				if ($triple['o_datatype']) {
					$obj->setDatatype($triple['o_datatype']);
				}
			}
			else {
				$obj = new Resource($triple['o']);
			}
	
			$this->rdfModel->add(new Statement(new Resource($triple['s']), new Resource($triple['p']), $obj));
		}
	}
	

	/**
	 * Removes a single Fragment from RDF Store by deleting all triples describing the Fragment URI
	 * WARNING: As far as triples extracted from the RDFa representation of a given fragment are concerned,
	 *          this method only removes the following:
	 *			1) fragmentURI loomp:contains sommeURI .
	 *			2) all triples: someURI someProperty someOtherURI  or   someURI someProperty someLiteral
	 *		    If there are other triples further describing someOtherURI they will not be removed, becuase this
	 * 		    requires the analysis of the entire RDF graph, i.e. the paths from someOtherURI to other fragmentURIs.
	 *		    At this point I do not have sufficient test data to play with. --> future work
	 *
	 * @param Fragment	$fragment	Fragment Object
	 */
	private function _removeFragment($fragment) {
	
		$it = $this->rdfModel->find(new Resource($fragment->getUri()), LOOMP::CONTAINS(), NULL)->getStatementIterator();
	
		$this->_removeResWithAllProps($fragment->getUri());
	
		// remove all triples describing a given resource which is not contained in any other fragment
		while ($it->hasNext()) {
			$s = $it->next();
			$obj = $s->getObject();
			if ($this->rdfModel->findCount(NULL, LOOMP::CONTAINS(), $obj) == 0) {
				$this->_removeResWithAllProps($obj->getURI());
			}
		}
	}


	/**
	 * Removes all triples describing the given URI
	 *
	 * @param string	$uri	URI of the RDF resource to be removed
	 */
	private function _removeResWithAllProps ($uri) {

		$it = $this->rdfModel->find(new Resource($uri), null, null)->getStatementIterator();
		while ($it->hasNext()) {
			$s = $it->next();
			$this->rdfModel->remove($s);
		}
	
	}
	
	
	/**
	 * Count all associations of the given fragment uri with mashups from the RDF Store
	 *
	 * @param string	$uri	Fragment URI
	 * @return integer
	 */
	private function _fragmentCount($uri) {
	
		$queryString = "SELECT ?mashup " .
				"WHERE " .
				"(?mashup ?p <" .$uri .">) " .
				"(?mashup rdf:type <" .LOOMP::MASHUP()->getURI() .">) ";
	
		$r = $this->rdfModel->rdqlQuery($queryString);
	
		// this workaround was needed due to a strange behavior of the rdqlDBengine.
		// this solution is stable even if there is no bug in the engine.
		if ((count($r)==1) && ($r[0]['?mashup'] == NULL)) {
			return 0;
		}
		return (count($r));
	}
	
	
	/**
	 * 
	 *
	 * @param string	$uri	Fragment URI
	 * @return array	[]['label']
	 * 			  ['comment]
	 */
	private function _getVocabLabelAndComment($id, $lang) {
		
		$a = array('label' => '', 'comment' => '' );
		$m = ModelFactory::getDefaultModel();
		$m->load($this->annotationsDirPath .$id);

		$it = $m->findAsIterator(NULL, RDF::TYPE(), LOOMP::ANNOTATION_SET());		
		if ($it->hasNext()) {
			$s = $it->next()->getSubject();
			
			$a['label'] = $this->_getLiteralInLang($m->find($s, RDFS::LABEL(), NULL), $lang);			
			$a['comment'] = $this->_getLiteralInLang($m->find($s, RDFS::COMMENT(), NULL), $lang);			
		}
		
		return $a;
		
	}
	
	private function _getLiteralInLang(&$m, $lang) {
		
		$it = $m->getStatementIterator();
		while ($it->hasNext()) {
			
			$o = $it->next()->getObject();
			if ($o->getLanguage() == $lang)
				return $o->getLabel();
				
		}
		return '';
	}
	
	private function _getPropValue(&$m, $s, $p) {
		
		$it = $m->findAsIterator($s, $p, NULL);
		if ($it->hasNext()) {
			
			return $it->next()->getObject()->getLabel();				
		}
		return '';
	}
	
	
	private function _getAnnotations(&$m, $lang) {

		$ann = array();
		
		$it = $m->findAsIterator(NULL, RDF::TYPE(), LOOMP::ANNOTATION());		
		while ($it->hasNext()) {
			$s = $it->next()->getSubject();
			
			$uri = $this->_getPropValue($m, $s, LOOMP::REFERS_TO());
			$label = $this->_getLiteralInLang($m->find($s, RDFS::LABEL(), NULL), $lang);			
			$description = $this->_getLiteralInLang($m->find($s, RDFS::COMMENT(), NULL), $lang);
			$type = $this->_getPropValue($m, $s, LOOMP::ANNOTATION_TYPE());
			$annDomain = $this->_getPropValue($m, $s, LOOMP::ANNOTATION_DOMAIN());
			if ($annDomain == '') {
				$annDomain = RDF::RESOURCE()->getURI();				
			}
			$annRange = $this->_getPropValue($m, $s, LOOMP::ANNOTATION_RANGE());
			
			$ann[] = new Annotation($uri, $label, $description, $type, $annDomain, $annRange);
		}
		
		return $ann;
	}
}

?>
