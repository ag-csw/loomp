<?php
// application/controllers/FragmentController.php

class FragmentController extends LoompAction 
{
    public function indexAction() 
    {
    	die();
    }
    
    public function listAction() 
    {
    	$req = $this->getRequest();
    	$res = trim(urldecode($req->getParam('resource')));
    	
    	$this->_helper->viewRenderer->setNoRender();
    	$this->_helper->layout->disableLayout();
    	
    	$my_api = Zend_Registry::getInstance()->loompApi;
    	$fragments = $my_api->getFragmentsForResource($res);
    	print "<ul>";
    	for($i=0;$i<count($fragments);$i++){
    		print "<li><a href='".$fragments[$i]->uri."'>".$fragments[$i]->uri."</a></li>";
    	}
    	print "</ul>";
    }
    
	function sendError($text) {
		$this->getLog()->err("FrameworkController Error: " . $text);
		header("HTTP/1.1 500 Internal Server Error");
		print $text;
		die();
	}
}