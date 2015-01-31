<?php
//error_reporting(E_ALL ^ (E_STRICT | E_DEPRECATED));
ini_set('display_errors', false);
ini_set('short_open_tags', true);

setlocale(LC_ALL, "en_US.UTF-8");

date_default_timezone_set("Europe/Berlin"); 
// Step 1 .. 3 in file application/initialize.php
require '../application/initialize.php';

// Step 4: DISPATCH:  Dispatch the request using the front controller.
// The front controller is a singleton, and should be setup by now. We 
// will grab an instance and call dispatch() on it, which dispatches the
// current request.
Zend_Controller_Front::getInstance()->dispatch();
