<?php
class Model_User extends ADOdb_Active_Record {
	var $_table = 'users';
 	const USER_LEVEL_USER = 1;
	const USER_LEVEL_ADMIN = 10;


	public function findById($id) {
		return $this->load("id=" . $id);
	}
	
	public function findByUser($username) {
		return $this->load("email=?",array($username));
	}

	public function findByActivation($key) {
		return $this->load("activation=?",array($key));
	}
	
	public function findAll($orderStmt) {
		return $this->find("1=1 order by $orderStmt");
	}

    public function setId($id) {
        $this->id = (int) $id;
        return $this;
    }

    public function getId()  {
        return $this->id;
    }

    public function setEmail($email) {
        $this->email = (string) $email;
        return $this;
    }

    public function getEmail() {
        return $this->email;
    }
    
    public function setRegistered($registered) {
        $this->registered = $registered;
        return $this;
    }

    public function getRegistered()  {
        return $this->registered;
    }

	public function setPassword($password) {
        $this->password = md5($password);
        return $this;
    }

    private function getPassword()  {
        return $this->password;
    }
    
    public function checkPassword($a_password) {
    	return $this->getPassword() == md5($a_password);
    }
    
    public function setActivation($activation) {
        $this->activation = (string) $activation;
        return $this;
    }

    public function getActivation()  {
        return $this->activation;
    }
    
    public function checkActivation($a_key) {
    	return $this->getActivation() == $a_key;
    }
    
    public function activate($a_key) {
    	if (!$this->findByActivation($a_key)) return false;
    	if ($this->isActive()) return false;
   
    	$this->setActive(true);
    	$this->setActivation("");
    	return $this->save();
    }
    
    public function activateAdmin() {
    	if ($this->isActive()) return false;
   
    	$this->setActive(true);
    	$this->setActivation("");
    	return $this->save();
    }
    
    public function deactivateAdmin() {
    	if (!$this->isActive()) return false;
   
    	$this->setActive(false);
    	return $this->save();
    }
    
    public function setActive($active) {
        $this->active = (bool) $active;
        return $this;
    }

    public function getActive()  {
        return $this->active;
    }
    
    public function isActive() {
    	return ($this->getActive() == true);
    }
    
    public function setUserlevel($userlevel) {
        $this->userlevel = (int) $userlevel;
        return $this;
    }
    
    public function promote() {
    	$this->setUserlevel(Model_User::USER_LEVEL_ADMIN);
    	return $this->save();
    }
    
    public function demote() {
    	$this->setUserlevel(Model_User::USER_LEVEL_USER);
    	return $this->save();
    }

    public function getUserlevel()  {
        return $this->userlevel;
    }
    
    public function isAdmin() {
    	return $this->getUserlevel() == Model_User::USER_LEVEL_ADMIN;
    }
    
	public function setUri($uri) {
        $this->uri = (string) $uri;
        return $this;
    }

    public function getUri()  {
        return $this->uri;
    }
    
    public function setFirstname($firstname) {
        $this->firstname = (string) $firstname;
        return $this;
    }

    public function getFirstname()  {
        return $this->firstname;
    }
    
    public function setLastname($lastname) {
        $this->lastname = (string) $lastname;
        return $this;
    }

    public function getLastname()  {
        return $this->lastname;
    }
    
    public function getFullName() {
    	return $this->getFirstname() . " " . $this->getLastname();
    }
    
   	public function setOrganisation($organisation) {
        $this->organisation = (string) $organisation;
        return $this;
    }

    public function getOrganisation()  {
        return $this->organisation;
    }
    
    public function __toString() {
    	return $this->getEmail();
    }
}

?>
