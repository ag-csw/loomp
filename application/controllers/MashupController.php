<?php
// application/controllers/MashupController.php

class MashupController extends LoompAction 
{
	const MASHUP_PARAM = "mashup";
	
	const MASHUP_META = "mashup_meta";
	const MASHUP_DATA = "mashup_data";
	
	const USER_PARAM = "user";
	const QUERY_PARAM = "query";
	const EMPTY_RESPONSE = "no_result";
	
	const TYPE_PARAM = "type";
	const VALUE_PARAM = "value";	
	
	/** SACH */
	const RESLABEL_PARAM = "reslabel";
	const SUBJ_URI_PARAM = "subjuri";
	const OBJ_URI_PARAM = "objuri";
	const PRED_PARAM = "predicate";
	const ANNOTURI_PARAM = "annoturi";

	
    public function indexAction() {
       	$uri = $this->getRequest()->getParam(MashupController::MASHUP_PARAM,"new");
       	if(!Zend_Uri::check($uri) && $uri != "new") {
  			$this->sendError("Usage: ?" . MashupController::MASHUP_PARAM . "=[URI]");
  		}
  		$this->view->uri = $uri;
  		$this->view->userUri = $this->getUserId();
  		$this->view->vocabs = array();
  		$this->view->vocabIds = array();
  		$vocabs = $this->getLA()->getVocabularies();
  		$fv = array();
  		foreach ($vocabs as $v) {
  			$fv[] = $this->getLA()->getVocabulary($v['ID']);
  		}
  		$this->view->vocabs = Zend_Json::encode($fv);
  		$this->getLog()->info("Editor page for $uri displayed");
  		
    }
    
    public function deleteAction() {
    	$uri = $this->getRequest()->getParam(MashupController::MASHUP_PARAM,false);
       	if(!$uri || !Zend_Uri::check($uri)) {
  			$this->sendError("Usage: ?" . MashupController::MASHUP_PARAM . "=[URI]");
  		}

  		$m = $this->getLA()->loadMashup($uri) or $this->sendError("Mashup " . $uri . " not found!");
  		if ($m->getCreatorId() == $this->getUserId() || $this->view->user['type'] == "admin") {
  			$this->getLA()->removeMashup($m);
  			$this->getLog()->info("Mashup " . $m->getUri(). " deleted");
  		} // TODO: Admin is allowed to delete all
  		$this->_helper->redirector('index', 'home');
    }
    
    public function loadAction() {
    	$this->disableLayout();    	
    	$uri = $this->getRequest()->getParam(MashupController::MASHUP_PARAM,false);        
   		if(!$uri || !Zend_Uri::check($uri)) {
  			$this->sendError("Usage: ?" . MashupController::MASHUP_PARAM . "=[URI]");
  		}
  		
  		$m = $this->getLA()->loadMashup($uri);
		if ($m) {
			$this->getLog()->info("Mashup " . $m->getUri() . " loaded");
			$this->_helper->json->sendJson($m);			
		}
		else {
			$this->sendError("Mashup " . $uri . " not found.");
		}
	}
	
