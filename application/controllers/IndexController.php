<?php
// application/controllers/IndexController.php

/**
 * IndexController is the default controller for this application
 * 
 * Notice that we do not have to require 'Zend/Controller/Action.php', this
 * is because our application is using "autoloading" in the bootstrap.
 *
 * @see http://framework.zend.com/manual/en/zend.loader.html#zend.loader.load.autoload
 */
class IndexController extends LoompAction {
	
	const MASHUP_PARAM = "mashup";
	const FORMATTER_PARAM = "fmt";
	const TEMPLATE_PARAM = "tpl";
	
    public function indexAction()  {
    	require_once("LoginController.php");
    	$this->view->loginForm = LoginController::getLoginForm();
    	$this->view->lastMashups = $this->getLA()->getLastMashups(3);
    }
    
	public function searchAction()  {
		$req = $this->getRequest();
    	$query = trim(urldecode($req->getParam('query')));
    	$page = trim(urldecode($req->getParam('page')));
    	
    	$this->getLog()->info("Displayed search results for $query");	
    	
    	
    	$config = Zend_Registry::getInstance()->configuration->search;
    	$n = $config->resultsperpage;
    	$start = $page * $n;
    	$p = $n+1; // peeking, always ask for one more result, in order to correctly display "next"
    	
    	$m = (!empty($query)) ? searchMashups($query, $start, $p) : $this->getLA()->getAllMashups($start, $p);
    	
    	$this->view->query = $query;
    	$this->view->has_next = sizeof($m) == $p;
    	$this->view->has_prev = $page > 0;
    	$this->view->page = (int)$page;
    	$this->view->mashups = array_slice($m,0,$n);
    }
    
	public function aboutAction() {}
	
	
	public function getFeedbackForm() {
		require_once APPLICATION_PATH . '/forms/FeedbackForm.php';
		$form = new FeedbackForm();
		$form->setAction($this->_helper->url('feedback'));
		return $form;
	}
	
	public function feedbackAction() {
		$form = $this->getFeedbackForm();
		$this->view->form = $form;
		$this->getLog()->debug("Displayed feedback page");	
		
		$request = $this->getRequest();
		// Check if we have a POST request
		if (!$request->isPost()) {
			return; // just show the form
		}

		// Get our form and validate it
		if (!$form->isValid($request->getPost())) {
			// Invalid entries
			$this->view->form = $form;
			return; // re-render the login form
		}
		
		// if we get here, we can send some mail...
		$values = $form->getValues();
		// send e-mail to admin
		$admin = Zend_Registry::getInstance()->configuration->feedback->recipient;
		$mail = new Zend_Mail();
		$mail->setBodyHTML("Feedback about LOOMP:<br>\n ". htmlentities($values['feedback']));
		$mail->setFrom($values['mail'], $values['mail']);
		$mail->addTo($admin, "Loomp Admin");
		$mail->setSubject("Loomp Feedback");
		$mail->send();
		
		$this->getLog()->info("Sent feedback message to ". $admin);	
		
		$this->view->feedbackSent = true;
		// Display some sort of message
		
	}
    
    public function viewAction() {
    	
       	$request = $this->getRequest()->getQuery();
       	
       	$this->_helper->layout->disableLayout();
       	
       	$this->view->singleMashup = false;
       	$this->view->mashupData = false;
       	
       	$config = Zend_Registry::getInstance()->configuration->formatter;
        
        $fmt = isset($_REQUEST[IndexController::FORMATTER_PARAM]) ? 
        	$_REQUEST[IndexController::FORMATTER_PARAM] : $config->default->formatter;
        $tpl = isset($_REQUEST[IndexController::TEMPLATE_PARAM]) ? 
        	$_REQUEST[IndexController::TEMPLATE_PARAM] : $config->default->template;

		// template constants
   		define('TPL_RES_PATH', str_replace("//","/", BASE_URL . "/" . $config->resources . "/" . $fmt . "/"));
   		define('TPL_BASE_PATH',$this->getBaseUri($fmt,$tpl));
   
       	$tpl_path = APPLICATION_PATH . "/" . $config->dir ."/". $fmt;
  		$this->view->addScriptPath($tpl_path);
  		
  		// parse formatter ini file
   		$ini_file = $tpl_path . "/config.ini";
   		$carr = @parse_ini_file($ini_file,true) or $carr = array();
   		
   		// TODO: save mashup -> config somewhere
   		// TODO: enable default config mapping
   		
   		$configset = "default";
   		// get user settings
   		$conn = $this->getDB();
		$dbsettings = array();
		$recordSet = $conn->Execute("SELECT `key`,`value` FROM `viewconfig` WHERE `name`=".$conn->qstr($configset)." AND `fmt`=".$conn->qstr($fmt).";");
		while (!$recordSet->EOF) {
    		$raw_data = $recordSet->GetRowAssoc(false);
    		$dbsettings[$raw_data['key']] = $raw_data['value'];
    		$recordSet->MoveNext();
		}
		
   		foreach ($carr as $citem) {
   			$key = $citem['key'];
   			if (isset($dbsettings[$key])) $val = $dbsettings[$key];
   			else $val = $citem['default'];
   			define($key,$val);
   			// convenience for tpl developers
   			@define(strtoupper($key),$val);
   			@define(strtolower($key),$val);
   		}
       	  	
       	if(isset($request[IndexController::MASHUP_PARAM]) && Zend_Uri::check($request[IndexController::MASHUP_PARAM])) {
  			$uri = $request[IndexController::MASHUP_PARAM];
	  		$m = $this->getLA()->loadMashup($uri);
	  		$this->view->singleMashup = true;
	  		if (!$m) {
	  			die("Mashup not found: " . $uri);
	  		}
  			$this->getLog()->info("Displayed mashup $uri on formatter $fmt with template $tpl");	
	  		
  		}
  		else {
  			$m = $this->getLA()->getAllMashups();
  			$this->getLog()->info("Displayed all mashups on formatter $fmt with template $tpl");	
  			
  			$this->view->singleMashup = false;
  		}
  		$this->view->mashupData = $m;
  		
  		print $this->view->render($tpl . "." . $config->ext);
  		die();
    }
    
    public static function getDataUri($uri) {
    	return $uri;
    }
    
    public function getBaseUri($fmt,$tpl) {
    	return $this->view->url(array('controller'=>'index','action' => 'view'))."?fmt=".$fmt."&amp;tpl=".$tpl;
    }
}
