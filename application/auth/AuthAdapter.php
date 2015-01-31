<?php
class AuthAdapter implements Zend_Auth_Adapter_Interface {
	private $user;
	private $pass;
	/**
	 * Sets username and password for authentication
	 *
	 * @return void
	 */
	public function __construct($username, $password) {
		$this->user = $username; // may also be e-mail-address
		$this->pass = $password;
	}

	/**
	 * Performs an authentication attempt
	 *
	 * @throws Zend_Auth_Adapter_Exception If authentication cannot
	 *                                     be performed
	 * @return Zend_Auth_Result
	 */
	public function authenticate() {
		$u = new Model_User();
		
		if ($u->findByUser($this->user) &&
			$u->checkPassword($this->pass) && 
			$u->isActive()) {
				
			return new Zend_Auth_Result(Zend_Auth_Result :: SUCCESS, $u);
		}
		else
			return new Zend_Auth_Result(Zend_Auth_Result :: FAILURE_CREDENTIAL_INVALID, $this->user);
	}
}