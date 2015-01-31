<?php

class Zend_View_Helper_IsActive
{
    public function isActive($controller, $action = "index")
    {
       $req = Zend_Controller_Front::getInstance()->getRequest();
       return ($req->getControllerName() === $controller && 
               $req->getActionName() === $action);
    }
}

?>