	public function saveAction() {
		$userid = $this->getUserId();
		
		$datestr = gmdate("Y-m-d\TH:i:s\Z");
		
    	$this->disableLayout();
    	$mashupData = $this->getRequest()->getParam(MashupController::MASHUP_DATA,false);
		$mashupMeta = $this->getRequest()->getParam(MashupController::MASHUP_META,false);
		
    	if(!$mashupData || !$mashupMeta) {
  			$this->sendError("Usage: POST me some data in " . MashupController::MASHUP_META . " and " . MashupController::MASHUP_DATA);
  		}
  		
  		$mashupUri = $mashupMeta['uri'];
  		$updateMode = false;
  		if (!empty($mashupUri)) { // existing mashup
  			// load it
  			$m = $this->getLA()->loadMashup($mashupUri);
  			if ($m == null) $this->sendError("Failed to load mashup " . $mashupUri);
  			$m->setTitle($mashupMeta['title']); // change title
  			$m->setModifyDate($datestr);
  			$updateMode = true;
  		}
  		else { // new mashup
  			$m = new Mashup($this->getLA()->getURIForTitle($mashupMeta['title'], LOOMP::MASHUP()), $userid, $datestr, $datestr, $mashupMeta['title'], array());
  		}
  		// rights check
  		if ($updateMode && $m->getCreatorId() != $this->getUserId() && $this->view->user['type'] != "admin")
  			$this->sendError("You have no rights to edit " . $m->getUri());
  		
  		$fragments = array();
  		// add fragments
  		foreach ($mashupData as $fragment) {
  			$fragmentUri = @$fragment['uri'];
  			if (!empty($fragmentUri)) { // existing fragment
  				$f = $this->getLA()->loadFragment($fragmentUri);
  				if ($f == null) $this->sendError("Failed to find fragment " . $fragmentUri . " in mashup " . $mashupUri);
  				$f->setTitle($fragment['title']);
  				$f->setContent($fragment['content']);
  				$f->setModifyDate($datestr); //2002-05-30T09:30:10Z
  			}
  			else { // new fragment
  				$class = "Fragment_".$fragment['type'];
  				$f = new $class($this->getLA()->getURIForTitle($fragment['title'], LOOMP::FRAGMENT()),$userid, $datestr, $datestr, $fragment['title'], $fragment['content'],$fragment['type']);
  			}
  			$fragments[$fragment['order']] = $f;
  		}
  		$deletedFragments = array();
  		if ($updateMode) {
  			$oldFragments = $m->getFragments();
  			foreach ($oldFragments as $fragment) {
  				if (!in_array($fragment,$fragments)) {
  					$deletedFragments[] = $fragment;
  				}
  			}
  		}
  		$m->setFragments($fragments);
		//print_r($m);
  		$this->getLA()->saveMashup($m, $deletedFragments) or $this->sendError("Failed to save Mashup " . $mashupUri);
  		foreach ($m->getFragments() as $fragment) searchUpdateFragment($fragment);
		// print preview link for usage in frontend
		if ($updateMode) $this->getLog()->info("Mashup " . $m->getUri() . " updated");
		else 			 $this->getLog()->info("Mashup " . $m->getUri() . " created");
		
		print $m->getUri();
	}
	
	public function searchAction() {
  	$this->disableLayout();
    $userUri = $this->getRequest()->getParam(MashupController::USER_PARAM,false);
    $query = $this->getRequest()->getParam(MashupController::QUERY_PARAM,false);
    if(!$userUri || !$query || !Zend_Uri::check($userUri)) 
		$this->sendError("Usage: ?" . MashupController::USER_PARAM . "=[User URI]&".MashupController::QUERY_PARAM."=[Search Query]");		
		$m = searchFragmentsForUser($userUri, $query);
		if (count($m) == 0) {
			print MashupController::EMPTY_RESPONSE; 
			$this->getLog()->info("Fragment search for " . $query . " found no results");
			
		}
		if ($m) {
			$this->_helper->json->sendJson(array_slice($m,0,10));
  			$this->getLog()->info("Fragment search for " . $query . " found some results");
		}
	}
	
	public function createresourceAction() {
    	$this->disableLayout();
		$type = $this->getRequest()->getParam(MashupController::TYPE_PARAM,false);
   		$value = $this->getRequest()->getParam(MashupController::VALUE_PARAM,false);

   		if(!$type || !$value)
  			$this->sendError("Usage: ?" . MashupController::TYPE_PARAM . "=[type]&" . MashupController::VALUE_PARAM . "=[value]");
  
		// ignoring type, just use "resource"
  		$uri = $this->getLA()->getUriForTitle($value,new Resource("resource"));
  		$this->getLA()->createResource($uri,$type);
  		$this->getLog()->info("Resource ".$uri." with type ".$type." created");
  		
  		print $uri;
	}
	
	public function getresourcesAction() {
    	$this->disableLayout();
		$type = $this->getRequest()->getParam(MashupController::TYPE_PARAM,false);
		$query = $this->getRequest()->getParam(MashupController::QUERY_PARAM,false);
		
   		if(!$type) 
   			$this->sendError("Usage: ?" . MashupController::TYPE_PARAM . "=[type]&" . MashupController::QUERY_PARAM . "=[query]");
  		
  		$this->getLog()->info("Resource search for " . $query . " with type ".$type);
  		print json_encode($this->getLA()->getResourcesAndProperties($type));
	}
	
	
	
