<?php
require_once 'Zend/Controller/Front.php';
 
class Zend_View_Helper_BaseUrl {
 	public function baseUrl($path = '')
	{
		return Zend_Controller_Front::getInstance()->getBaseUrl() . trim($path);
	}
}