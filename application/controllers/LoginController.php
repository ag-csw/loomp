<?php


class LoginController extends LoompAction {
	public function getLoginForm() {
		require_once APPLICATION_PATH . '/forms/LoginForm.php';
		$form = new LoginForm();
		$form->setAction($this->view->url(array('controller'=>'login','action' => 'loginprocess')));
		$form->getElement('nextf')->setValue(urldecode($this->getRequest()->getParam('next')));
		return $form;
	}
	
	public function getRegisterForm() {
		require_once APPLICATION_PATH . '/forms/RegisterForm.php';
		$form = new RegisterForm();
		$form->setAction($this->_helper->url('registerprocess'));
		//$form->getElement('next')->setValue($this->getRequest()->getParam('next'));
		return $form;
	}

	public function getAuthAdapter(array $params) {
		require_once APPLICATION_PATH . '/auth/AuthAdapter.php';
		return new AuthAdapter($params['username'],$params['password']);
	}

	public function preDispatch() {
		/*if (Zend_Auth :: getInstance()->hasIdentity()) {
			// If the user is logged in, we don't want to show the login form;
			// however, the logout action should still be available
			if ('logout' != $this->getRequest()->getActionName()) {
				$this->_helper->redirector('login', 'index');
			}
		} else {
			// If they aren't, they can't logout, so that action should
			// redirect to the login form
			if ('logout' == $this->getRequest()->getActionName()) {
				$this->_helper->redirector('index');
			}
		}*/
	}

	public function indexAction() {
		$this->view->form = $this->getLoginForm();
	}

	public function loginprocessAction() {
		$request = $this->getRequest();
		// Check if we have a POST request
		if (!$request->isPost()) {
			return $this->_helper->redirector('index');
		}
			
		// Get our form and validate it
		$form = $this->getLoginForm();
		if (!$form->isValid($request->getPost())) {
			// Invalid entries
			$this->view->form = $form;
			$this->getLog()->warn("Login attempt for ".$form->getValue('username')." failed");
			return $this->render('index'); // re-render the login form
		}

		// Get our authentication adapter and check credentials
		$adapter = $this->getAuthAdapter($form->getValues());
		$auth = Zend_Auth :: getInstance();
		$result = $auth->authenticate($adapter);
		if (!$result->isValid()) {
			// Invalid credentials
			$form->setDescription('Invalid credentials provided');
			$this->view->form = $form;
			$this->getLog()->warn("Login attempt for ".$form->getValue('username')." failed");
			return $this->render('index'); // re-render the login form
		}
		
		$this->getLog()->info("Login for ".$form->getValue('username')." successful");
		
		// We're authenticated! Redirect to the home page or to given value
		$next = str_replace(";","/",$form->getValue('nextf'));
		if (!empty($next)) {
			header("Location: " . $next);
			die();
		}
		else {
			$this->_helper->redirector('index', 'home');
		}
	}
	
	public function registerAction() {
		$this->view->form = $this->getRegisterForm();
		$this->getLog()->info("Registration form displayed");
	}
	
	public function registerprocessAction() {
		$request = $this->getRequest();
		// Check if we have a POST request
		if (!$request->isPost()) {
			return $this->_helper->redirector('register');
		}

		// Get our form and validate it
		$form = $this->getRegisterForm();
		if (!$form->isValid($request->getPost())) {
			// Invalid entries
			$this->view->form = $form;
			$this->getLog()->warn("Registration attempt failed");
			return $this->render('register'); // re-render the login form
		}
		$values = $form->getValues();
		
		// generate user uri
		$fullname = $values['firstname']." ".$values['lastname'];
		$uri = $this->getLA()->getUriForTitle($fullname, LOOMP::User());
		$activationkey = md5(microtime().$uri);
		
		// create user account in db
		$user = new Model_User();
		$user->setEmail($values['mail'])
			->setPassword($values['password'])
			->setActivation($activationkey)
			->setActive(false)
			->setUserlevel(Model_User::USER_LEVEL_USER)
			->setUri($uri)
			->setFirstname($values['firstname'])
			->setLastname($values['lastname'])
			->setOrganisation($values['organisation']);

		if (!$user->save()) {
			$this->getLog()->err("Registration for $user failed - " . $user->ErrorMsg());
			return $this->render('register');
		}
		$this->getLog()->info("Registration for $user succeeded");
		
		$server_config = Zend_Registry::getInstance()->configuration->server;
		
		$activation_link = LOOMP_BASE_PATH. 
			$this->view->url(array('controller'=>'login','action' => 'activate'), 'default', true). 
			"?key=" . $activationkey;
			
		// send e-mail to user
		$mail = new Zend_Mail();
		$mail->setBodyHTML("Welcome to LOOMP!\n <a href='$activation_link'>Activate your account by clicking here</a>.");
		$mail->setFrom('loomp@' . $server_config->host, 'LOOMP Registration');
		$mail->addTo($user->getEmail(), $user->getFullname());
		$mail->setSubject("Account Activation for ".$user->getFullname());
		$mail->send();
		
		$this->getLog()->info("Registration mail sent to ".$user->getEmail());
		

		// display thanks page
		$this->view->mail = $user->getEmail();
		// do nothing, thank you page will be rendered
	}
	
	public function activateAction() {
       	$request = $this->getRequest()->getQuery();
       	if(!isset($request['key'])) {
       		$this->getLog()->err("Activation failed");
   			$redirect = new Zend_Controller_Action_Helper_Redirector();
			$redirect->gotoSimple('index', 'index',  null); 
  		}
		
  		$user = new Model_User();
  		if (!$user->activate($request['key'])) {
			$this->getLog()->err("Activation failed for key " . $request['key']);
			$redirect = new Zend_Controller_Action_Helper_Redirector();
			$redirect->gotoSimple('index', 'index',  null); 
		}
		$this->getLog()->info("Activation successful for $user");
	}

	public function logoutAction() {
		Zend_Auth :: getInstance()->clearIdentity();
		$this->getLog()->info("Logout successful");
		$this->_helper->redirector('index','index'); // back to login page
	}
}