<?php
/*
 * Created on May 14, 2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - PHPeclipse - PHP - Code Templates
 */
 
abstract class LoompAction extends Zend_Controller_Action {
	protected $loompApi = false;
	protected $dbConn = false;
	protected $log = false;
	protected $translate = false;
	
	protected function getLA() {
		if (!$this->loompApi) {
			$this->loompApi = Zend_Registry::getInstance()->loompApi;
		}
		return $this->loompApi;
	}
	
	protected function getDB() {
		if (!$this->dbConn) {
			$this->dbConn = $this->getLA()->getDbConn();
		}
		return $this->dbConn;
	}
	
	protected function getLog() {
		if (!$this->log) {
			$this->log = Zend_Registry::getInstance()->logger;
		}
		return $this->log;
	}
	
	protected function addFlashMessage($msg) {
		$fm = $this->_helper->FlashMessenger($msg);
  		$this->view->messages = $fm->getMessages();
	}
	
	public function getUserId() {
		return $this->view->user->getUri();
	}
	
	public function getUser() {
		return $this->view->user;
	}
	
	public function getParamNumeric($name, $required = true) {
       	$request = $this->getRequest()->getQuery();
       	if(isset($request[$name]) && is_numeric($request[$name]) && $required) {
  			return @$request[$name];
  		}
  		else {
  			die("Missing required parameter: " . $name . " = [Numeric Value]");
  		}
	}
	
	public function getParamString($name, $required = true) {
       	$request = $this->getRequest()->getQuery();
       	if(isset($request[$name]) && $required) {
  			return @$request[$name];
  		}
  		else {
  			die("Missing required parameter: " . $name . " = [String Value]");
  		}
	}
	
	public function getParamUri($name, $required = true) {
       	$request = $this->getRequest()->getQuery();
       	if(isset($request[$name]) && Zend_Uri::check($request[$name]) && $required) {
  			return @$request[$name];
  		}
  		else {
  			die("Missing required parameter: " . $name . " = [URI]");
  		}
	}
	
	protected function getT() {
		if (!$this->translate) {
			$this->translate = Zend_Registry::getInstance()->translate;
		}
		return $this->translate;
	}
}
 
?>
