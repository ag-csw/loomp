<?php

class HomeController extends LoompAction {
	
	const MASHUP_PARAM = "mashup";
	
	public function getAccountUpdateForm() {
		require_once APPLICATION_PATH . '/forms/AccountUpdateForm.php';
		$form = new AccountUpdateForm();
		$form->setAction($this->_helper->url('accountupdate'));
		//$form->getElement('next')->setValue($this->getRequest()->getParam('next'));
		return $form;
	}
	
	
    public function indexAction() {
       $this->view->mashups = $this->getLA()->getMashupsForUser($this->getUserId());
       $this->getLog()->info("User home displayed");
    }
    
    public function accountAction() {
    	$form = $this->getAccountUpdateForm();
    	$user = $this->getUser();
    	$user->update();
    	
    	$data = array(
			'firstname' => $user->getFirstname(),
			'lastname' => $user->getLastname(),
			'organisation' => $user->getOrganisation()
    			);
    	$form->setDefaults($data);
    	
    	if (!isset($this->view->accountupdateform)) $this->view->accountupdateform = $form;
    	
    	$this->view->access = array();
    	$this->view->mashups = array();
    	$m_list = array();
    	$m_raw = $this->getLA()->getMashupsForUser($this->getUserId());
    	foreach ($m_raw as $m) {
    		$m_list[$m->getUri()] = $m;
    	}

    	$conn = $this->getDB();
    	$access = new Model_Access();
    	$list = $access->findAll("mashup");
		foreach ($list as $ac) {
    		if (isset($m_list[$ac->getMashup()])) {
    			$ac->setMashupInstance($m_list[$ac->getMashup()]);   			
    			$this->view->access[] = $ac;
    		}
		}
		foreach ($m_list as $m) {
			if (!isset($this->view->access[$m->getUri()])) $this->view->mashups[] = $m;
		}
    	$this->getLog()->info("User account page displayed");
    	
    }
    
    public function accountupdateAction() {
		$request = $this->getRequest();
		// Check if we have a POST request
		if (!$request->isPost()) {
			return $this->_helper->redirector('account');
		}

		// Get our form and validate it
		$form = $this->getAccountUpdateForm();
		if (!$form->isValid($request->getPost())) {
			// Invalid entries
			$this->view->accountupdateform = $form;
			$this->getLog()->warn("Account details update attempt failed");
			$this->addFlashMessage($this->getT()->_("Please fill out all marked fields in the form"));
			$this->accountAction();
			return $this->render('account');
		}
		$values = $form->getValues();

		$user = $this->getUser();
		$user->setFirstname($values['firstname'])
			->setLastname($values['lastname'])
			->setOrganisation($values['organisation'])
			->setPassword($values['password']);
		if (!$user->save()) {
			$this->getLog()->err("Account update failed for ".$this->getUserId()." failed");
			return $this->_helper->redirector('account');
		}
		
		$this->getLog()->info("Account update for ".$this->getUserId()." succeeded");
		$this->addFlashMessage($this->getT()->_("Account updated."));
		$this->accountAction();
		return $this->render('account');
		// do nothing, thank you page will be rendered
	}
    
    public function externalcreateAction() {
		$muri = $this->getRequest()->getParam(HomeController::MASHUP_PARAM);
		if(!Zend_Uri::check($muri)) {
			$this->getLog()->err("Access granting failed: " . $muri . " malformed");
  			return $this->_helper->redirector('account');
  		}
  		
  		// check if mashup exists & belongs to current user
  		$m = $this->getLA()->loadMashup($muri) or die('Mashup not found: ' . $muri);
  		if ($m->getCreatorId() != $this->getUserId() && $this->getUser()->isAdmin()) {
  			$this->getLog()->err("User ".$this->getUserId()." has no rights on " . $muri);
			return $this->_helper->redirector('account');
  		}
  		
  		// insert into auth table		
  		$access = new Model_Access();
  		$access->setMashup($muri)
  			->setUseruri($this->getUser()->getUri())
  			->createAccesskey();
  			
		if (!$access->save()) {
			$this->getLog()->err("ERROR creating access key: " . $conn->ErrorMsg());
			return $this->_helper->redirector('account');
		}
		$this->getLog()->info("Created access rights to " . $muri);
		return $this->_helper->redirector('account');
    }
    
    public function externaldeleteAction() {
		$muri = $this->getRequest()->getParam(HomeController::MASHUP_PARAM);
		if(!Zend_Uri::check($muri)) {
  			$this->getLog()->err("Access granting failed: " . $muri . " malformed");
  			return $this->_helper->redirector('account');
  		}

		// check if mashup exists & belongs to current user
  		$m = $this->getLA()->loadMashup($muri) or die('Mashup not found: ' . $muri);
  		if ($m->getCreatorId() != $this->getUserId() && $this->getUser()->isAdmin()) {
			$this->getLog()->err("User ".$this->getUserId()." has no rights on " . $muri);
			return $this->_helper->redirector('account');  		}

  		// delete from auth table	
  		$access = new Model_Access();
  		
		if (!$access->findByMashup($muri) || !$access->delete()) {
			$this->getLog()->err("Failed to remove access key: " . $access->ErrorMsg());
			return $this->_helper->redirector('account');
		}
		$this->getLog()->info("Removed access key to " . $muri);
		return $this->_helper->redirector('account');
    }
   
}
