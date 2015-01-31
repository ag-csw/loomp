<?php

class Zend_View_Helper_Fullname
{
    public function fullname($uri)
    {
        $conn = Zend_Registry::getInstance()->loompApi->getDBConn();
		$recordSet = $conn->Execute("SELECT email, firstname, lastname FROM users 
											WHERE `uri`=".$conn->qstr($uri).";");
		if ($recordSet && $recordSet->rowCount() == 1) {
			list($email, $firstname,$lastname) = $recordSet->fields;
			if (strlen($firstname) > 0 && strlen($lastname) > 0)
				return $firstname . " " . $lastname;
			else return $email;
		}
		else return basename($uri);
    }
}

?>
