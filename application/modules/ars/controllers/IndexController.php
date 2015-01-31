<?php 
// ars/controllers/IndexController.php

/**
 * IndexController and base entry point of the module
 * ARS (Annotation Recommender Service).
 * 
 * @category   ARS
 * @copyright  Copyright (c) 2009 Patrick Jungermann, Freie Universität Berlin
 * @license    http://www.gnu.org/licenses/lgpl-3.0.html     LGPL, version 3.0
 * @author     Patrick Jungermann
 */ 
class ARS_IndexController extends Zend_Controller_Action 
{
	/**
	 * Only forwards to the base controller {@link ARS_RecommenderController}.
	 * 
	 * @return  void 
	 */
	public function indexAction()
	{
		$this->_forward('index', 'recommender');
	}
}

?>