<?php
class AdminController extends LoompAction {
	
	const USER_PARAM = "user";
	
	public function preDispatch() { // secure this view
		if (!$this->view->loginStatus || !$this->getUser()->isAdmin()) {
    		$this->getLog()->warn("Unauthorized access to admin area by " . $this->getUser());
    		return $this->_helper->redirector('index','home');
		}
	}

    public function indexAction()  {
		$user = new Model_User();
		$this->view->users = $user->findAll("active,lastname") or
			$this->getLog()->err("Failed to list users: " . $user->ErrorMsg());
		$this->getLog()->debug("Displayed admin home");
    }
    
    public function useractivateAction() {
  		$id = $this->getParamNumeric(AdminController::USER_PARAM);
  		
  		$user = new Model_User();
  		if ($user->findById($id)) {
  			$user->activateAdmin();
  			$user->save();
  			$this->getLog()->info("Activated $user");
  			
  			// send e-mail to user
			$mail = new Zend_Mail();
			$mail->setBodyHTML("Your LOOMP account has been activated!\n <a href='$login_link'>Login to your account</a>.");
			$mail->setFrom('loomp@' . LOOMP_HOST, 'LOOMP Activation');
			$mail->addTo($id['email'], $user->getFullName());
			$mail->setSubject("Account Activation for " . $user->getFullName());
			$mail->send();
			$this->getLog()->info("Sent activation mail to $user");
  		}
    	else {
    		$this->getLog()->err("Failed to activate user $id");
    	}
    	return $this->_helper->redirector('index');
    }
    
    public function userdeactivateAction() {
  		$id = $this->getParamNumeric(AdminController::USER_PARAM);
  		
  		$user = new Model_User();
  		if ($user->findById($id)) {
  			$user->deactivateAdmin();
  			$user->save();
  			$this->getLog()->info("Deactivated $user");
  		}
    	else {
    		$this->getLog()->err("Failed to deactivate user $id");
    	}
    	return $this->_helper->redirector('index');
    }
    
    public function userdeleteAction() {
		$user = new Model_User();
  		if ($user->findById($this->getParamNumeric(AdminController::USER_PARAM))) {
  			$user->delete();
  			$this->getLog()->info("Deleted $user");
  		}
		return $this->_helper->redirector('index');
    }
    
    public function userpromoteAction() {
    	$user = new Model_User();
  		if ($user->findById($this->getParamNumeric(AdminController::USER_PARAM))) {
  			$user->promote();
  			$this->getLog()->info("Promoted $user to admin");
  		}
		return $this->_helper->redirector('index');
    }
    
    public function userdemoteAction() {
    	$user = new Model_User();
  		if ($user->findById($this->getParamNumeric(AdminController::USER_PARAM))) {
  			$user->demote();
  			$this->getLog()->info("Demoted $user");
  		}
		return $this->_helper->redirector('index');
    }
    
    // TODO: Active Records & logging below
    public function viewconfigAction() {
    	$config = Zend_Registry::getInstance()->configuration->formatter;
 
       	$fmt_path = APPLICATION_PATH . "/" . $config->dir ."/";
  		$filesf = scandir($fmt_path);
  		$allconfig = array();
  		foreach ($filesf as $file) {
  			$path = realpath($fmt_path.$file);
  			if (is_dir($path) && is_file($path."/config.ini")) {
  				$carr = parse_ini_file($path."/config.ini",true) or $carr = array();
  				$allconfig[$file] = $carr;
  			}
  		}
  		$this->view->allconfig = $allconfig;
  		
  		$this->view->setData = array();
  		$this->view->setTitle = "";
  		$request = $this->getRequest()->getQuery();
  		if(isset($request['set'])) {
  			$this->view->setTitle = @$request['set'];
  		}

   		// get user settings
   		$conn = $this->getDB();
		$dbsettings = array();
		$recordSet = $conn->Execute("SELECT * FROM `viewconfig` ORDER BY `name`,`fmt`;");
		while (!$recordSet->EOF) {
    		$raw_data = $recordSet->GetRowAssoc(false);
    		$dbsettings[$raw_data['name']][$raw_data['fmt']][$raw_data['key']] = $raw_data['value'];
    		if ($this->view->setTitle == $raw_data['name']) {
    			$this->view->setData[$raw_data['fmt']][$raw_data['key']] = $raw_data['value'];
    		}
    		$recordSet->MoveNext();
		}
		$this->view->allsets = $dbsettings;
    }
    
    public function saveconfigAction () {
    	$request = $this->getRequest();
		// Check if we have a POST request
		if (!$request->isPost()) {
			return $this->_helper->redirector('viewconfig');
		}
		$post = $request->getParams();
		$setTitle = $post['setTitle']; 
		$setData =  $post['setData'];
		if (empty($setTitle)) die("Need Set Title!");
		// delete all from db for this set
		$conn = $this->getDB();
		$dbsettings = array();
		$conn->Execute("DELETE FROM `viewconfig` WHERE `name`=".$conn->qstr($setTitle).";") or die("Unable to delete config.");

		foreach ($setData as $fmt => $values) {
			foreach ($values as $key => $value) {
				$recordSet = $conn->Execute("INSERT INTO `viewconfig` (`name`,`fmt`,`key`,`value`) VALUES(".$conn->qstr($setTitle).",".$conn->qstr($fmt).",".$conn->qstr($key).",".$conn->qstr($value).");") or die("Unable to write config");
			}
		}		
		
		return $this->_helper->redirector('viewconfig');
    }
    
    public function configdeleteAction () {
    	$request = $this->getRequest();
		$post = $request->getQuery();
		$set = $post['set']; 
		$conn = $this->getDB();
		$conn->Execute("DELETE FROM `viewconfig` WHERE `name`=".$conn->qstr($set).";") or die("Unable to delete config.");	
		
		return $this->_helper->redirector('viewconfig');
    }
}