	/** SACH */
	public function getmatchingresAction() {
    	$this->disableLayout();
		$type = $this->getRequest()->getParam(MashupController::TYPE_PARAM,false);
		$query = $this->getRequest()->getParam(MashupController::QUERY_PARAM,false);
		//$reslabel = $this->getRequest()->getParam(MashupController::RESLABEL_PARAM,false);
		//$subject_uri = $this->getRequest()->getParam(MashupController::SUBJ_URI_PARAM,false);
		
   		if(!$type) 
   			$this->sendError("Usage: ?" . MashupController::TYPE_PARAM . "=[type]&" . MashupController::QUERY_PARAM . "=[query]");
  		
  		$this->getLog()->info("Resource search for " . $query . " with type ".$type);
  		print json_encode($this->getLA()->allMatchingResourcesByType($type));
	}
	
	
		/** SACH */
	public function resourcebypredandlabelAction() {
    	$this->disableLayout();
		$pred = $this->getRequest()->getParam(MashupController::PRED_PARAM,false);
		//$query = $this->getRequest()->getParam(MashupController::QUERY_PARAM,false);
		$reslabel = $this->getRequest()->getParam(MashupController::RESLABEL_PARAM,false);
		//$subject_uri = $this->getRequest()->getParam(MashupController::SUBJ_URI_PARAM,false);
		
   		if(!$pred || !$reslabel) 
   			$this->sendError("Usage: ?" . MashupController::PRED_PARAM . "=[predicate]&" . MashupController::RESLABEL_PARAM . "=[reslabel]");
  		
  		$this->getLog()->info("Resource search for " . $reslabel . " with predicate ".$pred);
  		print json_encode($this->getLA()->findResourcesByPredicateAndLabel($pred, $reslabel));
	}
	
	
	
	/* SACH */
	public function resourceinfragmentAction() {
    	$this->disableLayout();
		$annotUri = $this->getRequest()->getParam(MashupController::ANNOTURI_PARAM,false);
		
   		if(!$annotUri) 
   			$this->sendError("Usage: ?" . MashupController::ANNOTURI_PARAM . "=[annoturi]");
  		
  		$this->getLog()->info("Search for resources in fragment of anot " . $annotUri);
  		print json_encode($this->getLA()->findResourcesInFragment($annotUri));
	}
	
	
	
	
	/* SACH*/
	public function resourcebyuriAction() {
    	$this->disableLayout();
		//$pred = $this->getRequest()->getParam(MashupController::PRED_PARAM,false);
		//$query = $this->getRequest()->getParam(MashupController::QUERY_PARAM,false);
		//$res = $this->getRequest()->getParam(MashupController::RESLABEL_PARAM,false);
		$subject_uri = $this->getRequest()->getParam(MashupController::SUBJ_URI_PARAM,false);
		
   		if(!$subject_uri) 
   			$this->sendError("Usage: ?" . MashupController::SUBJ_URI_PARAM . "=[subjuri]");
  		
  		$this->getLog()->info("Resource search for " . $subject_uri);
  		print json_encode($this->getLA()->findResourcesByUri($subject_uri));
	}
	
	
	
	
	/** SACH */
	public function connectresAction() {
    	$this->disableLayout();
		$subject_uri = $this->getRequest()->getParam(MashupController::SUBJ_URI_PARAM,false);
		$type = $this->getRequest()->getParam(MashupController::TYPE_PARAM,false);
   		$object_uri = $this->getRequest()->getParam(MashupController::OBJ_URI_PARAM,false);
		
   		if(!$type || !$subject_uri || !$object_uri)
  			$this->sendError("Usage: ?" . MashupController::SUBJ_URI_PARAM . "=[subjuri]&" . MashupController::TYPE_PARAM . "=[type]&" . MashupController::OBJ_URI_PARAM . "=[objuri]");
  
		// ignoring type, just use "resource"
  		//$uri = $this->getLA()->getUriForTitle($value,new Resource("resource"));
  		$this->getLA()->connectResources($subject_uri, $type, $object_uri);
  		$this->getLog()->info("Resources - Subject ".$subject_uri." connected with Object " .$object_uri. "using pradicate type ".$type);
  		
  		print "done"; 
	}
	
		
	
	
	
	public function getforeignresAction() {
    	$this->disableLayout();
    	$query = $this->getRequest()->getParam(MashupController::QUERY_PARAM,false);
    	        
   		if(!$query) 
  			$this->sendError("Usage: ?" . QUERY_PARAM . "=[label query]");
  			
  		$this->getLog()->info("Foreign resources search for " . $query);
  		print json_encode($this->getLA()->getResFromDbpedia($query));
	}
	
	function sendError($text) {
		$this->getLog()->err("MashupController Error: " . $text);
		header("HTTP/1.1 500 Internal Server Error");
		print $text;
		die();
	}
	
	private function disableLayout() {
		// disable layouting etc
    	$this->_helper->viewRenderer->setNoRender();
    	$this->_helper->layout->disableLayout();
	}
}
