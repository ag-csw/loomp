<?php

class ApiController extends LoompAction 
{
	const KEY_PARAM = 'key';
	const APPEND_TITLE_PARAM = 'title';
    const APPEND_CONTENT_PARAM = 'content';
	const SPARQL_QUERY_PARAM = 'query';
	
    public function indexAction() 
    {
  		$api_actions = array('get','append');
  		$this->sendError($this->getUsageStr("(".implode(" | ",$api_actions).")")) ;
    }
    
    public function appendAction() {
    	$m = $this->getMashup();

    	$title = $this->getParamString(ApiController::APPEND_TITLE_PARAM);
    	$content = $this->getParamString(ApiController::APPEND_CONTENT_PARAM);
    	
    	$datestr = gmdate("Y-m-d\TH:i:s\Z");
    	$f = new Fragment_text($this->getLA()->getURIForTitle($title, LOOMP::FRAGMENT()),$m->getCreatorId(), $datestr, $datestr, $title, $content,'text');
    	
    	$m->addFragment($f);
    	
    	$this->getLA()->saveMashup($m) or $this->sendError("Failed to save Mashup " . $m->getUri());
    	$this->getLog()->info("Append action on mashup ". $m->getUri());
    	print "success";
    }
    
    public function getAction() {
    	$m = $this->getMashup();
    	print json_encode($m);
    	$this->getLog()->info("Get action on mashup ". $m->getUri());
    }
    
    public function sparqlAction() {
		$query = $this->getParamString(ApiController::SPARQL_QUERY_PARAM);
		
		$model = Zend_Registry::getInstance()->rdfModel;
		$ret = "";
		try {
			$result = $model->sparqlQuery($query);
			$this->_helper->json->sendJson($result);
			$this->getLog()->info("SPARQL query executed");	
		}
		catch (Exception $e) {
			$this->getLog()->info("Error on SPARQL query: ". $e->getMessage());	
			die("### Error for SPARQL query '".$query."'. : ".$e->getMessage()." ### ");
		}
    }
    
    private function getMashup() {
    	$request = $this->getRequest()->getQuery();
    	$key = @$request[ApiController::KEY_PARAM];
    	
    	// disable templating etc
    	$this->_helper->viewRenderer->setNoRender();
    	$this->_helper->layout->disableLayout();
    		
    	// check if a access right exists
    	$access = new Model_Access();
    	$access->findByKey($key) or $this->sendError("Failed to find a accessible Mashup for key $key");
    	
    	$m = $this->getLA()->loadMashup($access->getMashup()) or $this->sendError("Failed to load mashup " . $access->getMashup());
    	return $m;
    }
    
    private function getUsageStr($component) {
    	return "Usage: .../api/".$component."?" . 
  			ApiController::KEY_PARAM . "=[Mashup Access Key]";
    }
	
	private function sendError($text) {
		header("HTTP/1.1 500 Internal Server Error (".$text.")");
		print "Error: " . $text;
		$this->getLog()->err("Error on API request: ". $text);	
		die();
	}
}
