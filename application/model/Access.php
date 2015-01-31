<?php
class Model_Access extends ADOdb_Active_Record {
	var $_table = 'access';
	private $mashupInstance = null;
	
	public function findByMashup($mashup) {
		return $this->load("mashup=?",array($mashup));
	}
	
	public function findByKey($key) {
		return $this->load("accesskey=?",array($key));
	}
	
	public function findAll($orderStmt) {
		return $this->find("1=1 order by $orderStmt");
	}
	
    public function setMashup($mashup) {
        $this->mashup = (string) $mashup;
        return $this;
    }

    public function getMashup()  {
        return $this->mashup;
    }
    
    public function setMashupInstance($minst) {
    	$this->mashupInstance = $minst;
    }
    
    public function getMashupInstance() {
    	if ($this->mashupInstance != null) return $this->mashupInstance;
    }

	public function setUseruri($useruri) {
        $this->useruri = (string) $useruri;
        return $this;
    }

    public function getUseruri()  {
        return $this->useruri;
    }
    
	public function setAccesskey($accesskey) {
        $this->accesskey = (string) $accesskey;
        return $this;
    }
    
    public function createAccesskey() {
    	$this->setAccesskey(md5($this->getMashup().microtime().rand(1,time())));
    	return $this;
    }

    public function getAccesskey()  {
        return $this->accesskey;
    }
    
    public function __toString() {
    	return "Access to " . $this->getMashup();
    }
}

?>
