<?php
class AuthPlugin extends Zend_Controller_Plugin_Abstract {
	public function preDispatch(Zend_Controller_Request_Abstract $request) {
		if (!Zend_Auth :: getInstance()->hasIdentity() 
			&& !in_array($this->getRequest()->getControllerName(), array('index','login','api','install','error','test'))) {
			$redirect = new Zend_Controller_Action_Helper_Redirector();
			$redirect->gotoSimple('index', 'login',  null,
                                       array('next' => str_replace("/",";",$this->getRequest()->getRequestUri()))
                                 );
		}
		if (Zend_Auth :: getInstance()->hasIdentity()) {
			$view = Zend_Layout::getMvcInstance()->getView();
			$view->loginStatus = true;
			$view->user = Zend_Auth :: getInstance()->getIdentity();
		}
	}
}